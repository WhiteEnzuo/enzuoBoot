package com.enzuo.orm.makeSql.impl;

import com.enzuo.ioc.bean.utils.ObjectUtils;
import com.enzuo.orm.annotation.Table;
import com.enzuo.orm.makeSql.MakeSql;
import com.enzuo.orm.makeSql.Wrapper;
import com.enzuo.orm.util.Page;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname MysqlMakeSql
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/16 09:11
 * @Created by Enzuo
 */
@Slf4j
public class MysqlMakeSql implements MakeSql {
    @Override
    public String select(Class<?> daoClazz, List<String> filter, Wrapper<?> wrapper) {


        Table table = daoClazz.getAnnotation(Table.class);
        String tableName=getTableName(daoClazz,table);


        List<String> selectField=new ArrayList<>();
        Field[] declaredFields = daoClazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if(!filter.contains(declaredField.getName())){
                selectField.add(declaredField.getName());
            }
        }
        StringBuilder stringBuilder  = new StringBuilder();
        stringBuilder.append("select ");
        for (String filedName : selectField) {
            stringBuilder.append(filedName).append(",");
        }
        if(stringBuilder.length()>7) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }else{
            throw  new RuntimeException("filter全过滤了");
        }

        stringBuilder.append(" from ").append(tableName);
        String sql = addWrapper(stringBuilder, wrapper);
        log.info(sql);
        return sql;
    }

    @Override
    public String selectByPage(Class<?> clazz,List<String> filter, Page<?> page, Wrapper<?> wrapper) {
        Integer limit = page.getLimit();
        Integer size = page.getSize();
        String select = select(clazz, filter, wrapper);
        select = select.replace(";","");
        return select + " " + "limit " + limit + "," + size + ";";
    }

    @Override
    public String update(Object dao, Wrapper<?> wrapper)  {
        if(ObjectUtils.isNull(dao)){
            throw  new RuntimeException("不能传入null的dao");
        }
        Class<?> daoClazz = dao.getClass();
        Table table = daoClazz.getAnnotation(Table.class);
        String tableName=getTableName(daoClazz,table);


        Field[] declaredFields = daoClazz.getDeclaredFields();

        StringBuilder stringBuilder  = new StringBuilder();
        stringBuilder.append("update ").append(tableName);
        int length = stringBuilder.length();
        for (Field declaredField : declaredFields) {
            String fieldName = declaredField.getName();
            Object filed =null;
            try {
                filed = declaredField.get(dao);

            }catch (Exception e){
                log.error(e.getMessage());
                return "";
            }
            if(ObjectUtils.isNull(filed)){
                continue;
            }
            stringBuilder.append(fieldName).append("=").append(filed)
                    .append(",");

        }

        if(stringBuilder.length()>length) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }else{
            throw  new RuntimeException("filter全过滤了");
        }
        stringBuilder.append(" ");
        String sql = addWrapper(stringBuilder, wrapper);
        log.info(sql);
        return sql;
    }

    @Override
    public String delete(Class<?> daoClazz, Wrapper<?> wrapper) {

        Table table = daoClazz.getAnnotation(Table.class);
        String tableName=getTableName(daoClazz,table);
        StringBuilder stringBuilder  = new StringBuilder();
        stringBuilder.append("delete from ").append(tableName);
        String sql = addWrapper(stringBuilder, wrapper);
        log.info(sql);
        return sql;
    }

    @Override
    public String insert(Object dao) {
        if(ObjectUtils.isNull(dao)){
            throw  new RuntimeException("不能传入null的dao");
        }
        Class<?> daoClazz = dao.getClass();
        Table table = daoClazz.getAnnotation(Table.class);

        String tableName=getTableName(daoClazz,table);

        StringBuilder stringBuilder  = new StringBuilder();
        stringBuilder.append("insert into ").append(tableName);
        Field[] declaredFields = daoClazz.getDeclaredFields();
        stringBuilder.append("(");
        List<Object> values=new ArrayList<>();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object value=null;
            try {
                value = declaredField.get(dao);

            }catch (Exception e){
                log.error(e.getMessage());
                return "";
            }
            if(ObjectUtils.isNull(value)){
                continue;
            }
            values.add(value);
            String name = declaredField.getName();
            stringBuilder.append(name).append(",");
        }
        if(values.isEmpty()){
            throw  new RuntimeException("filter全过滤了");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1).append(") values (");
        for (Object value : values) {
            stringBuilder.append(value.toString()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1).append(")");


        String sql = stringBuilder.toString();
        log.info(sql);
        return sql;
    }
    private String addWrapper(StringBuilder sql , Wrapper<?> wrapper){

        return sql.toString();
    }

    private String makeName(String name){
        return name;
    }
    private String getTableName(Class<?> clazz,Table table){
        String tableName;
        if(ObjectUtils.isNull(table)){
            tableName=clazz.getSimpleName();
        }else{
            if(ObjectUtils.isStringEmpty(table.value())){
                tableName=makeName(clazz.getSimpleName());
            }else{
                tableName=makeName(table.value());
            }
        }
        return tableName;
    }
}
