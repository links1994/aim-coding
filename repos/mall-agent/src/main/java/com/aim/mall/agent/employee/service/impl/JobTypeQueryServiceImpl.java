package com.aim.mall.agent.employee.service.impl;

import com.aim.mall.agent.employee.domain.dto.JobTypePageQuery;
import com.aim.mall.agent.employee.domain.entity.AimJobTypeDO;
import com.aim.mall.agent.employee.service.JobTypeQueryService;
import com.aim.mall.agent.employee.service.mp.AimJobTypeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 岗位类型查询服务实现
 * <p>
 * 只读查询，封装查询逻辑
 *
 * @author AI Agent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobTypeQueryServiceImpl implements JobTypeQueryService {

    private final AimJobTypeService aimJobTypeService;

    @Override
    public Page<AimJobTypeDO> pageJobType(JobTypePageQuery query) {
        log.debug("分页查询岗位类型列表, keyword: {}, pageNum: {}, pageSize: {}",
                query.getKeyword(), query.getPageNum(), query.getPageSize());

        Page<AimJobTypeDO> page = aimJobTypeService.pageByKeyword(
                query.getKeyword(), query.getPageNum(), query.getPageSize());

        log.debug("分页查询岗位类型列表完成, 总数: {}, 当前页: {}",
                page.getTotal(), page.getRecords().size());
        return page;
    }

    @Override
    public AimJobTypeDO getById(Long id) {
        log.debug("根据ID查询岗位类型, id: {}", id);
        return aimJobTypeService.getById(id);
    }

    @Override
    public AimJobTypeDO getByCode(String code) {
        log.debug("根据编码查询岗位类型, code: {}", code);
        return aimJobTypeService.getByCode(code);
    }
}
