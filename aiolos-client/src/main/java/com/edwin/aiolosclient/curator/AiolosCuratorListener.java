package com.edwin.aiolosclient.curator;

import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.aiolosclient.AiolosContext;
import com.edwin.aiolosclient.ConfigChanageListener;
import com.edwin.aiolosclient.Constants;
import com.edwin.aiolosclient.ZKPath;
import com.edwin.aiolosclient.helper.ByteHelper;
import com.google.common.base.Strings;

/**
 * 事件监听器（一旦有事件产生就会有触发，可以监听所有事件包括watch事件，watch事件只是curator事件的一种）
 * 
 * @author jinming.wu
 * @date 2015-5-22
 */
public class AiolosCuratorListener implements CuratorListener {

    private static final Logger logger = LoggerFactory.getLogger(AiolosCuratorListener.class);

    private CuratorWrapper      curatorWrapper;

    public AiolosCuratorListener(CuratorWrapper curatorWrapper) {
        this.curatorWrapper = curatorWrapper;
    }

    @Override
    public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {

        if (event == null) {
            return;
        }

        if (event.getType() == CuratorEventType.CLOSING) {
            logger.error("Zookeeper is closing. ");
        }
        if (event.getType() == CuratorEventType.WATCHED) {
            if (event.getWatchedEvent().getPath() != null) {
                processEvent(event.getWatchedEvent());
            }
        }
    }

    private void processEvent(WatchedEvent event) {

        ConcurrentMap<String, String> configs = AiolosContext.getInstance().getConfigs();

        ConcurrentMap<String, Long> timestamps = AiolosContext.getInstance().getTimestamps();

        if (event.getType() == EventType.NodeCreated || event.getType() == EventType.NodeDataChanged) {

            String key = ZKPath.getKeyByPath(event.getPath());
            if (Strings.isNullOrEmpty(key)) {
                return;
            }

            String tsPath = ZKPath.getTimestampPath(event.getPath());
            try {
                byte[] data = curatorWrapper.getData(tsPath, false);
                if (data != null) {
                    Long tsInZK = ByteHelper.getLong(data);
                    Long tsInMem = timestamps.get(event.getPath());
                    if (tsInMem == null || tsInZK > tsInMem) {
                        timestamps.put(event.getPath(), tsInZK);
                        data = curatorWrapper.getData(event.getPath(), true);
                        String newValue = null;
                        if (data != null) {
                            newValue = new String(data, Constants.CHARSET);
                            configs.put(key, newValue);
                        } else {
                            configs.remove(key);
                        }
                        if (curatorWrapper.getChangeListeners() != null) {
                            for (ConfigChanageListener change : curatorWrapper.getChangeListeners()) {
                                change.onChange(key, newValue);
                            }
                        }
                    } else {

                        // 若timestamp没有过期，则不做变动，
                        curatorWrapper.watch(event.getPath());
                    }
                } else {
                    curatorWrapper.watch(event.getPath());
                }
            } catch (Exception e) {
                try {
                    curatorWrapper.watch(event.getPath());
                } catch (Exception e1) {
                    logger.error("Process event {} path {} exception. " + event.getType(), event.getPath(), e1);
                }
            }

        } else if (event.getType() == EventType.NodeDeleted) {
            String key = ZKPath.getKeyByPath(event.getPath());
            configs.remove(key);
            String tsPath = ZKPath.getTimestampPath(event.getPath());
            timestamps.remove(tsPath);
            try {
                curatorWrapper.watch(event.getPath());
            } catch (Exception e) {
                logger.error("Process event {} path {} exception. " + event.getType(), event.getPath(), e);
            }
        } else if (event.getType() == EventType.NodeChildrenChanged) {

        }
    }
}
