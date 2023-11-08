package com.enzuo.orm.makeSql;

import com.enzuo.orm.util.Page;

import java.nio.file.Watchable;
import java.util.List;

/**
 * @Classname MakeSQL
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 09:04
 * @Created by Enzuo
 */
public interface MakeSql {
    public String select(Class<?> clazz ,List<String> filter,Wrapper<?> wrapper);
    public String selectByPage(Class<?> clazz ,List<String> filter, Page<?> page, Wrapper<?> wrapper);
    public String update(Object dao,Wrapper<?> wrapper) ;
    public String delete(Class<?> dao,Wrapper<?> wrapper);
    public String insert(Object dao) ;

}
