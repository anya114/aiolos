package com.edwin.aiolosclient;

import java.io.IOException;
import java.util.Properties;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.common.tools.io.ResourceHelper;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * 配置所处环境加载类
 * 
 * @author jinming.wu
 * @date 2015-5-11
 */
public class Environment {

    private static Logger     logger = LoggerFactory.getLogger(Environment.class);

    /** environment eg.dev */
    @Setter
    @Getter
    private static String     deployenv;

    /** serverips eg.192.168.1.11:2181 */
    @Setter
    @Getter
    private static String     zkserver;

    @Getter
    private static Properties envProperties;

    static {
        try {
            envProperties = ResourceHelper.getProperties(Constants.ENV_FILE_PATH);
        } catch (IOException e) {
            logger.warn("Read properties exception. ", e);
        }

        if (envProperties == null) {
            envProperties = defaultProperties();
        }

        deployenv = envProperties.getProperty(Constants.KEY_DEPLOYENV);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(deployenv), Constants.KEY_DEPLOYENV + " is empty. ");

        zkserver = envProperties.getProperty(Constants.KEY_ZKSERVER);

        Preconditions.checkArgument(!Strings.isNullOrEmpty(zkserver), Constants.KEY_ZKSERVER + " is empty. ");
    }

    public static Properties defaultProperties() {
        Properties properties = new Properties();
        properties.put(Constants.KEY_DEPLOYENV, Constants.DEFAULT_DEPLOYENV);
        properties.put(Constants.KEY_ZKSERVER, Constants.DEFAULT_ZKSERVER);
        return properties;
    }
}
