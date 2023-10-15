package com.enzuo.mvc.enums;

/**
 * @Classname HttpEnum
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 17:02
 * @Created by Enzuo
 */
public enum HttpMethodsEnum {
    GET("get"),
    POST("post"),
    PUT("put"),
    DELETE("delete"),
    HEAD("head"),
    OPTIONS("options");

    private final String methods;
    HttpMethodsEnum(String methods){
        this.methods=methods;
    }

    public String getMethods() {
        return methods;
    }

}
