package com.ypf.gulimall.product;

import com.ypf.gulimall.product.dao.AttrGroupDao;
import com.ypf.gulimall.product.entity.BrandEntity;
import com.ypf.gulimall.product.service.BrandService;
import com.ypf.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ypf.gulimall.product.service.SkuSaleAttrValueService;
import com.ypf.gulimall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * 1、引入oss-starter
 * 2、配置key，endpoint相关信息即可
 * 3、使用OSSClient 进行相关操作
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallProductApplicationTests {
@Autowired
StringRedisTemplate redisTemplate;
    @Autowired
    BrandService brandService;

@Autowired
    RedissonClient redissonClient;
    @Autowired
    CategoryService categoryService;
@Autowired
    AttrGroupDao attrGroupDao;
@Autowired
SkuSaleAttrValueService skuSaleAttrValueService;
    @Test
    public void testFindPath(){
        Long[] catelogPath = categoryService.findCatelogPath(225L);
        log.info("完整路径：{}",Arrays.asList(catelogPath));
    }


    @Test
    public void contextLoads() {

//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("华为");

//
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功....");

//        brandService.updateById(brandEntity);


        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item) -> {
            System.out.println(item);
        });

    }
    @Test
    public  void testRedis(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //valueOperations.set("hello","world"+ UUID.randomUUID());
        System.out.println(valueOperations.get("hello"));
    }
    @Test
    public  void redissonTest(){
        System.out.println(redissonClient);
    }
    @Test
public  void attrTest(){
        List<SkuItemVo.SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupDao.getAttrGroupWithAttrsBySpuId(3L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);

        System.out.println(skuSaleAttrValueService.getSaleAttrsBySpuId(3L));

    }
}
