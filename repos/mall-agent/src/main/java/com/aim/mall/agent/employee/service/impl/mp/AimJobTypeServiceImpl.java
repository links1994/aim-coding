package com.aim.mall.agent.employee.service.impl.mp;

import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.aim.mall.agent.employee.mapper.AimJobTypeMapper;
import com.aim.mall.agent.employee.service.mp.AimJobTypeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位类型MyBatis-Plus数据服务实现
 * <p>
 * 封装所有数据访问，包括MP方法和原生Mapper调用
 * 上层服务禁止直接使用 getBaseMapper()，必须通过本类封装方法间接访问
 *
 * @author AI Agent
 */
@Slf4j
@Service
public class AimJobTypeServiceImpl extends ServiceImpl<AimJobTypeMapper, AimJobTypeDO> implements AimJobTypeService {

    @Override
    public AimJobTypeDO getByCode(String code) {
        log.debug("根据编码查询岗位类型, code: {}", code);
        return baseMapper.selectByCode(code);
    }

    @Override
    public boolean isCodeExists(String code) {
        return getByCode(code) != null;
    }

    @Override
    public boolean isCodeExistsExcludeId(String code, Long excludeId) {
        AimJobTypeDO entity = getByCode(code);
        return entity != null && !entity.getId().equals(excludeId);
    }

    @Override
    public List<AimJobTypeDO> selectPageByKeyword(String keyword, Integer offset, Integer limit) {
        log.debug("分页查询岗位类型列表, keyword: {}, offset: {}, limit: {}", keyword, offset, limit);
        return baseMapper.selectPageByKeyword(keyword, offset, limit);
    }

    @Override
    public Long countByKeyword(String keyword) {
        log.debug("统计岗位类型总数, keyword: {}", keyword);
        return baseMapper.countByKeyword(keyword);
    }

    @Override
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
