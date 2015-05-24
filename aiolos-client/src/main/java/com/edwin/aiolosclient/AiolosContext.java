package com.edwin.aiolosclient;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

/**
 * 运行客户端上下文（单例）
 * 
 * @author jinming.wu
 * @date 2015-5-23
 */
public class AiolosContext {

    private static AiolosContext          instance   = new AiolosContext();

    // zk缓存在内存中的配置
    private ConcurrentMap<String, String> configs    = Maps.newConcurrentMap();

    private ConcurrentMap<String, Long>   timestamps = Maps.newConcurrentMap();

    private AiolosContext() {

    }

    public static AiolosContext getInstance() {
        return instance;
    }

    public ConcurrentMap<String, String> getConfigs() {
        return this.configs;
    }

    public ConcurrentMap<String, Long> getTimestamps() {
        return this.timestamps;
    }
}
