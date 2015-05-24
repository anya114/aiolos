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

    public static String getKeyByPath(String path) {
        if (path == null || !path.startsWith(Constants.CONFIG_PATH)) {
            return null;
        }
        String key = path.substring(Constants.CONFIG_PATH.length() + 1);
        int idx = key.indexOf(Constants.SEPARATOR);
        if (idx != -1) {
            key = key.substring(0, idx);
        }
        return key;
    }
}
