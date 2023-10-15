package com.enzuo.ioc.bean.utils;

import java.util.Iterator;
import java.util.Map;

/**
 * @Classname ObjectUtils
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/2 21:10
 * @Created by Enzuo
 */

public class ObjectUtils {
    public static boolean isNull(Object obj) {
        return null == obj || obj.equals((Object)null);
    }

    public static boolean isNotNull(Object obj) {
        return null != obj && !obj.equals((Object)null);
    }
    public static boolean isStringEmpty(Object obj) {
        return obj==null || obj.equals((Object)null) || obj.equals("");
    }

}
