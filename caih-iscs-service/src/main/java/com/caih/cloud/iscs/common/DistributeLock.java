package com.caih.cloud.iscs.common;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @className DistributeLock
 * @Author chenkang
 * @Date 2019/7/29 17:12
 * @Version 1.0
 */

@Component
public class DistributeLock {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DefaultRedisScript<Long> script;

    private static final Long RELEASE_SUCCESS = 1L;

    /**
     * 保存如果不存在
     */
    private static final String SET_IF_NOT_EXIST = "NX";

    /**
     * 只有存在才会替换
     */
    private static final String SET_IF_EXIST = "XX";

    /**
     * 秒
     */
    private static final String EXP_SECONGD = "EX";

    /**
     * 毫秒
     */
    private static final String EXP_MILLISECONDS = "PX";


    private static final String UNLOCK_LUA ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 获取锁
     * @return
     */
    /*private boolean tryLock(String lockKey , String threadId , long expireTime){
             if(expireTime <= 0){
                 return  false;
             }

             //存入redis  -- 如果用事务保障 这个返回始终为null - redis版本2.1.x
        Boolean result = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, threadId, expireTime, TimeUnit.MILLISECONDS);

             if(result == null){
                    return false;
             }

             return result;

    }*/

    public synchronized String lock(String lockKey, String threadId, long expireTime) {
        return (String) stringRedisTemplate.execute((RedisCallback<? extends Object>) connection -> {
            Object  nativeConnection = connection.getNativeConnection();
            //EX = seconds; PX = milliseconds
            if(nativeConnection instanceof JedisCluster){
                return ((JedisCluster) nativeConnection).set(lockKey, threadId, SET_IF_NOT_EXIST, EXP_MILLISECONDS, expireTime);
            }else if(nativeConnection instanceof Jedis){
                Byte[] args =  new Byte[1024];
                return ((Jedis) nativeConnection).set(lockKey, threadId, SET_IF_NOT_EXIST, EXP_MILLISECONDS, expireTime);
            }
            return 0L;
        });
    }


    /**
     * 释放锁
     * @param lockKey
     *
     * @param threadId
     * @return
     */
    public boolean unlock(String lockKey , String threadId){
        //使用lua脚本 ， 先判断是否是自己设置的锁，再执行删除
        Long result = stringRedisTemplate.execute(script, Arrays.asList(lockKey,threadId));

        return RELEASE_SUCCESS.equals(result);
    }

    @Bean
    public DefaultRedisScript<Long> defaultRedisScript(){
        DefaultRedisScript<Long>  defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptText("if redis.call('get', KEYS[1]) == KEYS[2] then return redis.call('del', KEYS[1]) else return 0 end");
        return defaultRedisScript;
    }

}

