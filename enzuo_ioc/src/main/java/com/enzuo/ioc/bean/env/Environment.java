package com.enzuo.ioc.bean.env;

import lombok.Data;

/**
 * @Classname Environment
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/22 16:43
 * @Created by Enzuo
 */

@Data
public class Environment {
    private String address="0.0.0.0";
    private int port=8080;
    private String HttpContext="Servlet";

}
