package com.edwin.aiolosregister.register;

import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.aiolosregister.ConfigRegister;
import com.edwin.aiolosregister.ZKConstants;

/**
 * @author: edwin
 * @date: 15-5-9 20:47
 */
public abstract class AbstractConfigRegister implements ConfigRegister {

    protected final Logger logger            = LoggerFactory.getLogger(getClass());

    // ip1:port,ip2:port
    @Setter
    @Getter
    protected String       connectionString;

    // defaut namesapce
    @Setter
    @Getter
    protected String       namespace         = ZKConstants.NAMESPACE + "/CONFIGURATION";

    @Setter
    @Getter
    protected int          sessionTimeout    = ZKConstants.DEFAULT_SESSION_TIMEOUT;

    @Setter
    @Getter
    protected int          connectionTimeout = ZKConstants.DEFAULT_CONNECTION_TIMEOUT;

    @Setter
    @Getter
    protected String       parentPath        = ZKConstants.PATH_CONFIGURATION;
}
