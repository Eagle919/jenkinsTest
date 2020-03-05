package com.github.pig.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @className BeanUtils
 * @Author chenkang
 * @Date 2019/1/10 11:11
 * @Version 1.0
 */
public class ReflectBeanUtils {

    private ReflectBeanUtils() {
    }

    /**
     * 业务类转换工厂 提取字段名和值
     * key - 字段名
     * @param object
     * @throws IllegalAccessException
     */
    public static Map<String,Object> bizClazzFactory(Object object) throws IllegalAccessException{

        Map<String,Object> map = new HashMap<>();

        Class<?> clazz = object.getClass();
        //获取属性集合
        Field[] fields = clazz.getDeclaredFields();

        for(Field field : fields){
            field.setAccessible(true);
            map.put(field.getName(),field.get(object));
        }
        return map;
    }

}
