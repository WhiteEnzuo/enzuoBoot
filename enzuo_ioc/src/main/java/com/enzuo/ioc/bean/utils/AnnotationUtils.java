package com.enzuo.ioc.bean.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname AnnotationUtils
 * @Description
 * @Version 1.0.0
 * @Date 2023/10/4 19:21
 * @Created by Enzuo
 */

public class AnnotationUtils {
    private static boolean isAnnotation(Annotation annotation, List<Class<?>> list, Class<? extends Annotation> annotationClazz) {
        if (annotation.annotationType().getAnnotation(annotationClazz) != null) return true;
        if (list.contains(annotation.annotationType())) return false;
        list.add(annotation.annotationType());
        Class<?> annotationType = annotation.annotationType();
        Annotation[] annotations = annotationType.getDeclaredAnnotations();
        boolean flag = false;
        for (Annotation an : annotations) {
            flag = flag || isAnnotation(an, list, annotationClazz);
        }
        return flag;
    }

    public static boolean isAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClazz, List<Class<? extends Annotation>> temp) {
        if (clazz.getAnnotation(annotationClazz) != null) {
            Annotation annotation = clazz.getAnnotation(annotationClazz);
            if (temp != null)
                temp.add(annotation.annotationType());
            return true;
        }
        boolean flag = false;
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            flag = flag || AnnotationUtils.isAnnotation(annotation, new ArrayList<>(), annotationClazz);
            if (temp != null && flag) {
                temp.add(annotation.annotationType());
            }
        }
        return flag;
    }

    private static Object getAnnotation(Annotation annotation, List<Class<?>> list, Class<? extends Annotation> annotationClazz) {
        if (annotation.annotationType().getAnnotation(annotationClazz) != null) return annotation;
        if (list.contains(annotation.annotationType())) return null;
        list.add(annotation.annotationType());
        Class<?> annotationType = annotation.annotationType();
        Annotation[] annotations = annotationType.getDeclaredAnnotations();
        Object target = null;
        for (Annotation an : annotations) {
            target = getAnnotation(an, list, annotationClazz);
            if(ObjectUtils.isNotNull(target)) {
                return target;
            }
        }
        return target;
    }

    public static <T> List<T> getAnnotation(Class<? extends Annotation> annotationClazz,Class<T> targetAnnotation){
        List<T> list =new ArrayList<>();
        Class<? extends  Annotation> targetAnnotationClazz=(Class<? extends Annotation>)targetAnnotation;
        if (annotationClazz.getAnnotation(targetAnnotationClazz) != null) {
            list.add((T) annotationClazz.getAnnotation(targetAnnotationClazz));
        }
        Object target=null;
        for (Annotation annotation : annotationClazz.getDeclaredAnnotations()) {
            target = AnnotationUtils.getAnnotation(annotation, new ArrayList<>(), targetAnnotationClazz);
            if(ObjectUtils.isNotNull(target)){
                list.add ((T)target);
            }
        }
        return list;
    }
}
