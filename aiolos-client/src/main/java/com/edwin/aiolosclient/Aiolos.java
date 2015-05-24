package com.edwin.aiolosclient;

import com.edwin.aiolosclient.curator.CuratorWrapper;

/**
 * 门面类（读取zk配置）
 * 
 * @author jinming.wu
 * @date 2015-5-24
 */
public class Aiolos {

    // 单例
    private CuratorWrapper curatorWrapper;

    public Aiolos(String connectionString) {
        curatorWrapper = CuratorWrapper.getInstance(connectionString);
        if (!curatorWrapper.getIsConnected().get()) {
            curatorWrapper.init();
        }
    }

    public Long getLongProperty(String key) {
        String value = curatorWrapper.getProperty(key);
        if (value == null) {
            return null;
        }
        return Long.parseLong(value);
    }

    public Integer getIntProperty(String key) {
        String value = curatorWrapper.getProperty(key);
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public Short getShortProperty(String key) {
        String value = curatorWrapper.getProperty(key);
        if (value == null) {
            return null;
        }
        return Short.parseShort(value);
    }

    public Byte getByteProperty(String key) {
        String value = curatorWrapper.getProperty(key);
        if (value == null) {
            return null;
        }
        return Byte.parseByte(value);
    }

    public Float getFloatProperty(String key) {
        String value = curatorWrapper.getProperty(key);
        if (value == null) {
            return null;
        }
        return Float.parseFloat(value);
    }

    public Double getDoubleProperty(String key) {
        String value = curatorWrapper.getProperty(key);
        if (value == null) {
            return null;
        }
        return Double.parseDouble(value);
    }

    public Boolean getBooleanProperty(String key) {
        String value = curatorWrapper.getProperty(key);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }
}
