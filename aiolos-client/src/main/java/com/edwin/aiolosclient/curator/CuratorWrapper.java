package com.edwin.aiolosclient.curator;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.aiolosclient.AiolosContext;
import com.edwin.aiolosclient.ConfigChanageListener;
import com.edwin.aiolosclient.Constants;
import com.edwin.aiolosclient.ZKPath;
import com.edwin.aiolosclient.helper.ByteHelper;
import com.google.common.collect.Lists;

/**
 * 客户端操作的封装
 * 
 * @author jinming.wu
 * @date 2015-5-22
 */
public class CuratorWrapper {

    private static final Logger         logger            = LoggerFactory.getLogger(CuratorWrapper.class);

    private CuratorFramework            curatorClient;

    @Getter
    private AtomicBoolean               isConnected       = new AtomicBoolean(false);

    @Setter
    private int                         sessionTimeout    = Constants.DEFAULT_SESSION_TIMEOUT;

    @Setter
    private RetryPolicy                 retryPolicy;

    @Getter
    private List<ConfigChanageListener> changeListeners   = Lists.newArrayList();

    /** 同步时间间隔 */
    @Setter
    private int                         syncInterval      = Constants.DEFAULT_SYNC_INTERVAL;

    @Setter
    private int                         connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;

    private ConfigSyncer                configSyncer;

    private String                      connectionString;

    private static CuratorWrapper       instance;

    private CuratorWrapper(String connectionString) {
        this.connectionString = connectionString;
    }

    public static CuratorWrapper getInstance(String connectionString) {

        if (instance == null) {
            synchronized (CuratorWrapper.class) {
                if (instance == null) {
                    instance = new CuratorWrapper(connectionString);
                }
            }
        }

        return instance;
    }

    public void init() {
        if (isConnected.get()) {
            synchronized (isConnected) {
                if (isConnected.get()) {

                    configSyncer = new ConfigSyncer(this);

                    retryPolicy = new ExponentialBackoffRetry(Constants.BASE_SLEEP_MS, Constants.MAX_TRY_TIMES);
                    curatorClient = CuratorFrameworkFactory.builder().connectString(connectionString).sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout).retryPolicy(retryPolicy).build();

                    curatorClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {

                        @Override
                        public void stateChanged(CuratorFramework client, ConnectionState newState) {
                            if (newState == ConnectionState.CONNECTED) {
                                isConnected.set(true);
                            } else if (newState == ConnectionState.RECONNECTED) {
                                isConnected.set(true);

                                // 重连需要从zk同步内存数据
                                try {
                                    configSyncer.syncConfig();
                                } catch (Exception e) {
                                    logger.error("Sync data from zookeepr fail. ", e);
                                }
                            } else {
                                isConnected.set(false);
                                logger.error("Lost connection to zookeeper. ");
                            }
                        }
                    });
                    curatorClient.getCuratorListenable().addListener(new AiolosCuratorListener(this));
                    curatorClient.start();
                    configSyncer.startSyncThread(syncInterval);
                }
            }
        }
    }

    public String getProperty(String key) {

        String value = null;
        Properties localProps = AiolosContext.getInstance().getLocalProps();
        if (localProps != null) {
            value = localProps.getProperty(key);
            if (value != null && !value.startsWith("${") && !value.endsWith("}")) {
                return value;
            }
            if (value != null && value.startsWith("${") && value.endsWith("}")) {
                value = value.substring(2);
                value = value.substring(0, value.length() - 1);
                return getValue(key);
            }
        }

        if (value == null) {
            value = getValue(key);
        }

        return value;
    }

    public String getValue(String key) {

        ConcurrentMap<String, String> configs = AiolosContext.getInstance().getConfigs();

        String value = configs.get(key);

        if (value == null) {
            try {
                this.getValue(key);
            } catch (Exception e) {
                logger.error("Fail to get value by {}", key, e);
            }
        }

        return value;
    }

    public String getZKValue(String key) throws Exception {

        String path = ZKPath.getPathByKey(key);
        String timestampPath = ZKPath.getTimestampPath(path);
        String value = null;

        byte[] data = this.getData(path, true);
        if (data != null) {
            value = new String(data, Constants.CHARSET);
            AiolosContext.getInstance().getConfigs().put(key, value);
            data = this.getData(timestampPath, false);
            if (data != null) {
                Long timestamp = ByteHelper.getLong(data);
                AiolosContext.getInstance().getTimestamps().put(path, timestamp);
            }
        } else {
            AiolosContext.getInstance().getConfigs().put(key, "");
        }
        return value;
    }

    public void watch(final String path) throws Exception {

        execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                curatorClient.checkExists().watched().forPath(path);
                return null;
            }
        });
    }

    public byte[] getData(final String path, final boolean watched) throws Exception {

        return (byte[]) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                if (watched) {
                    return curatorClient.getData().watched().forPath(path);
                }

                return curatorClient.getData().forPath(path);
            }
        });
    }

    public boolean exists(final String path, final boolean watched) {

        return (Boolean) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                Stat stat = null;
                if (watched) {
                    stat = curatorClient.checkExists().watched().forPath(path);
                } else {
                    stat = curatorClient.checkExists().forPath(path);
                }
                return stat != null;
            }
        });
    }

    private Object execute(Operation operation) {

        // 失败后等待重连
        if (!isConnected.get()) {
            logger.error("Lost connection to zookeeper. wait to auto reconnecting...");
            return null;
        }

        Object result = null;
        try {
            result = operation.execute();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    interface Operation {

        Object execute() throws Exception;
    }

    public synchronized void addChangeListener(ConfigChanageListener change) {
        this.changeListeners.add(change);
    }

    public synchronized void removeChangeListener(ConfigChanageListener change) {
        this.changeListeners.remove(change);
    }
}
