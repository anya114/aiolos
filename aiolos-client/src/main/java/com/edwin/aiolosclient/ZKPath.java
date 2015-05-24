package com.edwin.aiolosclient;

/**
 * @author jinming.wu
 * @date 2015-5-23
 */
public class ZKPath {

    public static String getPathByKey(String key) {
        return Constants.CONFIG_PATH + Constants.SEPARATOR + key;
    }

    public static String getTimestampPath(String path) {
        return path + Constants.SEPARATOR + Constants.TIMESTAMP;
    }
}
