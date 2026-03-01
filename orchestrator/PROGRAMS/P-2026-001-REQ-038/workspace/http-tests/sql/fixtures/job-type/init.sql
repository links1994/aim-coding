-- ============================================
-- 岗位类型测试数据初始化
-- ============================================

-- 清理可能存在的旧数据
DELETE FROM aim_job_type WHERE code IN ('SALES', 'TECH', 'CS', 'TEST', 'TEMP001');

-- 插入测试数据
INSERT INTO aim_job_type (id, code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES (1, 'SALES', '销售顾问', '负责商品销售和客户维护', 1, 1, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_job_type (id, code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES (2, 'TECH', '技术工程师', '负责技术开发和系统维护', 1, 2, 0, 1, 1, NOW(), NOW());

INSERT INTO aim_job_type (id, code, name, description, status, sort_order, is_deleted, creator_id, updater_id, create_time, update_time) 
VALUES (3, 'CS', '客服专员', '负责客户服务和投诉处理', 1, 3, 0, 1, 1, NOW(), NOW());
