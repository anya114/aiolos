package com.edwin.aiolosclient.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.edwin.aiolosclient.Constants;

/**
 * curator client生成工厂（单例）
 * 
 * @author jinming.wu
 * @date 2015-5-18
 */
public class CuratorFactory {

    private CuratorFramework curatorClient;

    private RetryPolicy      retryPolicy;

    private int              sessionTimeout    = Constants.DEFAULT_SESSION_TIMEOUT;

    private int              connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;

    private CuratorFactory() {

        // 重试策略
        if (retryPolicy == null) {
            retryPolicy = new ExponentialBackoffRetry(Constants.BASE_SLEEP_MS, Constants.MAX_TRY_TIMES);
        }
    }

    // lazy-load and thread-safe
    private static class SingletonHolder {

        private static final CuratorFactory INSTANCE = new CuratorFactory();
    }

    public static CuratorFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public CuratorFramework getCuratorClient(String connectionString) {

        // double check
        if (curatorClient == null) {
            synchronized (CuratorFactory.class) {
                if (curatorClient == null) {
                    curatorClient = CuratorFrameworkFactory.builder().connectString(connectionString).sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout).retryPolicy(retryPolicy).build();
                }
            }
        }

        return curatorClient;
    }
}
