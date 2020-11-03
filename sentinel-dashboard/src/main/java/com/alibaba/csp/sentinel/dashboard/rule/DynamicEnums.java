package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;

/**
 * 动态配置枚举
 * @author 松果
 * @version 1.0
 * @date 2020/11/3 10:35
 */
public class DynamicEnums {

    /**
     * 数据源类型
     * @author 松果
     * @date 2020/11/3 10:42
     */
    public enum Type {
        Nacos("nacos");
        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * 规则类型
     * @author 松果
     * @date 2020/11/3 10:42
     */
    public enum Rule {
        FLOW(FlowRuleEntity.class, "flow"),
        DEGRADE(DegradeRuleEntity.class, "degrade"),
        SYSTEM(SystemRuleEntity.class, "system"),
        AUTHORITY(AuthorityRuleEntity.class, "authority"),
        PARAM_FLOW(ParamFlowRuleEntity.class, "param-flow"),
        GATEWAY_FLOW(GatewayFlowRuleEntity.class, "gw-flow");
        private Class entityClass;
        private String key;

        Rule(Class entityClass, String key) {
            this.entityClass = entityClass;
            this.key = key;
        }

        public <T> Class<T> getEntityClass() {
            return entityClass;
        }

        public String getKey() {
            return key;
        }
    }
}
