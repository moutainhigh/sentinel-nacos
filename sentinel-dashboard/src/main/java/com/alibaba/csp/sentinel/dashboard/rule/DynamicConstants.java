package com.alibaba.csp.sentinel.dashboard.rule;

/**
 * 动态配置 全局静态常量
 * @author 松果
 * @version 1.0
 * @date 2020/11/3 10:43
 */
public interface DynamicConstants {

    String CONFIG = "spring.cloud.sentinel.datasource.{rule}.{type}.{property}";

    String SERVER_ADDR = "server-addr";
    String DATA_ID = "data-id";
    String GROUP_ID = "group-id";
    String RULE_TYPE = "rule-type";
    String DATA_TYPE = "data-type";

}
