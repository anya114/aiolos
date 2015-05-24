package com.edwin.aiolosclient;

import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

/**
 * 运行客户端上下文（单例，内存中缓存的key-value）
 * 
 * @author jinming.wu
 * @date 2015-5-23
 */
public class AiolosContext {

    private static AiolosContext          instance   = new AiolosContext();

    // zk缓存在内存中的配置
    private ConcurrentMap<String, String> configs    = Maps.newConcurrentMap();

    private ConcurrentMap<String, Long>   timestamps = Maps.newConcurrentMap();

    private Properties                    localProps;

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

    public Properties getLocalProps() {
        return this.localProps;
    }

    public void setLocalProps(Properties localProps) {
        this.localProps = localProps;
    }
}
