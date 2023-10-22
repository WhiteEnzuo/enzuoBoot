package com.enzuo.orm.proxy;

import com.enzuo.orm.makeSql.Wrapper;

import java.io.Serializable;
import java.util.List;

/**
 * @Classname BaseMapper
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/17 12:33
 * @Created by Enzuo
 */
public interface BaseMapper<T> {
    List<T> select(Wrapper<T> wrapper);
    List<T> selectOne(Wrapper<T> wrapper);
    List<T> selectById(Serializable id);

    int delete(Wrapper<T> wrapper);

    int deleteById(Serializable id);

    int update(T object,Wrapper<T> wrapper);
    int updateById(T object);

    int insert(T object);
}
