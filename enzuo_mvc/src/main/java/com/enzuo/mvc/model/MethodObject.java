package com.enzuo.mvc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @Classname MethodObject
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 18:09
 * @Created by Enzuo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MethodObject {
    private Method method;
    private Object object;
    private String url;
}
