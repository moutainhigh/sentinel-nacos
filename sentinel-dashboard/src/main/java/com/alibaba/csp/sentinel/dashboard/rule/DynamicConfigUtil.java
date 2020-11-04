package com.alibaba.csp.sentinel.dashboard.rule;

import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * 动态配置工具
 * @author 松果
 * @version 1.0
 * @date 2020/11/3 10:44
 */
public final class DynamicConfigUtil {

    public static String getProperty(String appName, DynamicEnums.Rule rule) {
        return appName + DynamicConstants.DATA_ID_CLIENT + "-" + rule.getKey() +
                DynamicConstants.DATA_ID_POSTFIX;
    }

}
