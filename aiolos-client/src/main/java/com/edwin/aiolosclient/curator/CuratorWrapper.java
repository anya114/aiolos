package com.edwin.aiolosclient.curator;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jinming.wu
 * @date 2015-5-22
 */
public class CuratorWrapper {

    private static final Logger           logger      = LoggerFactory.getLogger(CuratorWrapper.class);

    private CuratorFramework              curatorClient;

    private static volatile AtomicBoolean isConnected = new AtomicBoolean(false);

    public void init() {
        if (isConnected.get()) {
            synchronized (isConnected) {
                if (isConnected.get()) {

                    curatorClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {

                        @Override
                        public void stateChanged(CuratorFramework client, ConnectionState newState) {
                            if (newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED) {
                                isConnected.set(true);
                            } else {
                                isConnected.set(false);
                                logger.error("Lost connection to zookeeper. ");
                            }
                        }
                    });

                    curatorClient.getCuratorListenable().addListener(new AiolosCuratorListener());

                    curatorClient.start();
                }
            }
        }
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
}
