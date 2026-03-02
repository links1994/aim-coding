package com.aim.mall.basic.idgen.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 分布式锁工具类
 *
 * @author AI Agent
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock {

    private final StringRedisTemplate redisTemplate;

    /**
     * 默认锁超时时间（秒）
     */
    private static final long DEFAULT_LOCK_TIMEOUT = 10;

    /**
     * 获取锁并执行业务逻辑
     *
     * @param lockKey     锁的Key
     * @param timeout     锁超时时间（秒）
     * @param supplier    业务逻辑
     * @param <T>         返回值类型
     * @return 业务逻辑返回值，获取锁失败返回null
     */
    public <T> T executeWithLock(String lockKey, long timeout, Supplier<T> supplier) {
        Boolean locked = tryLock(lockKey, timeout);
        if (!Boolean.TRUE.equals(locked)) {
            log.debug("获取分布式锁失败，lockKey={}", lockKey);
            return null;
        }

        try {
            return supplier.get();
        } finally {
            releaseLock(lockKey);
        }
    }

    /**
     * 获取锁并执行业务逻辑（使用默认超时时间）
     *
     * @param lockKey     锁的Key
     * @param supplier    业务逻辑
     * @param <T>         返回值类型
     * @return 业务逻辑返回值，获取锁失败返回null
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> supplier) {
        return executeWithLock(lockKey, DEFAULT_LOCK_TIMEOUT, supplier);
    }

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey   锁的Key
     * @param timeout   锁超时时间（秒）
     * @return 是否获取成功
     */
    public Boolean tryLock(String lockKey, long timeout) {
        return redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", timeout, TimeUnit.SECONDS);
    }

    /**
     * 尝试获取分布式锁（使用默认超时时间）
     *
     * @param lockKey 锁的Key
     * @return 是否获取成功
     */
    public Boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_LOCK_TIMEOUT);
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey 锁的Key
     */
    public void releaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
            log.debug("释放分布式锁成功，lockKey={}", lockKey);
        } catch (Exception e) {
            log.error("释放分布式锁失败，lockKey={}", lockKey, e);
        }
    }
}
