-- ============================================
-- 岗位类型测试数据清理
-- ============================================

-- 软删除测试数据（保留数据便于问题排查）
UPDATE aim_job_type 
SET is_deleted = 1, update_time = NOW(), updater_id = 1
WHERE code IN ('SALES', 'TECH', 'CS', 'TEST', 'TEMP001', 'TEMP002');

-- 如需彻底删除，使用以下语句：
-- DELETE FROM aim_job_type WHERE code IN ('SALES', 'TECH', 'CS', 'TEST', 'TEMP001', 'TEMP002');
