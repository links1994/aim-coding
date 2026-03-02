package com.aim.mall.basic.api.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * ID生成响应
 *
 * @author AI Agent
 */
@Data
public class IdGenApiResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 生成的完整编码
     */
    private String code;

    /**
     * 业务前缀
     */
    private String prefix;

    /**
     * 日期部分
     */
    private String datePart;

    /**
     * 序号部分
     */
    private String sequence;
}
