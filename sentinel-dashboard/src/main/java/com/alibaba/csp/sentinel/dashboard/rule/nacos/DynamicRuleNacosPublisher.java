package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.rule.DynamicConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicConstants;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicEnums;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 松果
 * @version 1.0
 * @date 2020/11/3 10:58
 */
@Component("dynamicRuleNacosPublisher")
public class DynamicRuleNacosPublisher<T> implements DynamicRulePublisher<List<T>> {

    @Autowired
    private ConfigService configService;
    @Autowired
    private NacosConfig nacosConfig;

    @Override
    public void publish(String app, List<T> rules, DynamicEnums.Rule rule) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        configService.publishConfig(DynamicConfigUtil.getProperty(app, rule), nacosConfig.getGroupId(),
                JSON.toJSONString(rules));
    }
}