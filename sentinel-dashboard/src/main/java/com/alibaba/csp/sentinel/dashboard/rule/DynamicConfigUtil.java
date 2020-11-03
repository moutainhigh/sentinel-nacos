package com.alibaba.csp.sentinel.dashboard.rule;

import org.springframework.core.env.Environment;

/**
 * 动态配置工具
 * @author 松果
 * @version 1.0
 * @date 2020/11/3 10:44
 */
public final class DynamicConfigUtil {

    public static String getProperty(Environment environment, DynamicEnums.Type type, DynamicEnums.Rule rule, String property) {
        return environment.getProperty(DynamicConstants.CONFIG
                .replace("{type}", type.getName())
                .replace("{rule}", rule.getKey())
                .replace("{property}", property));
    }

}
