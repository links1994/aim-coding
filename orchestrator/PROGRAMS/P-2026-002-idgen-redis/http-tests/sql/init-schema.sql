-- ID生成规则表
CREATE TABLE IF NOT EXISTS aim_idgen_rule
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    prefix          VARCHAR(10)  NOT NULL COMMENT '业务前缀',
    date_pattern    VARCHAR(20)  NOT NULL COMMENT '日期格式',
    current_max_seq BIGINT       DEFAULT 0 COMMENT '当前最大序号',
    step_size       INT          DEFAULT 1000 COMMENT '号段步长',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_prefix_pattern (prefix, date_pattern)
) COMMENT 'ID生成规则表'
    DEFAULT CHARSET = utf8mb4;
