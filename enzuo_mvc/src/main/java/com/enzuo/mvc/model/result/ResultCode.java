package com.enzuo.mvc.model.result;

public interface ResultCode {
    public static Integer SUCCESS = 200;  //成功
    public static Integer NO_RESOURCES = 204; //无资源
    public static Integer FORWARD = 300; //重定向
    public static Integer NO_LOGIN = 402; //未登录
    public static Integer REFUSE = 403;  //废弃
    public static Integer BAN = 405; //账号被封禁

    public static Integer NOT_FOUND=404;

    public static Integer ERROR = 500; //错误
    public static Integer FLUSH = 600; //刷新
}
