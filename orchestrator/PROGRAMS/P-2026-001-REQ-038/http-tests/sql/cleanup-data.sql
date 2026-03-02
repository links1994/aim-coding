-- ============================================
-- 岗位类型管理测试数据清理
-- ============================================

-- 软删除所有测试数据（保留数据便于问题排查）
UPDATE aim_agent_job_type 
SET is_deleted = 1, update_time = NOW(), updater_id = 1
WHERE code IN ('SALES', 'TECH', 'CS', 'MARKETING', 'HR', 'FINANCE', 'TEST', 'TEMP001', 'TEMP002');

-- 如需彻底删除测试数据，取消注释以下语句：
-- DELETE FROM aim_agent_job_type WHERE code IN ('SALES', 'TECH', 'CS', 'MARKETING', 'HR', 'FINANCE', 'TEST', 'TEMP001', 'TEMP002');
