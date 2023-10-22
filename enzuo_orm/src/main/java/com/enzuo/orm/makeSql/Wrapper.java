package com.enzuo.orm.makeSql;

import java.io.Serializable;

/**
 * @Classname Wapper
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 09:08
 * @Created by Enzuo
 */
public interface Wrapper<T> {
    public Wrapper eq(Object key, Object value);
    public Wrapper or();
    public Wrapper and();
    public Wrapper like(Object key, Object value);

}
