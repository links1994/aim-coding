package com.aim.mall.basic.idgen.service.impl;

import com.aim.mall.basic.idgen.constant.IdGenConstant;
import com.aim.mall.basic.idgen.domain.entity.AimIdGenRuleDO;
import com.aim.mall.basic.idgen.domain.enums.DatePatternEnum;
import com.aim.mall.basic.idgen.mapper.AimIdGenRuleMapper;
import com.aim.mall.basic.idgen.service.IdGenService;
import com.aim.mall.basic.idgen.util.RedisDistributedLock;
import com.aim.mall.common.api.CommonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ID生成服务实现（号段模式）
 *
 * @author AI Agent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdGenServiceImpl implements IdGenService {

    private final StringRedisTemplate redisTemplate;
    private final AimIdGenRuleMapper idGenRuleMapper;
    private final RedisDistributedLock distributedLock;

    /**
     * Redis 号段 Key 前缀
     */
    private static final String SEGMENT_KEY_PREFIX = "idgen:segment";

    /**
     * Redis Hash Field
     */
    private static final String FIELD_CURRENT = "current";
    private static final String FIELD_MAX = "max";
    private static final String FIELD_STEP = "step";

    @Override
    public String generateCode(String prefix, DatePatternEnum datePattern) {
        // 获取当前日期字符串
        String dateValue = LocalDateTime.now().format(datePattern.getFormatter());

        // 构建 Redis Key
        String redisKey = String.format("%s:%s:%s:%s", SEGMENT_KEY_PREFIX, prefix, datePattern.getPattern(), dateValue);

        // 获取当前序号
        Long sequence = getNextSequence(redisKey, prefix, datePattern.getPattern());

        // 检查是否超过上限
        if (sequence > IdGenConstant.SEQUENCE_MAX_VALUE) {
            log.error("ID生成序号达到上限，prefix={}, datePattern={}, sequence={}", prefix, datePattern.getPattern(), sequence);
            throw new RuntimeException("当日序号已用完");
        }

        // 格式化序号（6位，不足补零）
        String seqStr = String.format(IdGenConstant.SEQUENCE_FORMAT, sequence);

        // 组合完整编码
        String code = prefix + dateValue + seqStr;

        log.debug("生成编码成功，prefix={}, datePattern={}, code={}", prefix, datePattern.getPattern(), code);

        return code;
    }

    /**
     * 分布式锁超时时间（秒）
     */
    private static final long LOCK_TIMEOUT = 10;

    /**
     * 获取下一个序号（号段模式）
     *
     * @param redisKey    Redis Key
     * @param prefix      业务前缀
     * @param datePattern 日期格式
     * @return 下一个序号
     */
    private Long getNextSequence(String redisKey, String prefix, String datePattern) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        // 1. 尝试从 Redis 自增
        Long current = hashOps.increment(redisKey, FIELD_CURRENT, 1);

        // 2. 检查号段是否存在或已用尽
        if (current == null || current == 1 || isSegmentExhausted(redisKey, current)) {
            // 号段不存在或已用尽，使用分布式锁申请新号段
            String lockKey = String.format("lock:idgen:%s:%s", prefix, datePattern);

            Long result = distributedLock.executeWithLock(lockKey, LOCK_TIMEOUT, () -> {
                // 双重检查
                String maxStr = hashOps.get(redisKey, FIELD_MAX);
                String currentStr = hashOps.get(redisKey, FIELD_CURRENT);

                if (maxStr == null || (currentStr != null && Long.parseLong(currentStr) > Long.parseLong(maxStr))) {
                    // 申请新号段
                    IdGenSegment segment = allocateSegmentFromMySQL(prefix, datePattern);
                    loadSegmentToRedis(redisKey, segment);

                    // 重新自增
                    return hashOps.increment(redisKey, FIELD_CURRENT, 1);
                } else {
                    // 其他线程已加载号段，重新自增
                    return hashOps.increment(redisKey, FIELD_CURRENT, 1);
                }
            });

            if (result == null) {
                // 获取锁失败，其他节点正在申请号段，等待后重试
                log.warn("获取分布式锁失败，等待后重试，redisKey={}", redisKey);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // 递归重试
                return getNextSequence(redisKey, prefix, datePattern);
            }

            current = result;
        }

        return current;
    }

    /**
     * 检查号段是否已用尽
     *
     * @param redisKey Redis Key
     * @param current  当前序号
     * @return 是否已用尽
     */
    private boolean isSegmentExhausted(String redisKey, Long current) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        String maxStr = hashOps.get(redisKey, FIELD_MAX);

        if (maxStr == null) {
            return true;
        }

        Long max = Long.parseLong(maxStr);
        return current > max;
    }

    /**
     * 从 MySQL 分配号段
     *
     * @param prefix      业务前缀
     * @param datePattern 日期格式
     * @return 号段信息
     */
    @Transactional(rollbackFor = Exception.class)
    public IdGenSegment allocateSegmentFromMySQL(String prefix, String datePattern) {
        // 1. 查询记录是否存在
        AimIdGenRuleDO rule = idGenRuleMapper.selectByPrefixAndPattern(prefix, datePattern);

        if (rule == null) {
            // 2. 自动初始化记录
            log.info("自动初始化ID生成规则，prefix={}, datePattern={}", prefix, datePattern);

            rule = new AimIdGenRuleDO();
            rule.setPrefix(prefix);
            rule.setDatePattern(datePattern);
            rule.setCurrentMaxSeq(0L);
            rule.setStepSize(1000);

            idGenRuleMapper.insert(rule);
        }

        // 3. 分配号段（原子更新）
        int stepSize = rule.getStepSize();
        int updated = idGenRuleMapper.allocateSegment(prefix, datePattern, stepSize);

        if (updated == 0) {
            throw new RuntimeException("分配号段失败，prefix=" + prefix + ", datePattern=" + datePattern);
        }

        // 4. 查询更新后的记录
        rule = idGenRuleMapper.selectByPrefixAndPattern(prefix, datePattern);

        long startSeq = rule.getCurrentMaxSeq() - stepSize + 1;
        long endSeq = rule.getCurrentMaxSeq();

        log.info("分配号段成功，prefix={}, datePattern={}, startSeq={}, endSeq={}",
                prefix, datePattern, startSeq, endSeq);

        return new IdGenSegment(startSeq, endSeq, stepSize);
    }

    /**
     * 加载号段到 Redis
     *
     * @param redisKey Redis Key
     * @param segment  号段信息
     */
    private void loadSegmentToRedis(String redisKey, IdGenSegment segment) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();

        Map<String, String> map = new HashMap<>();
        map.put(FIELD_CURRENT, String.valueOf(segment.getStartSeq() - 1)); // 初始值为 start-1，第一次自增后变为 start
        map.put(FIELD_MAX, String.valueOf(segment.getEndSeq()));
        map.put(FIELD_STEP, String.valueOf(segment.getStepSize()));

        hashOps.putAll(redisKey, map);

        // 设置过期时间（7天）
        redisTemplate.expire(redisKey, 7, TimeUnit.DAYS);

        log.debug("加载号段到Redis，redisKey={}, segment={}", redisKey, segment);
    }

    /**
     * 号段信息
     */
    private static class IdGenSegment {
        private final long startSeq;
        private final long endSeq;
        private final int stepSize;

        public IdGenSegment(long startSeq, long endSeq, int stepSize) {
            this.startSeq = startSeq;
            this.endSeq = endSeq;
            this.stepSize = stepSize;
        }

        public long getStartSeq() {
            return startSeq;
        }

        public long getEndSeq() {
            return endSeq;
        }

        public int getStepSize() {
            return stepSize;
        }

        @Override
        public String toString() {
            return "IdGenSegment{" +
                    "startSeq=" + startSeq +
                    ", endSeq=" + endSeq +
                    ", stepSize=" + stepSize +
                    '}';
        }
    }
}
