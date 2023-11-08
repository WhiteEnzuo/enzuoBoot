package com.enzuo.mvc.redirect;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname RedireHtml
 * @Description
 * @Version 1.0.0
 * @Date 2023/11/08 20:51
 * @Created by Enzuo
 */
@Data
public class RedirectHtml {
    private String redirectHtmlPath;
    private final Map<String,Object> data=new HashMap<>();
    private RedirectHtml(){}
    public RedirectHtml(String redirectHtmlPath){
        this.redirectHtmlPath=redirectHtmlPath;
    }
    public void putDataByKey(String key,Object value){
        data.put(key,value);
    }
    public Object getDataByKey(String key){
        return data.get(key);
    }
}
