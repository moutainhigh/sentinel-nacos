package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.rule.DynamicConfigUtil;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicConstants;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicEnums;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 远程配置获取
 * @author 松果
 * @version 1.0
 * @date 2020/11/3 10:56
 */
@Component("dynamicRuleNacosProvider")
public class DynamicRuleNacosProvider <T> implements DynamicRuleProvider<List<T>> {

    @Autowired
    private ConfigService configService;
    @Autowired
    private Environment environment;

    @Override
    public List<T> getRules(String appName, DynamicEnums.Rule rule) throws Exception {
        String rules = configService.getConfig(
                DynamicConfigUtil.getProperty(environment, DynamicEnums.Type.Nacos, rule, DynamicConstants.DATA_ID),
                DynamicConfigUtil.getProperty(environment, DynamicEnums.Type.Nacos, rule, DynamicConstants.GROUP_ID),
                3000);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return JSONArray.parseArray(rules, rule.getEntityClass());
    }
}
