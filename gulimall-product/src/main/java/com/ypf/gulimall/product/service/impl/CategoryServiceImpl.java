package com.ypf.gulimall.product.service.impl;

import com.alibaba.nacos.common.utils.JacksonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ypf.gulimall.product.service.CategoryBrandRelationService;
import com.ypf.gulimall.product.vo.Catelog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ypf.common.utils.PageUtils;
import com.ypf.common.utils.Query;

import com.ypf.gulimall.product.dao.CategoryDao;
import com.ypf.gulimall.product.entity.CategoryEntity;
import com.ypf.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1、查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

        //2、组装成父子的树形结构

        //2.1）、找到所有的一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    //每一个需要缓存的数据要指定缓存名字
    @Cacheable(value = {"category"}, key = "#root.method.name",sync = true)
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(
                new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        log.info("消耗时间：" + (System.currentTimeMillis() - l));
        return categoryEntities;
    }

    //[2,25,225]
    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);

        Collections.reverse(parentPath);


        return parentPath.toArray(new Long[parentPath.size()]);
    }

    /**
     * 级联更新所有关联的数据
     *
     * @param category
     */
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = {"category"}, key = "'getLevel1Categorys'"),
                    @CacheEvict(value = {"category"}, key = "'getCatelogJson'")
            })

    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonWithRedisLock() {
        String uuid = "locked" + UUID.randomUUID();
        //同步加锁和设置过期时间保证原子性
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);
        if (lock) {
            //过期时间必须原子
            redisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            Map<String, List<Catelog2Vo>> catalogJson1 = null;
            try {
                catalogJson1 = getCatelogJsonOrigin();
            } finally {

            }

            String lockUUID = (String) redisTemplate.opsForValue().get("lock");
            if (uuid.equals(lockUUID)) {
                redisTemplate.delete("lock");
            }

            return catalogJson1;
        } else {
            return getCatelogJsonWithRedisLock();
        }
    }

    public Map<String, List<Catelog2Vo>> getCatelogJsonWithRedissonLock() {
        log.info("catelogJson-Lock加锁成功");
        RLock lock = redissonClient.getLock("catelogJson-Lock");
        lock.lock();
        Map<String, List<Catelog2Vo>> catalogJson1 = null;
        try {
            catalogJson1 = getCatelogJsonOrigin();
        } finally {
            lock.unlock();
        }

        String lockUUID = (String) redisTemplate.opsForValue().get("lock");
        return catalogJson1;
    }


    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        String catelogJson = (String) redisTemplate.opsForValue().get("catelogJson");
        if (StringUtils.isEmpty(catelogJson)) {
            log.info("缓存未命中 查询数据库");
            Map<String, List<Catelog2Vo>> catalogJson1 = getCatelogJsonOrigin();
            String s = JacksonUtils.toJson(catalogJson1);
            redisTemplate.opsForValue().set("catelogJson", s);
        }
        log.info("缓存命中");
        Map<String, List<Catelog2Vo>> result = JacksonUtils.toObj(catelogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
        });

        return result;
    }


    public Map<String, List<Catelog2Vo>> getCatelogJsonOrigin() {
        /*
         *只查询一次
         * */
        List<CategoryEntity> selectList = baseMapper.selectList(null);
        //查出1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        //封装数据
        Map<String, List<Catelog2Vo>> collect1 = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getParentCid());
            List<Catelog2Vo> catelog2Vos = null;

            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catelog2Vo catelog2Vo = new Catelog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    List<CategoryEntity> categoryEntities1 = getParent_cid(selectList, l2.getParentCid());
                    if (categoryEntities1 != null) {
                        List<Catelog2Vo.cateLog3Vo> collect = categoryEntities1.stream().map(l3 -> {
                            Catelog2Vo.cateLog3Vo cateLog3Vo = new Catelog2Vo.cateLog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return cateLog3Vo;

                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(collect);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));
        return collect1;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
        return collect;
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;

    }


    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }


}