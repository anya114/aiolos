package com.edwin.aiolosclient.curator;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import com.edwin.aiolosclient.AiolosContext;
import com.edwin.aiolosclient.ConfigChanageListener;
import com.edwin.aiolosclient.Constants;
import com.edwin.aiolosclient.ZKPath;
import com.edwin.aiolosclient.helper.ByteHelper;

/**
 * 配置同步器（抽象接口太烦了）
 * 
 * @author jinming.wu
 * @date 2015-5-23
 */
public class ConfigSyncer {

    private CuratorWrapper curatorWrapper;

    public ConfigSyncer(CuratorWrapper curatorWrapper) {
        this.curatorWrapper = curatorWrapper;
    }

    /**
     * 同步zk的值到内存（每个配置下均有timestamp，根据时间戳来判断是否要同步）
     * 
     * @throws Exception
     */
    public void syncConfig() throws Exception {

        ConcurrentMap<String, String> configs = AiolosContext.getInstance().getConfigs();
        ConcurrentMap<String, Long> timestamps = AiolosContext.getInstance().getTimestamps();

        for (Entry<String, String> entry : configs.entrySet()) {
            synchronized (entry.getValue()) {
                String path = ZKPath.getPathByKey(entry.getKey());
                String tsPath = ZKPath.getTimestampPath(path);
                byte[] data = curatorWrapper.getData(tsPath, false);
                if (data != null) {
                    Long tsInZK = ByteHelper.getLong(data);
                    Long tsInMem = timestamps.get(path);
                    if (tsInMem == null || tsInZK > tsInMem) {

                        // reset timestamp in memoery
                        timestamps.put(path, tsInZK);
                        data = curatorWrapper.getData(path, true);
                        if (data != null) {
                            String value = new String(data, Constants.CHARSET);

                            if (!value.equals(entry.getValue())) {
                                entry.setValue(value);
                            }
                        } else {
                            entry.setValue("");
                        }

                        if (curatorWrapper.getChangeListeners() != null) {
                            for (ConfigChanageListener change : curatorWrapper.getChangeListeners()) {
                                change.onChange(entry.getKey(), entry.getValue());
                            }
                        }
                    } else {
                        curatorWrapper.watch(path);
                    }
                }
            }
        }
    }

    /**
     * 守护线程同步配置
     */
    public void startSyncThread(int syncInterval) {
        Thread syncThread = new Thread(new SyncWoker(syncInterval));
        syncThread.setDaemon(true);
        syncThread.start();
    }

    class SyncWoker implements Runnable {

        private long lastSyncTime;

        private int  syncInterval;

        public SyncWoker(int syncInterval) {
            lastSyncTime = System.currentTimeMillis();
        }

        @Override
        public void run() {
            int i = 0;
            while (true) {
                try {
                    long now = System.currentTimeMillis();
                    if (curatorWrapper.getIsConnected().get() && (now - lastSyncTime > syncInterval)) {
                        syncConfig();
                        lastSyncTime = now;
                    } else {
                        Thread.sleep(1000);
                    }
                    i = 0;
                } catch (Exception e) {
                    i++;
                    if (i > 3) {
                        try {
                            Thread.sleep(5000);
                            i = 0;
                        } catch (InterruptedException ie) {
                            break;
                        }
                    }
                }
            }
        }
    }
}
