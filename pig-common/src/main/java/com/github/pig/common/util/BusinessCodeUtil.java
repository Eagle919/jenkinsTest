package com.github.pig.common.util;

import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author yuwei
 * @date 2018/12/20 10:30
 */
public class BusinessCodeUtil {
    private BusinessCodeUtil(){}
    /**
     * 编号生成器
     * @param prefix
     * @param randomLength
     * @param redisTemplate
     * @return
     */
    public static String dailyCodeWithRedis(final String prefix, final String randomLength, final RedisTemplate<String, Long> redisTemplate) {
        // 获取距离今天结束的毫秒数，设置为缓存过期时间
        Long seconds = DateUtil.distanceToNextDay();
        // 获取redis中自增
        Long times = RedisUtil.incrLong(prefix, seconds, redisTemplate);
        // 根据规则返回
        // 业务类型 + 时间 + 自然日流水号
        return prefix + DateUtil.currentDateStr() + StringUtil.addZeroBefore(times.intValue(), randomLength);
    }

    /**
     * 流水号
     * @param prefix
     * @param randomLength
     * @param redisTemplate
     * @return
     */
    public static String serialNumber(final String prefix, final String randomLength, final RedisTemplate<String, Long> redisTemplate) {
        // 获取距离今天结束的毫秒数，设置为缓存过期时间
        Long seconds = DateUtil.distanceToNextDay();
        // 获取redis中自增
        Long times = RedisUtil.incrLong(prefix, seconds, redisTemplate);
        // 自然日流水号
        return StringUtil.addZeroBefore(times.intValue(), randomLength);
    }
}
