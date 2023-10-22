package com.enzuo.orm.makeSql.impl;

import com.enzuo.orm.makeSql.Wrapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.Function;

/**
 * @Classname QueryLamdaWrapper
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 13:04
 * @Created by Enzuo
 */
public class QueryLambdaWrapper<T> implements Wrapper<T> {
    StringBuilder sql = new StringBuilder();
    public Wrapper eq(Function<T, ?> key, Object value) {
        String name = getVariableName(key);
        sql.append(name).append("=").append(value).append(" ");
        return this;
    }

    @Override
    public Wrapper eq(Object key, Object value) {
        return null;
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
        return null;
    }


    public Wrapper like(Function<T,?> key, Object value) {
        sql.append(getVariableName(key)).append("LIKE").append(value).append(" ");
        return this;
    }

    public  String getVariableName(Function<T, ?> function) {

        try {
            Field field = function.getClass().getDeclaredField("val$function");
            field.setAccessible(true);
            return ((Field) field.get(function)).getName();
        } catch (Exception e) {

            return null;

        }
    }
    @Override
    public String toString() {
        return "WHERE " + sql;
    }
}
