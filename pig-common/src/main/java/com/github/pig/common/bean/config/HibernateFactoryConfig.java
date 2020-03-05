package com.github.pig.common.bean.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Created by zengheng on 2018/7/23.
 */

@Configuration
@EnableAutoConfiguration
public class HibernateFactoryConfig {

    static final Logger logger= LoggerFactory.getLogger(HibernateFactoryConfig.class);

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        return new MethodValidationPostProcessor();
    }

}
