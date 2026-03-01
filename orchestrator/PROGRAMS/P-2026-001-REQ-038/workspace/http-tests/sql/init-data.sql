-- ============================================
-- 岗位类型管理测试数据初始化
-- ============================================
-- 表结构: aim_agent_job_type
-- 说明: 岗位类型基础数据
-- ============================================

-- 清理可能存在的旧数据（使用软删除）
UPDATE aim_agent_job_type 
SET is_deleted = 1, update_time = NOW(), updater_id = 1
WHERE code IN ('SALES', 'TECH', 'CS', 'MARKETING', 'HR', 'FINANCE');

-- 插入标准测试数据
INSERT INTO aim_agent_job_type (code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES ('SALES', '销售顾问', '负责商品销售和客户维护', 1, 1, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES ('TECH', '技术工程师', '负责技术开发和系统维护', 1, 2, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES ('CS', '客服专员', '负责客户服务和投诉处理', 1, 3, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES ('MARKETING', '市场营销', '负责市场推广和品牌建设', 1, 4, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES ('HR', '人力资源', '负责人员招聘和培训管理', 0, 5, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_agent_job_type (code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES ('FINANCE', '财务会计', '负责财务管理和会计核算', 1, 6, 0, 1, 1, NOW(), NOW());
