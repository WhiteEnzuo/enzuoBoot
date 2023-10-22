package com.enzuo.orm.proxy;

/**
 * @Classname NotInterfaceMapperException
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/17 19:15
 * @Created by Enzuo
 */
public class NotInterfaceMapperException extends RuntimeException {
    public NotInterfaceMapperException(){
        super();
    }
    public NotInterfaceMapperException(String message){
        super(message);
    }
}
