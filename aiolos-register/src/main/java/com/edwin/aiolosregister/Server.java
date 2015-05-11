package com.edwin.aiolosregister;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * Fluent接口方式看着很不错，调试起来就坑爹了
 * 
 * @author: edwin
 * @date: 15-5-5 17:43
 */
public class Server implements Watcher {

    public static void main(String agrs[]) throws Exception {

        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("192.168.7.41:2181", 5000, 3000,
                                                                              new ExponentialBackoffRetry(1000, 3));

        curatorFramework.start();

        // 默认创建是持久节点
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/test1",
                                                                                                   "".getBytes());

        Stat stat = new Stat();

        curatorFramework.getData().storingStatIn(stat).forPath("/test/test1");

        System.out.println(stat.getVersion());

        // Thread.sleep(5000);
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.getType());
    }

    private void reConnect(ZooKeeper zooKeeper) {
        // 可以用sessionId和passwd来寻找服务端的session，不存在则返回SessionExpiredException
        long sessionId = zooKeeper.getSessionId();
        byte[] passwd = zooKeeper.getSessionPasswd();
    }

    class ICallBack implements AsyncCallback.StringCallback {

        @Override
        public void processResult(int rc, String path, Object ctx, String name) {

        }
    }
}
