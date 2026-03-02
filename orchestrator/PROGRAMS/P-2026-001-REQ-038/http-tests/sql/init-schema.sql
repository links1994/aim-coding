-- ============================================
-- 表结构定义
-- ============================================

CREATE TABLE IF NOT EXISTS aim_agent_job_type
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name        VARCHAR(100) NOT NULL COMMENT '岗位类型名称',
    code        VARCHAR(50)  NOT NULL COMMENT '岗位类型编码',
    description VARCHAR(500) COMMENT '岗位类型描述',
    status      TINYINT      DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    sort_order  INT          DEFAULT 0 COMMENT '排序号',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted  TINYINT      DEFAULT 0 COMMENT '逻辑删除标记：0-未删除 1-已删除',
    creator_id  BIGINT COMMENT '创建人ID',
    updater_id  BIGINT COMMENT '更新人ID',
    UNIQUE KEY uk_code (code)
) COMMENT '岗位类型表' DEFAULT CHARSET = utf8mb4;

-- ============================================
-- 初始化数据
-- ============================================

INSERT INTO aim_agent_job_type (id, code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time)
VALUES (1, 'SALES', '销售顾问', '负责商品销售和客户维护', 1, 1, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (id, code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time)
VALUES (2, 'TECH', '技术工程师', '负责技术开发和系统维护', 1, 2, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (id, code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time)
VALUES (3, 'CS', '客服专员', '负责客户服务和投诉处理', 1, 3, 0, 1, 1, NOW(), NOW());
