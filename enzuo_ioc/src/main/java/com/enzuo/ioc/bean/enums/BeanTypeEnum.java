package com.enzuo.ioc.bean.enums;

/**
 * @Classname BeanTypeEnum
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/6 19:03
 * @Created by Enzuo
 */

public enum BeanTypeEnum {
    SINGLETON_BEAN("SingletonBean"),
    MULTICASE_BEAN("MulticaseBean");
    private String type;

    BeanTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
