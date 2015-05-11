package com.edwin.aiolosregister.register;

import java.io.IOException;

import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.aiolosregister.RetryPolicy;

/**
 * 默认连接断开重试策略（失败即不断尝试重连）
 * 
 * @author: edwin
 * @date: 15-5-7 15:48
 */
public class DefaultRetryPolicy implements RetryPolicy {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRetryPolicy.class);

    private Object              lock   = new Object();

    private WrapperZookeeper    wrapperZK;

    public DefaultRetryPolicy(WrapperZookeeper wrapperZK) {
        this.wrapperZK = wrapperZK;
    }

    @Override
    public void reconnect(ZooKeeper oldZK) throws IOException, InterruptedException {

        logger.info("Zookeeper client is expired, try to reconnect. ");

        if (oldZK != null) {
            oldZK.close();
        }

        // 防止reconnect被并发调用
        synchronized (lock) {
            ZooKeeper newZK = wrapperZK.getZookeeper();
            if (oldZK == newZK) {
                try {

                    // reconnect by sessionId and sessionPasswd
                    newZK = new ZooKeeper(wrapperZK.getConnectString(), wrapperZK.getSessionTimeout(), wrapperZK,
                                          oldZK.getSessionId(), oldZK.getSessionPasswd());
                } catch (Exception e) {
                    logger.warn("Reconnect to zookeeper server by sessionId and sessionPasswd failed. ", e);
                    newZK = new ZooKeeper(wrapperZK.getConnectString(), wrapperZK.getSessionTimeout(), wrapperZK);
                }
            }
            wrapperZK.setZookeeper(newZK);
            oldZK = null;
        }
    }
}
