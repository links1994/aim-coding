package com.aim.mall.agent.employee.service.mp;

import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 岗位类型MyBatis-Plus数据服务接口
 * <p>
 * 定义所有数据访问方法，包括MP方法和原生Mapper调用
 * 上层服务通过此接口调用，由 AimJobTypeServiceImpl 实现
 *
 * @author AI Agent
 */
public interface AimJobTypeService extends IService<AimJobTypeDO> {

    /**
     * 根据编码查询（排除已删除）
     *
     * @param code 岗位类型编码
     * @return 岗位类型实体
     */
    AimJobTypeDO getByCode(String code);

    /**
     * 检查编码是否已存在（排除已删除）
     *
     * @param code 岗位类型编码
     * @return 是否存在
     */
    boolean isCodeExists(String code);

    /**
     * 检查编码是否已存在（排除指定ID，用于更新场景）
     *
     * @param code 岗位类型编码
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean isCodeExistsExcludeId(String code, Long excludeId);

    /**
     * 分页查询岗位类型列表（根据关键词）
     *
     * @param keyword  关键词
     * @param offset   偏移量
     * @param limit    每页大小
     * @return 岗位类型列表
     */
    List<AimJobTypeDO> selectPageByKeyword(String keyword, Integer offset, Integer limit);

    /**
     * 统计岗位类型总数（根据关键词）
     *
     * @param keyword 关键词
     * @return 总数
     */
    Long countByKeyword(String keyword);

    /**
     * 分页查询岗位类型（封装完整分页逻辑）
     *
     * @param keyword  关键词
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<AimJobTypeDO> pageByKeyword(String keyword, Integer pageNum, Integer pageSize);
}
