package com.aim.mall.basic.idgen.service;

import com.aim.mall.basic.idgen.domain.enums.DatePatternEnum;

/**
 * ID生成服务接口
 *
 * @author AI Agent
 */
public interface IdGenService {

    /**
     * 生成编码
     *
     * @param prefix      业务前缀，1-3位大写字母
     * @param datePattern 日期格式枚举
     * @return 生成的完整编码
     */
    String generateCode(String prefix, DatePatternEnum datePattern);
}
