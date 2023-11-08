package com.enzuo.mvc.context;



import com.enzuo.ioc.bean.beanFactory.AbstractBeanFactory;
import com.enzuo.ioc.bean.context.ApplicationContext;
import com.enzuo.ioc.bean.utils.ObjectUtils;
import com.enzuo.mvc.annotation.*;
import com.enzuo.mvc.enums.HttpMethodsEnum;
import com.enzuo.mvc.model.MethodObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname HttpContext
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/15 17:10
 * @Created by Enzuo
 */
public class ControllerContext {
    private ApplicationContext context;
    private Map<String, MethodObject> methodMap;
    private ControllerContext(){}
    public ControllerContext(ApplicationContext context){
        this.context = context;
        this.methodMap=new HashMap<>();
        AbstractBeanFactory beanFactory = context.getBeanFactory();
        Iterable<Object> beans = beanFactory.getBean();
        for (Object bean : beans) {
            Class<?> beanClazz = bean.getClass();
            Controller controller = beanClazz.getAnnotation(Controller.class);
            if(ObjectUtils.isNull(controller)){
                continue;
            }
            StringBuilder urlStringBuilder=new StringBuilder();
            RequestMapping clazzRequestMapping = beanClazz.getAnnotation(RequestMapping.class);
            if(ObjectUtils.isNull(clazzRequestMapping)){
                registerMethods(beanClazz,urlStringBuilder,bean);
            }else{
                urlStringBuilder.append(clazzRequestMapping.value());
                registerMethods(beanClazz,urlStringBuilder,bean);
            }
        }

    }
    private String makeUrl(String methods,String url,StringBuilder builder){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(methods).append(" ").append(builder.toString()).append(url);
        return stringBuilder.toString();
    }
    private void registerMethods(Class<?> beanClazz,StringBuilder urlStringBuilder,Object bean){
        Method[] declaredMethods = beanClazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            RequestMapping methodRequestMapping = declaredMethod.getAnnotation(RequestMapping.class);
            if(ObjectUtils.isNotNull(methodRequestMapping)){
                HttpMethodsEnum methods = methodRequestMapping.methods();
                String methodsString = methods.getMethods();
                if(ObjectUtils.isStringEmpty(methodsString)){
                    StringBuilder newUrlStringBuilder = new StringBuilder(urlStringBuilder.toString());
                    String url = makeUrl("All", methodRequestMapping.value(), newUrlStringBuilder);

                    MethodObject methodObject = new MethodObject();
                    methodObject.setMethod(declaredMethod);
                    methodObject.setUrl(newUrlStringBuilder.toString());
                    methodObject.setObject(bean);
                    methodMap.put(url,methodObject);
                    continue;
                }
                StringBuilder newUrlStringBuilder = new StringBuilder(urlStringBuilder.toString());
                String url = makeUrl(methods.getMethods(), methodRequestMapping.value(), newUrlStringBuilder);
                MethodObject methodObject = new MethodObject();
                methodObject.setMethod(declaredMethod);
                methodObject.setUrl(newUrlStringBuilder.toString());
                methodObject.setObject(bean);
                methodMap.put(url,methodObject);
                continue;
            }
            GetMapping methodGetMapping = declaredMethod.getAnnotation(GetMapping.class);
            if(ObjectUtils.isNotNull(methodGetMapping)){
                StringBuilder newUrlStringBuilder = new StringBuilder(urlStringBuilder.toString());
                newUrlStringBuilder.append(makeUrl(HttpMethodsEnum.GET.getMethods(),  methodGetMapping.value(),newUrlStringBuilder));
                MethodObject methodObject = new MethodObject();
                methodObject.setMethod(declaredMethod);
                methodObject.setUrl(newUrlStringBuilder.toString());
                methodObject.setObject(bean);
                methodMap.put(newUrlStringBuilder.toString(),methodObject);
                continue;
            }
            PostMapping methodPostMapping = declaredMethod.getAnnotation(PostMapping.class);
            if(ObjectUtils.isNotNull(methodGetMapping)){
                StringBuilder newUrlStringBuilder = new StringBuilder(urlStringBuilder.toString());
                makeUrl(HttpMethodsEnum.POST.getMethods(),  methodPostMapping.value(),newUrlStringBuilder);
                MethodObject methodObject = new MethodObject();
                methodObject.setMethod(declaredMethod);
                methodObject.setUrl(newUrlStringBuilder.toString());
                methodObject.setObject(bean);
                methodMap.put(newUrlStringBuilder.toString(),methodObject);
                continue;
            }
            PutMapping methodPutMapping = declaredMethod.getAnnotation(PutMapping.class);
            if(ObjectUtils.isNotNull(methodGetMapping)){
                StringBuilder newUrlStringBuilder = new StringBuilder(urlStringBuilder.toString());
                makeUrl(HttpMethodsEnum.PUT.getMethods(),  methodPutMapping.value(),newUrlStringBuilder);
                MethodObject methodObject = new MethodObject();
                methodObject.setMethod(declaredMethod);
                methodObject.setUrl(newUrlStringBuilder.toString());
                methodObject.setObject(bean);
                methodMap.put(newUrlStringBuilder.toString(),methodObject);
                continue;
            }
            DeleteMapping methodDeleteMapping = declaredMethod.getAnnotation(DeleteMapping.class);
            if(ObjectUtils.isNotNull(methodGetMapping)){
                StringBuilder newUrlStringBuilder = new StringBuilder(urlStringBuilder.toString());
                makeUrl(HttpMethodsEnum.DELETE.getMethods(),  methodDeleteMapping.value(),newUrlStringBuilder);
                MethodObject methodObject = new MethodObject();
                methodObject.setMethod(declaredMethod);
                methodObject.setUrl(newUrlStringBuilder.toString());
                methodObject.setObject(bean);
                methodMap.put(newUrlStringBuilder.toString(),methodObject);

            }
        }
    }

    public MethodObject getMethods(String methods,String url){
        String methodKey=methods+" "+ url;
        return methodMap.get(methodKey);
    }
}
