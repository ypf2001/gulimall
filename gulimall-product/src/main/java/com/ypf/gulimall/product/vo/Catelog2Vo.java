package com.ypf.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-12 21:26
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {
    private String catalog1Id;
    private List<cateLog3Vo> catalog3List;
    private String id;
    private String name;
@AllArgsConstructor
@NoArgsConstructor
@Data
    public static class cateLog3Vo {
        private String catalog2Id;
        private String id;
        private String name;

    }
}
