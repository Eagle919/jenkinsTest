package com.caih.cloud.iscs.common;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IscsUtil {

    public static StringBuilder packStringBuilder(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1024];
        int len;
        StringBuilder stringBuilder = new StringBuilder();
        while ((len = inputStream.read(bytes)) != -1) {
            stringBuilder.append(new String(bytes, 0, len, StandardCharsets.UTF_8));
        }
        return stringBuilder;
    }

    /**
     * copy Map中的属性 到 实体类中
     * @param map
     * @param o
     */
    public static void copyMapToObject(Map<String, Object> map, Object o) {
        Set<String> set = map.keySet();
        Class c = o.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (set.contains(f.getName())){
                try {
                    f.set(o, map.get(f.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * copy 实体类中的属性 到 Map中
     * @param o
     * @param map
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void copyObjectToMap(Object o, Map<String, Object> map)
            throws IntrospectionException, IllegalAccessException, InvocationTargetException {
        Class type = o.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(o, new Object[0]);
                if (result != null) {
                    map.put(propertyName, result);
                } else {
                    map.put(propertyName, "");
                }
            }
        }
    }

}
