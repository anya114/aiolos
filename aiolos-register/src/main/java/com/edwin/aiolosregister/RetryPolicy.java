package com.edwin.aiolosregister;

import org.apache.zookeeper.ZooKeeper;

/**
 * 链接断开重试策略
 * 
 * @author: edwin
 * @date: 15-5-7 15:32
 */
public interface RetryPolicy {

    /**
     * Reconnect by old zookeeper
     * 
     * @param oldZK
     * @throws Exception
     */
    public void reconnect(ZooKeeper oldZK) throws Exception;
}
