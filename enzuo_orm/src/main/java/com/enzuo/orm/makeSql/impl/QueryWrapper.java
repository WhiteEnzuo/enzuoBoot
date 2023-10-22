package com.enzuo.orm.makeSql.impl;

import com.enzuo.orm.makeSql.Wrapper;

import java.io.Serializable;

/**
 * @Classname QueryWrapper
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 12:56
 * @Created by Enzuo
 */
public class QueryWrapper<T> implements Wrapper<T> {
    private StringBuilder sql=new StringBuilder();
    @Override
    public Wrapper eq(Object key, Object value) {
        sql.append(key).append("=").append(value).append(" ");
        return this;
    }

    @Override
    public Wrapper or() {
        sql.append(" OR ");
        return this;
    }

    @Override
    public Wrapper and() {
        sql.append(" AND ");
        return this;
    }

    @Override
    public Wrapper like(Object key, Object value) {
        sql.append(key).append("LIKE").append(value).append(" ");
        return this;
    }

    @Override
    public String toString() {
        return "WHERE " + sql;
    }
}
