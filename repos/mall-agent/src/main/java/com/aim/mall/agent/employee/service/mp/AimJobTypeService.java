package com.aim.mall.agent.service.mp;

import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.aim.mall.agent.mapper.AimJobTypeMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 岗位类型MyBatis-Plus数据服务
 * <p>
 * 封装所有数据访问，包括MP方法和原生Mapper调用
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
}
