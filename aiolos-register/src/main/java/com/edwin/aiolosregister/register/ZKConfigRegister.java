package com.edwin.aiolosregister.register;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import lombok.Setter;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;

import com.edwin.aiolosregister.ZKCall;
import com.edwin.aiolosregister.ZKConstants;
import com.edwin.aiolosregister.helper.ByteHelper;
import com.google.common.collect.Maps;

/**
 * zookeeper配置注册器（添加配置到ZK集群）
 * 
 * @author jinming.wu
 * @date 2015-4-8
 */
public class ZKConfigRegister extends AbstractConfigRegister {

    // 封装后的zk
    @Setter
    private WrapperZookeeper zookeeper;

    // 顶级父节点是否存在
    private volatile boolean parentPathExists;

    private Object           lock           = new Object();

    // 已确认的路径
    private Set<String>      confirmedPaths = Collections.newSetFromMap(Maps.<String, Boolean> newConcurrentMap());

    public ZKConfigRegister(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public void init() throws Exception {
        this.zookeeper = new WrapperZookeeper(connectionString, sessionTimeout);
        this.creatingParentsIfNeeded(parentPath);
    }

    @Override
    public void registerValue(final String key, final String value) throws Exception {

        execute(new ZKCall() {

            @Override
            public Object call() throws Exception {

                set(parentPath + "/" + key, value);
                return null;
            }
        });
    }

    @Override
    public void unregister(String key) throws Exception {

        // 不需要同步，创建和注销没有同时进行的场景
        String path = parentPath + "/" + key;
        if (zookeeper.exists(path) != null) {
            List<String> children = zookeeper.getChildren(path);
            if (children != null && !children.isEmpty()) {
                for (String child : children) {
                    try {
                        zookeeper.delete(path + "/" + child, ZKConstants.LATEST_VERSION);
                    } catch (KeeperException.NoNodeException e) {
                        // do nothing
                    }
                }
            }
            zookeeper.delete(path, ZKConstants.LATEST_VERSION);
        }
    }

    public Object execute(ZKCall zkCall) throws Exception {

        // 保证父节点被创建
        creatingParentsIfNeeded(parentPath);

        try {
            return zkCall.call();
        } catch (Exception e) {
            confirmedPaths.clear();
            throw new Exception(e);
        }
    }

    private void set(String path, String value) throws Exception {
        this.set(path, ByteHelper.stringToBytes(value));
    }

    private void set(String path, byte[] bytes) throws Exception {

        // 不存在则新增
        if (zookeeper.exists(path) == null) {
            zookeeper.create(path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } else {

            // -1是针对数据的最新版本更新
            zookeeper.setData(path, bytes, ZKConstants.LATEST_VERSION);
        }
    }

    // 可用递归
    private void creatingParentsIfNeeded(String path) throws Exception {

        if (!parentPathExists) {

            int fromIndex;
            fromIndex = 1;
            int indexOfSlash = -1;
            while ((indexOfSlash = path.indexOf('/', fromIndex)) != -1) {

                creatingPathIfNeeded(path.substring(0, indexOfSlash));

                fromIndex = indexOfSlash + 1;
            }

            creatingPathIfNeeded(path);
            parentPathExists = true;
        }
    }

    private void creatingPathIfNeeded(String path) throws Exception {

        synchronized (lock) {
            if (!confirmedPaths.contains(path)) {
                if (zookeeper.exists(path) == null) {

                    // 父节点均为持久节点
                    zookeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
                confirmedPaths.add(path);
            }
        }
    }

    @Override
    public void destroy() throws InterruptedException {
        this.zookeeper.close();
    }
}
