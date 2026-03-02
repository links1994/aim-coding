package com.aim.mall.basic.idgen.constant;

/**
 * ID生成常量
 *
 * @author AI Agent
 */
public final class IdGenConstant {

    private IdGenConstant() {
        // 私有构造函数，防止实例化
    }

    /**
     * Redis Key 前缀
     */
    public static final String REDIS_KEY_PREFIX = "idgen";

    /**
     * 序号最大位数
     */
    public static final int SEQUENCE_MAX_DIGITS = 6;

    /**
     * 序号最大值（6位数字）
     */
    public static final long SEQUENCE_MAX_VALUE = 999999L;

    /**
     * 序号格式化模板（6位，不足补零）
     */
    public static final String SEQUENCE_FORMAT = "%06d";
}
