package com.github.pig.common.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @className springcontextUtils
 * @Author chenkang
 * @Date 2018/11/15 13:34
 * @Version 1.0
 */

@SuppressWarnings("unchecked")
@Component
public class SpringContextUtils implements BeanFactoryPostProcessor {

    /**
     * Spring应用上下文环境
     */
    private static ConfigurableListableBeanFactory beanFactory;

    private static  void setBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory){
        beanFactory = configurableListableBeanFactory;
    }
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        setBeanFactory(beanFactory) ;
    }
    /**
     * 获取对象
     *
     * @param name
     * @return Object 一个以所给名字注册的bean的实例
     * @throws BeansException
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        //首字母默认小写
        name=lowerCaseInit(name);
        if (containsBean(name)){
            return (T) beanFactory.getBean(name);
        }else{
            return null;
        }
    }

    /**
     * 获取类型为requiredType的对象
     *
     * @param clz
     * @return
     * @throws BeansException
     *
     */
    public static <T> T getBean(Class<T> clz) {
        @SuppressWarnings("unchecked")
        T result = beanFactory.getBean(clz);
        return result;
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @param name
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return beanFactory.containsBean(name);
    }

    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @param name
     * @return boolean
     * @throws NoSuchBeanDefinitionException
     *
     */
    public static boolean isSingleton(String name)  {
        return beanFactory.isSingleton(name);
    }

    /**
     * @param name
     * @return Class 注册对象的类型
     * @throws NoSuchBeanDefinitionException
     *
     */
    public static Class<?> getType(String name) {
        return beanFactory.getType(name);
    }

    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     *
     * @param name
     * @return
     * @throws NoSuchBeanDefinitionException
     *
     */
    public static String[] getAliases(String name)  {
        return beanFactory.getAliases(name);
    }

    /**
     *首字母小写
     * @return:  小写的首字母
     */

    private static String lowerCaseInit(String str){
        if (str.length()>0){
            char c = str.charAt(0);
            if (c >= 65 && c <= 90){
                int i = c + 32;
                return ((char)i)+str.substring(1);
            }else{
                return str;
            }
        }else{
            return  null;
        }
    }
}
