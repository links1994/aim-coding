package com.aim.mall.common.utils;

import com.aim.mall.common.exception.ApiException;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 根据header获取相关信息
 * </p>
 *
 * @author fei
 * @since 2025/5/25
 */
@Slf4j
public class UserInfoUtil {

    public static UserHeaderInfoDTO getUserInfo(String user) {
        if (ObjectUtils.isEmpty(user)) {
            throw new ApiException("非法入参, 用户信息为空");
        }
        try {
            String decoded = URLDecoder.decode(user, StandardCharsets.UTF_8);
            log.info("当前用户登录信息:{}", decoded);
            return JSON.parseObject(decoded, UserHeaderInfoDTO.class);
        } catch (Exception e) {
            log.error("parse user info error:{}", user, e);
            throw new ApiException("非法入参, 用户信息解析失败");
        }
    }

    @Data
    public static class UserHeaderInfoDTO {
        /**
         * 用户ID
         */
        private Long id;

        /**
         * 设备id
         */
        private String device_id;

        /**
         * 用户名
         */
        private String user_name;
        /**
         * 商家ID
         */
        private Long merchant_id;
        /**
         * 合作商id
         */
        private Long partner_id;

        /**
         * 客户端ID
         */
        private String client_id;
    }
}
