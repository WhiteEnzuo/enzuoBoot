package com.enzuo.orm.util;

import lombok.Data;

import java.util.List;

/**
 * @Classname Page
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/30 20:43
 * @Created by Enzuo
 */
@Data
public class Page <T>{
    private Integer limit;
    private Integer size ;
    private List<T> list;
}
