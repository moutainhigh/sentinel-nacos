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
package com.alibaba.csp.sentinel.dashboard.rule;

/**
 * @author Eric Zhao
 * @since 1.4.0
 */
public interface DynamicRulePublisher<T> {

    /**
     * 将规则发布到给定应用程序名称的远程规则配置中心。，原方法增加规则枚举参数
     * @param app app Name
     * @param rules 要推送的规则集合
     * @param rule 规则枚举对象
     * @author 松果
     * @date 2020/11/3 10:46
     * @return void
     * @throws Exception if some error occurs
     */
    void publish(String app, T rules, DynamicEnums.Rule rule) throws Exception;
}
