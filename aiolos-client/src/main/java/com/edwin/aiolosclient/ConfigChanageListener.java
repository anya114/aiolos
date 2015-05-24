package com.edwin.aiolosclient;

/**
 * @author jinming.wu
 * @date 2015-5-23
 */
public interface ConfigChanageListener {

    /**
     * 配置改变时触发事件
     * 
     * @param key
     * @param newValue
     */
    public void onChange(String key, String value);
}
