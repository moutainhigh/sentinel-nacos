package com.alibaba.csp.sentinel.dashboard.util;

import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.util.StringUtil;

/**
 * 参数校验工具类
 * @author 松果
 * @version 1.0
 * @date 2020/11/4 14:06
 */
public class ParamCheckUtils {

    /**
     * 校验参数
     * @param app appName
     * @param ip  ip地址
     * @param port 端口号
     * @author 松果
     * @date 2020/11/4 14:06
     * @return com.alibaba.csp.sentinel.dashboard.domain.Result<R>
     */
    public static <R> Result<R> checkBasicParams(String app, String ip, Integer port) {
        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (StringUtil.isEmpty(ip)) {
            return Result.ofFail(-1, "ip can't be null or empty");
        }
        if (port == null) {
            return Result.ofFail(-1, "port can't be null");
        }
        if (port <= 0 || port > 65535) {
            return Result.ofFail(-1, "port should be in (0, 65535)");
        }
        return null;
    }
}
