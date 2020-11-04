/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.controller;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.alibaba.csp.sentinel.dashboard.auth.AuthAction;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.PrivilegeType;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicEnums;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.util.ParamCheckUtils;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.util.StringUtil;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.repository.rule.InMemDegradeRuleStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author leyou
 */
@Controller
@RequestMapping(value = "/degrade", produces = MediaType.APPLICATION_JSON_VALUE)
public class DegradeController {

    private final Logger logger = LoggerFactory.getLogger(DegradeController.class);

    @Autowired
    private InMemDegradeRuleStore repository;

    @Autowired
    @Qualifier("dynamicRuleNacosProvider")
    private DynamicRuleProvider<List<DegradeRuleEntity>> ruleProvider;

    @Autowired
    @Qualifier("dynamicRuleNacosPublisher")
    private DynamicRulePublisher<List<DegradeRuleEntity>> rulePublisher;

    @ResponseBody
    @RequestMapping("/rules.json")
    @AuthAction(PrivilegeType.READ_RULE)
    public Result<List<DegradeRuleEntity>> queryMachineRules(String app, String ip, Integer port) {
        Result<List<DegradeRuleEntity>> checkResult = ParamCheckUtils.checkBasicParams(app, ip, port);
        if (checkResult != null) {
            return checkResult;
        }

        try {
            List<DegradeRuleEntity> rules = ruleProvider.getRules(app, DynamicEnums.Rule.DEGRADE);
            rules = repository.saveAll(rules);
            return Result.ofSuccess(rules);
        } catch (Throwable throwable) {
            logger.error("queryApps error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
    }

    @ResponseBody
    @RequestMapping("/new.json")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<DegradeRuleEntity> add(String app, String ip, Integer port, String limitApp, String resource,
                                         Double count, Integer timeWindow, Integer grade) {

        Result<DegradeRuleEntity> checkResult = ParamCheckUtils.checkBasicParams(app, ip, port);
        if (checkResult != null) {
            return checkResult;
        }
        if (StringUtil.isBlank(limitApp)) {
            return Result.ofFail(-1, "limitApp can't be null or empty");
        }
        if (StringUtil.isBlank(resource)) {
            return Result.ofFail(-1, "resource can't be null or empty");
        }
        if (count == null) {
            return Result.ofFail(-1, "count can't be null");
        }
        if (timeWindow == null) {
            return Result.ofFail(-1, "timeWindow can't be null");
        }
        if (grade == null) {
            return Result.ofFail(-1, "grade can't be null");
        }
        if (grade < RuleConstant.DEGRADE_GRADE_RT || grade > RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT) {
            return Result.ofFail(-1, "Invalid grade: " + grade);
        }
        DegradeRuleEntity entity = new DegradeRuleEntity();
        entity.setApp(app.trim());
        entity.setIp(ip.trim());
        entity.setPort(port);
        entity.setLimitApp(limitApp.trim());
        entity.setResource(resource.trim());
        entity.setCount(count);
        entity.setTimeWindow(timeWindow);
        entity.setGrade(grade);
        Date date = new Date();
        entity.setGmtCreate(date);
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            publishRules(entity.getApp());
        } catch (Throwable throwable) {
            logger.error("Failed to add degrade rule:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        return Result.ofSuccess(entity);
    }

    @ResponseBody
    @RequestMapping("/save.json")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<DegradeRuleEntity> updateIfNotNull(Long id, String app, String limitApp, String resource,
                                                     Double count, Integer timeWindow, Integer grade) {
        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }
        if (grade != null) {
            if (grade < RuleConstant.DEGRADE_GRADE_RT || grade > RuleConstant.DEGRADE_GRADE_EXCEPTION_COUNT) {
                return Result.ofFail(-1, "Invalid grade: " + grade);
            }
        }
        DegradeRuleEntity entity = repository.findById(id);
        if (entity == null) {
            return Result.ofFail(-1, "id " + id + " dose not exist");
        }

        if (StringUtil.isNotBlank(app)) {
            entity.setApp(app.trim());
        }

        if (StringUtil.isNotBlank(limitApp)) {
            entity.setLimitApp(limitApp.trim());
        }
        if (StringUtil.isNotBlank(resource)) {
            entity.setResource(resource.trim());
        }
        if (count != null) {
            entity.setCount(count);
        }
        if (timeWindow != null) {
            entity.setTimeWindow(timeWindow);
        }
        if (grade != null) {
            entity.setGrade(grade);
        }
        Date date = new Date();
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            if (entity == null) {
                return Result.ofFail(-1, "save entity fail: null");
            }
            publishRules(entity.getApp());
            return Result.ofSuccess(entity);
        } catch (Throwable t) {
            Throwable e = t instanceof ExecutionException ? t.getCause() : t;
            assert entity != null;
            logger.error("Error when updating degrade rules, app={}, ip={}, ruleId={}", entity.getApp(),
                    entity.getIp(), id, e);
            return Result.ofFail(-1, e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("/delete.json")
    @AuthAction(PrivilegeType.DELETE_RULE)
    public Result<Long> delete(Long id) {
        if (id == null) {
            return Result.ofFail(-1, "id can't be null");
        }

        DegradeRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofSuccess(null);
        }
        try {
            repository.delete(id);
        } catch (Throwable throwable) {
            logger.error("delete error:", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        try {
            publishRules(oldEntity.getApp());
            return Result.ofSuccess(id);
        } catch (Throwable t) {
            Throwable e = t instanceof ExecutionException ? t.getCause() : t;
            logger.error("Error when deleting degrade rules, app={}, ip={}, id={}", oldEntity.getApp(),
                    oldEntity.getIp(), id, e);
            return Result.ofFail(-1, e.getMessage());
        }
    }

    private void publishRules(String app) throws Exception {
        List<DegradeRuleEntity> rules = repository.findAllByApp(app);
        rulePublisher.publish(app, rules, DynamicEnums.Rule.DEGRADE);
    }
}
