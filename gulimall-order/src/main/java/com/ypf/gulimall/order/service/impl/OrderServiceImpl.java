package com.ypf.gulimall.order.service.impl;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ypf.common.utils.R;
import com.ypf.common.vo.MemberRespVo;
import com.ypf.gulimall.order.constant.OrderConstant;
import com.ypf.gulimall.order.dao.OrderItemDao;
import com.ypf.gulimall.order.entity.OrderItemEntity;
import com.ypf.gulimall.order.feign.CartFeignService;
import com.ypf.gulimall.order.feign.MemberFeignService;
import com.ypf.gulimall.order.feign.ProductFeignService;
import com.ypf.gulimall.order.feign.WmsFeignService;
import com.ypf.gulimall.order.interceptor.LoginUserInterceptor;
import com.ypf.gulimall.order.service.OrderItemService;
import com.ypf.gulimall.order.to.OrderCreateTo;
import com.ypf.gulimall.order.vo.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypf.common.utils.PageUtils;
import com.ypf.common.utils.Query;

import com.ypf.gulimall.order.dao.OrderDao;
import com.ypf.gulimall.order.entity.OrderEntity;
import com.ypf.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();
    @Autowired
    MemberFeignService memberFeignService;
    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    ThreadPoolExecutor poolExecutor;
    @Autowired
    WmsFeignService wmsFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignService productFeignService;
    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderItemDao orderItemDao;
    @Autowired
    OrderItemService orderItemService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        CompletableFuture<Void> getAddress = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberRespVo.getId());
            confirmVo.setAddress(address);
            confirmVo.setIntegeration(memberRespVo.getIntegration());
        });

        CompletableFuture<Void> getUserItems = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> currentUserItems = cartFeignService.getCurrentUserItems();
            confirmVo.setItems(currentUserItems);
        }).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(item ->
                    item.getSkuId()
            ).collect(Collectors.toList());
            R hasStock = wmsFeignService.getSkuHasStock(collect);
            List<SkuStockVo> data = (List<SkuStockVo>) hasStock.getData(new TypeReference<List<SkuStockVo>>(){});



            if (data != null) {
                Map<Long, Boolean> collect1 = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(collect1);
            }
        });
        //防重复令牌
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(OrderConstant.PUBLIC_ORDER_TOKEN_PREFIX + memberRespVo.getId(), token, 30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);
        CompletableFuture.allOf(getAddress, getUserItems).get();

        return confirmVo;
    }
    @GlobalTransactional
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
        confirmVoThreadLocal.set(orderSubmitVo);
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        SubmitOrderResponseVo submitOrderResponseVo = new SubmitOrderResponseVo();
        submitOrderResponseVo.setCode(0);
        //返回0 失败和1 成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = orderSubmitVo.getOrderToken();
        Long result = redisTemplate
                .execute(new DefaultRedisScript<Long>(script, Long.class),
                        Arrays.asList(OrderConstant.PUBLIC_ORDER_TOKEN_PREFIX + memberRespVo.getId()),
                        orderToken);

        if (result == 1L) {
            //验证成功
            OrderCreateTo orderCreateTo = createTo();

            BigDecimal payAmount = orderCreateTo.getOrder()==null?new BigDecimal("10"): orderCreateTo.getOrder().getPayAmount();
            BigDecimal payPrice = orderSubmitVo.getPayPrice()==null?new BigDecimal("100"):orderSubmitVo.getPayPrice();
            if(true){
                saveOrder(orderCreateTo);
                //库存锁定
                WareSkuLockVo lockVo = new WareSkuLockVo();
                List<OrderItemVo> collect = orderCreateTo.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(collect);
                R r = wmsFeignService.orderLock(lockVo);
                if(r.getCode()==0){
                    //锁成功
                    submitOrderResponseVo.setOrder(orderCreateTo.getOrder());

                    return submitOrderResponseVo;
                }else{
                    //锁失败
                    submitOrderResponseVo.setCode(1);
                    return submitOrderResponseVo;
                }
            }else{
                submitOrderResponseVo.setCode(3);
                return  submitOrderResponseVo;
            }

        }else {
            submitOrderResponseVo.setCode(2);
            return  submitOrderResponseVo;
        }

    }

    /*
     * @Param: [orderCreateTo]
     * @Return: void
    */
    private void saveOrder(OrderCreateTo orderCreateTo) {
        OrderEntity orderEntity = orderCreateTo.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItems = orderCreateTo.getOrderItems();
        orderItemService.saveBatch(orderItems);

    }

    private OrderCreateTo createTo() {
        OrderCreateTo createTo = new OrderCreateTo();
        String orderSn = IdWorker.getTimeId();
        OrderEntity order = buildOrder(orderSn);
        List<OrderItemEntity> orderItemEntities = buildOrders(orderSn);
        //计算价格
        computePrice(order,orderItemEntities);
        createTo.setOrder(order);
        createTo.setOrderItems(orderItemEntities);

        return createTo;

    }

    private void computePrice(OrderEntity order, List<OrderItemEntity> orderItemEntities) {
        //1订单价格相关
        BigDecimal total = new BigDecimal("0.0");
        for (OrderItemEntity entity:orderItemEntities) {
            BigDecimal decimal = entity.getRealAmount();
            //计算优惠金额
//            BigDecimal couponAmount = entity.getCouponAmount();
//            BigDecimal integrationAmount = entity.getIntegrationAmount();
//            BigDecimal promotionAmount = entity.getPromotionAmount();
            total = total.add(decimal==null? new BigDecimal("0.0"):decimal);

        }
        order.setTotalAmount(total);
        order.setPayAmount(total.add(order.getFreightAmount()));


    }

    private OrderEntity buildOrder(String orderSn) {
        MemberRespVo memberRespVo = LoginUserInterceptor.loginUser.get();
        OrderEntity order = new OrderEntity();
        order.setOrderSn(orderSn);
        order.setMemberId(memberRespVo.getId());
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        //设置运费信息
        order.setFreightAmount(new BigDecimal(10));
        order.setReceiverCity("xx市");
        order.setReceiverName("xxx人");
        order.setReceiverDetailAddress("xx街");
        order.setReceiverPostCode("111111");
        order.setReceiverProvince("xxx省");
        order.setReceiverPhone("12345678912");

        order.setStatus(0);
        order.setAutoConfirmDay(7);
        order.setIntegration(1);
        order.setCouponAmount(new BigDecimal("0.0"));
        order.setGrowth(1);
        return order;
    }

    /*
     * @Param: [orderSn]
     * @Return: java.util.List<com.ypf.gulimall.order.entity.OrderItemEntity>
    */
    private List<OrderItemEntity> buildOrders(String orderSn) {
        List<OrderItemVo> currentUserItems = cartFeignService.getCurrentUserItems();
        if (CollectionUtils.isNotEmpty(currentUserItems)) {
            List<OrderItemEntity> collect = currentUserItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);

                return itemEntity;
            }).collect(Collectors.toList());
            return collect;
        }
      return null;
    }

/*
 * @Param: [orderItemVo]
 * @Return: com.ypf.gulimall.order.entity.OrderItemEntity
*/
    private OrderItemEntity buildOrderItem(OrderItemVo orderItemVo) {
        OrderItemEntity itemEntity = new OrderItemEntity();

        //spu
        Long skuId = orderItemVo.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = (SpuInfoVo) r.getData(new TypeReference<SpuInfoVo>() {});


        itemEntity.setSpuId(data.getId());
        itemEntity.setSpuBrand(data.getBrandId() == null ? "" : data.getBrandId().toString());
        itemEntity.setSpuName(data.getSpuName());
        //sku
        itemEntity.setSkuId(orderItemVo.getSkuId());
        itemEntity.setSkuName(orderItemVo.getTitle());
        itemEntity.setSkuPic(orderItemVo.getImage());
        itemEntity.setSkuPrice(orderItemVo.getPrice());
        String skuAttr = StringUtils.collectionToDelimitedString(orderItemVo.getSkuAttrValues(), ";");
        itemEntity.setSkuQuantity(orderItemVo.getCount());
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setGiftGrowth(orderItemVo.getPrice().intValue());
        itemEntity.setGiftIntegration(orderItemVo.getPrice().intValue());

        //价格
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));
        itemEntity.setRealAmount(new BigDecimal(itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity())).toString()));
        //设置订单状态

        return itemEntity;
    }
}