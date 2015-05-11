package com.edwin.aiolosregister.register;

import java.io.IOException;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.SessionExpiredException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.aiolosregister.RetryPolicy;
import com.edwin.aiolosregister.ZKCall;

/**
 * 包装zookeeper，所有方法的调用增加session失败重试策略（curator可以自动实现，但无法实现临时节点的恢复）
 * 
 * @author: edwin
 * @date: 15-5-7 15:15
 */
public class WrapperZookeeper implements Watcher {

    private static final Logger logger = LoggerFactory.getLogger(WrapperZookeeper.class);

    @Setter
    @Getter
    private String              connectString;

    @Setter
    @Getter
    private Watcher             watcher;

    @Setter
    @Getter
    private int                 sessionTimeout;

    @Setter
    @Getter
    private volatile ZooKeeper  zookeeper;

    @Setter
    @Getter
    private RetryPolicy         retryPolicy;

    public WrapperZookeeper(String connectString, int sessionTimeout) throws IOException {
        this(connectString, sessionTimeout, null, null);
    }

    /**
     * 构造ZK实例
     * 
     * @param connectString
     * @param sessionTimeout
     * @param watcher
     * @param retryPolicy 重试策略
     * @throws IOException
     */
    public WrapperZookeeper(String connectString, int sessionTimeout, Watcher watcher, RetryPolicy retryPolicy)
                                                                                                               throws IOException {
        this.connectString = connectString;
        this.sessionTimeout = sessionTimeout;
        this.watcher = watcher;
        this.zookeeper = new ZooKeeper(connectString, sessionTimeout, this);
        this.retryPolicy = retryPolicy == null ? new DefaultRetryPolicy(this) : retryPolicy;
    }

    @Override
    public void process(WatchedEvent event) {

        // 链接断开或session过期时会发出事件
        if (event.getType() == Event.EventType.None && event.getState() == Event.KeeperState.Expired) {
            try {
                this.retryPolicy.reconnect(this.zookeeper);
            } catch (Exception e) {
                logger.warn("Reconnect to zookeeper cluster failed while session expired.", e);
            }
        }
    }

    /**
     * 不提供 true or false, 一旦session过期默认的watcher将不可恢复
     * 
     * @param path
     * @return
     * @throws Exception
     */
    public Stat exists(final String path) throws Exception {
        return exists(path, null);
    }

    public Stat exists(final String path, final Watcher watcher) throws Exception {

        return (Stat) execute(new ZKCall() {

            @Override
            public Object call() throws Exception {
                return zookeeper.exists(path, watcher);
            }
        });
    }

    /**
     * 返回更新数据后的版本信息
     * 
     * @param path
     * @param data
     * @param version
     * @return
     * @throws Exception
     */
    public Stat setData(final String path, final byte[] data, final int version) throws Exception {
        return (Stat) execute(new ZKCall() {

            @Override
            public Object call() throws Exception {
                return zookeeper.setData(path, data, version);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<String> getChildren(final String path) throws Exception {
        return (List<String>) execute(new ZKCall() {

            @Override
            public Object call() throws Exception {
                return zookeeper.getChildren(path, false);
            }
        });
    }

    public void delete(final String path, final int version) throws Exception {
        execute(new ZKCall() {

            @Override
            public Object call() throws Exception {
                zookeeper.delete(path, version);
                return null;
            }
        });
    }

    public byte[] getData(final String path, final Watcher watcher, final Stat stat) throws Exception {
        return (byte[]) execute(new ZKCall() {

            @Override
            public Object call() throws Exception {
                byte[] data = zookeeper.getData(path, watcher, stat);
                return data;
            }
        });
    }

    public byte[] getData(final String path, final Stat stat) throws Exception {
        return (byte[]) execute(new ZKCall() {

            @Override
            public Object call() throws Exception {
                return zookeeper.getData(path, false, stat);
            }
        });
    }

    public String create(final String path, final byte data[], final List<ACL> acl, final CreateMode createMode)
                                                                                                                throws Exception {
        return (String) execute(new ZKCall() {

            @Override
            public Object call() throws Exception {
                return zookeeper.create(path, data, acl, createMode);
            }
        });
    }

    public void create(final String path, final byte data[], final List<ACL> acl, final CreateMode createMode,
                       final AsyncCallback.StringCallback cb, final Object ctx) throws Exception {
        execute(new ZKCall() {

            @Override
            public Object call() throws Exception {
                zookeeper.create(path, data, acl, createMode, cb, ctx);
                return null;
            }
        });
    }

    /**
     * 统一调用
     * 
     * @param zkCall
     * @return
     */
    private Object execute(ZKCall zkCall) throws Exception {

        ZooKeeper oldZK = this.zookeeper;

        try {
            return zkCall.call();
        } catch (SessionExpiredException e) {
            logger.warn("Execute zookeeper operation failed of session expiration, try it again with reconnect. ", e);
            this.retryPolicy.reconnect(oldZK);
            return zkCall.call();
        }
    }

    public void close() throws InterruptedException {
        zookeeper.close();
    }
}
