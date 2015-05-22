package com.edwin.aiolosclient;

/**
 * @author jinming.wu
 * @date 2015-5-11
 */
public class Constants {

    public static String       ENV_FILE_PATH              = "/data/webapps/appenv";

    public static final String KEY_DEPLOYENV              = "deployenv";

    public static final String KEY_ZKSERVER               = "zkserver";

    public static final String DEFAULT_DEPLOYENV          = "dev";

    public static final String DEFAULT_ZKSERVER           = "dev.lion.dp:2181";

    // read from property
    public static int          DEFAULT_SESSION_TIMEOUT    = Integer.getInteger("config-default-session-timeout",
                                                                               60 * 1000);

    public static int          DEFAULT_CONNECTION_TIMEOUT = Integer.getInteger("config-default-connection-timeout",
                                                                               15 * 1000);

    public static int          BASE_SLEEP_MS              = Integer.getInteger("config-base-sleep-ms", 5 * 1000);

    public static int          MAX_TRY_TIMES              = Integer.getInteger("config-max-try-times", 3);

    /** just for test */
    public static String       DEFAULT_CONNECTION         = "192.168.7.41:2181";

    public static String       NAMESPACE                  = "aiolos";

    public static String       BASE_PATH                  = "/aiolos";

    public static String       PATH_CONFIGURATION         = BASE_PATH + "/config";

    public static String       CHARSET                    = "UTF-8";
}
