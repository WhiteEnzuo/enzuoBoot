package com.enzuo.mvc.controller;

import com.enzuo.mvc.annotation.Controller;
import com.enzuo.mvc.annotation.GetMapping;
import com.enzuo.mvc.redirect.RedirectHtml;
import lombok.Data;

/**
 * @Classname TestController
 * @Description
 * @Version 1.0.0
 * @Date 2023/11/07 17:22
 * @Created by Enzuo
 */
@Controller
public class TestController {
    @GetMapping("/test")
    public RedirectHtml Test(){
        RedirectHtml redirectHtml = new RedirectHtml("123.html");
        redirectHtml.putDataByKey("enzuo","enzuo");
        return redirectHtml;
    }
}

