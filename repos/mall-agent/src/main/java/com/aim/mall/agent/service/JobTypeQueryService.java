package com.aim.mall.agent.service;

import com.aim.mall.agent.domain.dto.JobTypePageQuery;
import com.aim.mall.agent.domain.entity.AimJobTypeDO;
import com.aim.mall.agent.service.mp.AimJobTypeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位类型查询服务
 * <p>
 * 只读查询，封装查询逻辑
 *
 * @author AI Agent
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobTypeQueryService {

    private final AimJobTypeService aimJobTypeService;

    /**
     * 分页查询岗位类型列表
     * <p>
     * 数据量预估：预计 &lt; 1000 条（小表）
     * 分页方案：MyBatis-Plus 分页
     *
     * @param query 查询参数
     * @return 分页结果
     */
    public Page<AimJobTypeDO> pageJobType(JobTypePageQuery query) {
        log.debug("分页查询岗位类型列表, keyword: {}, pageNum: {}, pageSize: {}",
                query.getKeyword(), query.getPageNum(), query.getPageSize());

        int offset = (query.getPageNum() - 1) * query.getPageSize();

        // 查询总数
        Long total = aimJobTypeService.getBaseMapper().countByKeyword(query.getKeyword());

        // 查询列表
        List<AimJobTypeDO> records = aimJobTypeService.getBaseMapper()
                .selectPageByKeyword(query.getKeyword(), offset, query.getPageSize());

        Page<AimJobTypeDO> page = new Page<>(query.getPageNum(), query.getPageSize(), total);
        page.setRecords(records);

        log.debug("分页查询岗位类型列表完成, 总数: {}, 当前页: {}", total, records.size());
        return page;
    }

    /**
     * 根据ID查询岗位类型
     *
     * @param id 岗位类型ID
     * @return 岗位类型实体
     */
    public AimJobTypeDO getById(Long id) {
        log.debug("根据ID查询岗位类型, id: {}", id);
        return aimJobTypeService.getById(id);
    }

    /**
     * 根据编码查询岗位类型
     *
     * @param code 岗位类型编码
     * @return 岗位类型实体
     */
    public AimJobTypeDO getByCode(String code) {
        log.debug("根据编码查询岗位类型, code: {}", code);
        return aimJobTypeService.getByCode(code);
    }
}
