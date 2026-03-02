package com.aim.mall.agent.service.mp;

import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.aim.mall.agent.employee.mapper.AimJobTypeMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位类型MyBatis-Plus数据服务
 * <p>
 * 封装所有数据访问，包括MP方法和原生Mapper调用
 * 上层服务禁止直接使用 getBaseMapper()，必须通过本类封装方法间接访问
 *
 * @author AI Agent
 */
@Slf4j
@Service
public class AimJobTypeService extends ServiceImpl<AimJobTypeMapper, AimJobTypeDO> {

    /**
     * 根据编码查询（排除已删除）
     *
     * @param code 岗位类型编码
     * @return 岗位类型实体
     */
    public AimJobTypeDO getByCode(String code) {
        log.debug("根据编码查询岗位类型, code: {}", code);
        return baseMapper.selectByCode(code);
    }

    /**
     * 检查编码是否已存在（排除已删除）
     *
     * @param code 岗位类型编码
     * @return 是否存在
     */
    public boolean isCodeExists(String code) {
        return getByCode(code) != null;
    }

    /**
     * 检查编码是否已存在（排除指定ID，用于更新场景）
     *
     * @param code 岗位类型编码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    public boolean isCodeExistsExcludeId(String code, Long excludeId) {
        AimJobTypeDO entity = getByCode(code);
        return entity != null && !entity.getId().equals(excludeId);
    }

    /**
     * 分页查询岗位类型列表（根据关键词）
     *
     * @param keyword  关键词
     * @param offset   偏移量
     * @param limit    每页大小
     * @return 岗位类型列表
     */
    public List<AimJobTypeDO> selectPageByKeyword(String keyword, Integer offset, Integer limit) {
        log.debug("分页查询岗位类型列表, keyword: {}, offset: {}, limit: {}", keyword, offset, limit);
        return baseMapper.selectPageByKeyword(keyword, offset, limit);
    }

    /**
     * 统计岗位类型总数（根据关键词）
     *
     * @param keyword 关键词
     * @return 总数
     */
    public Long countByKeyword(String keyword) {
        log.debug("统计岗位类型总数, keyword: {}", keyword);
        return baseMapper.countByKeyword(keyword);
    }

    /**
     * 分页查询岗位类型（封装完整分页逻辑）
     * <p>
     * 数据量预估：预计 < 1000 条（小表）
     * 分页方案：MyBatis-Plus 分页
     *
     * @param keyword  关键词
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public Page<AimJobTypeDO> pageByKeyword(String keyword, Integer pageNum, Integer pageSize) {
        log.debug("分页查询岗位类型, keyword: {}, pageNum: {}, pageSize: {}", keyword, pageNum, pageSize);

        int offset = (pageNum - 1) * pageSize;

        // 查询总数
        Long total = countByKeyword(keyword);

        // 查询列表
        List<AimJobTypeDO> records = selectPageByKeyword(keyword, offset, pageSize);

        Page<AimJobTypeDO> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(records);

        log.debug("分页查询岗位类型完成, 总数: {}, 当前页: {}", total, records.size());
        return page;
    }
}
