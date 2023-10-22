package com.enzuo.orm.proxy;

import com.enzuo.orm.Test;
import com.enzuo.orm.makeSql.MakeSql;
import com.enzuo.orm.makeSql.Wrapper;
import com.enzuo.orm.makeSql.impl.MysqlMakeSql;
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
    public MapperProxyObject(Class<?> clazz){
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if(genericInterfaces.length==0){
            throw  new NotInterfaceMapperException("没有继承BaseMapper");
        }
        ParameterizedType genericInterface = (ParameterizedType) genericInterfaces[0];
        Type[] actualTypeArguments = genericInterface.getActualTypeArguments();
        if(actualTypeArguments.length==0){
            throw  new NotInterfaceMapperException("没找到泛型");
        }
        Type actualTypeArgument = actualTypeArguments[0];
        String typeName = actualTypeArgument.getTypeName();
        try {
            this.clazz = Class.forName(typeName);

        }catch (ClassNotFoundException e) {
            throw new NotInterfaceMapperException(e.getMessage());
        }


    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MysqlMakeSql mysqlMakeSql = new MysqlMakeSql();
        Class<?> baseMapperClass = BaseMapper.class;
        String sql ;
        if (method.equals(baseMapperClass.getDeclaredMethod("select", Wrapper.class))){
             sql=mysqlMakeSql.select(clazz, new ArrayList<>(), (Wrapper) args[0]);
        } else if (method.equals(baseMapperClass.getDeclaredMethod("selectById", Serializable.class))) {
            sql=mysqlMakeSql.select(clazz,new ArrayList<>(),(Wrapper) args[0]);
        } else if (method.equals(baseMapperClass.getDeclaredMethod("selectOne", Wrapper.class))) {
            
        }


        return null;
    }
}
