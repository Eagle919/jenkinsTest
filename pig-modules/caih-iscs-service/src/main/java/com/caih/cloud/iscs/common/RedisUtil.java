package com.caih.cloud.iscs.common;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;

import java.util.concurrent.TimeUnit;

/**
 * @author yuwei
 * @date 2018/11/29 11:48
 */
public class RedisUtil {
    /**
     * Long 自增
     * @param key
     * @param liveTime
     * @param redisTemplate
     * @return
     */
    public static Long incrLong(String key, long liveTime, RedisTemplate<String, Long> redisTemplate) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        Long increment = entityIdCounter.getAndIncrement();
        if ((null == increment || increment.longValue() == 0) && liveTime > 0) {//初始设置过期时间
            entityIdCounter.expire(liveTime, TimeUnit.SECONDS);
        }
        return increment;
    }
}
