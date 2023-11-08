package com.enzuo.orm.proxy;

import com.enzuo.orm.Test;
import com.enzuo.orm.makeSql.MakeSql;
import com.enzuo.orm.makeSql.Wrapper;
import com.enzuo.orm.makeSql.impl.MysqlMakeSql;
import com.enzuo.orm.makeSql.impl.QueryLambdaWrapper;
import com.enzuo.orm.util.Page;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Classname MapperProxyObject
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 14:29
 * @Created by Enzuo
 */
public class MapperProxyObject implements InvocationHandler {
    private Class<?> clazz;
    private Class<?> baseMapperClass;
    private MakeSql makeSql;

    public MapperProxyObject(Class<?> clazz) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if (genericInterfaces.length == 0) {
            throw new NotInterfaceMapperException("没有继承BaseMapper");
        }
        ParameterizedType genericInterface = (ParameterizedType) genericInterfaces[0];
        Type[] actualTypeArguments = genericInterface.getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            throw new NotInterfaceMapperException("没找到泛型");
        }
        Type actualTypeArgument = actualTypeArguments[0];
        String typeName = actualTypeArgument.getTypeName();
        try {
            this.clazz = Class.forName(typeName);

        } catch (ClassNotFoundException e) {
            throw new NotInterfaceMapperException(e.getMessage());
        }
        baseMapperClass = BaseMapper.class;
        makeSql = new MysqlMakeSql();


    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String sql;
        if (method.equals(baseMapperClass.getDeclaredMethod("select", Wrapper.class))) {
            sql = makeSql.select(clazz, new ArrayList<>(), (Wrapper<?>) args[0]);
        } else if (method.equals(baseMapperClass.getDeclaredMethod("selectById", Serializable.class))) {
            Object id = args[0];
            QueryLambdaWrapper<Object> objectQueryLambdaWrapper = new QueryLambdaWrapper<>();
            objectQueryLambdaWrapper.eq("id",id);
            sql = makeSql.select(clazz, new ArrayList<>(), objectQueryLambdaWrapper);
        } else if (method.equals(baseMapperClass.getDeclaredMethod("selectOne", Wrapper.class))) {
            sql = makeSql.select(clazz, new ArrayList<>(), (Wrapper<?>) args[0]);
        } else if (method.equals(baseMapperClass.getDeclaredMethod("selectByPage", Page.class,Wrapper.class))) {
            Page<?> page = (Page<?>)args[0];
            Wrapper<?> wrapper = (Wrapper<?>) args[1];
            sql = makeSql.selectByPage(clazz,new ArrayList<>(),page,wrapper);
        } else if (method.equals(baseMapperClass.getDeclaredMethod("delete", Wrapper.class))){
            Wrapper<?> wrapper = (Wrapper<?>) args[1];
            sql = makeSql.delete(clazz,wrapper);
        } else if (method.equals(baseMapperClass.getDeclaredMethod("deleteById", Serializable.class))) {
            Object id= args[0];
            QueryLambdaWrapper<Object> objectQueryLambdaWrapper = new QueryLambdaWrapper<>();
            objectQueryLambdaWrapper.eq("id",id);
            sql = makeSql.delete(clazz,objectQueryLambdaWrapper);
        }  else if (method.equals(baseMapperClass.getDeclaredMethod("update", Object.class, Wrapper.class))) {
            Object obj = args[0];
            Wrapper<?> wrapper = (Wrapper<?>) args[1];
            sql = makeSql.update(obj, wrapper);
        }else if (method.equals(baseMapperClass.getDeclaredMethod("updateById", Object.class))) {
            Object obj = args[0];
            Field id = clazz.getDeclaredField("id");
            QueryLambdaWrapper<Object> objectQueryLambdaWrapper = new QueryLambdaWrapper<>();
            objectQueryLambdaWrapper.eq("id", id.get(obj));
            sql = makeSql.update(obj,objectQueryLambdaWrapper);
        } else if (method.equals(baseMapperClass.getDeclaredMethod("insert", Object.class))){
            Object obj = args[0];
            sql = makeSql.insert(obj);
        }


        return null;
    }
}
