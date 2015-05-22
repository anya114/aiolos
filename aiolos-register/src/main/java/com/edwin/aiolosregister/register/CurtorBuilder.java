package com.edwin.aiolosregister.register;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.edwin.aiolosregister.ZKConstants;
import com.google.common.base.Strings;

/**
 * @author jinming.wu
 * @date 2015-5-13
 */
public class CurtorBuilder {

    private CuratorFramework curatorFramework;

    private RetryPolicy      retryPolicy;

    private Builder          builder;

    private CurtorBuilder() {

        retryPolicy = new ExponentialBackoffRetry(ZKConstants.BASE_SLEEP_MS, ZKConstants.MAX_TRY_TIMES);

        builder = CuratorFrameworkFactory.builder();

        builder = builder.retryPolicy(retryPolicy);
    }

    // lazy-load and thread-safe
    private static class SingletonHolder {

        private static final CurtorBuilder INSTANCE = new CurtorBuilder();
    }

    public static CurtorBuilder getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Builder defaultBuilder(String connectString) {
        this.withConnectString(connectString).withSessionTimeout(0).withConnectTimeout(0).withNamespace(null);
        return builder;
    }

    public CurtorBuilder withConnectString(String connectString) {
        builder = builder.connectString(connectString);
        return this;
    }

    public CurtorBuilder withSessionTimeout(int sessionTimeout) {
        builder = builder.sessionTimeoutMs(sessionTimeout == 0 ? ZKConstants.DEFAULT_SESSION_TIMEOUT : sessionTimeout);
        return this;
    }

    public CurtorBuilder withConnectTimeout(int connectionTimeout) {
        builder = builder.connectionTimeoutMs(connectionTimeout == 0 ? ZKConstants.DEFAULT_CONNECTION_TIMEOUT : connectionTimeout);
        return this;
    }

    public CurtorBuilder withNamespace(String namespace) {
        builder = builder.namespace(Strings.isNullOrEmpty(namespace) ? (ZKConstants.NAMESPACE + "/CONFIGURATION") : namespace);
        return this;
    }

    public CuratorFramework build() {
        synchronized (this) {
            if (curatorFramework != null) {
                return this.curatorFramework;
            }
            return this.builder.build();
        }
    }
}
