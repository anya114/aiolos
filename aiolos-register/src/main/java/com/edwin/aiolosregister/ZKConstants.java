package com.edwin.aiolosregister;

/**
 * @author: edwin
 * @date: 15-5-9 20:43
 */
public class ZKConstants {

    // read from property
    public static int    DEFAULT_SESSION_TIMEOUT    = Integer.getInteger("config-default-session-timeout", 60 * 1000);

    public static int    DEFAULT_CONNECTION_TIMEOUT = Integer.getInteger("config-default-connection-timeout", 15 * 1000);

    public static int    BASE_SLEEP_MS              = Integer.getInteger("config-base-sleep-ms", 5 * 1000);

    public static int    MAX_TRY_TIMES              = Integer.getInteger("config-max-try-times", 3);

    public static String NAMESPACE                  = "aiolos";

    public static String BASE_PATH                  = "/aiolos";

    public static String PATH_CONFIGURATION         = BASE_PATH + "/config";

    public static String CHARSET                    = "UTF-8";

    public static int    LATEST_VERSION             = -1;
}
