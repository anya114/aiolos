package com.edwin.aiolosregister.register;

import org.springframework.stereotype.Component;

import com.edwin.aiolosregister.ConfigRegister;
import com.edwin.aiolosregister.ConfigRegisterFactory;
import com.edwin.aiolosregister.entity.Environment;
import com.google.common.base.Preconditions;

/**
 * Zookeeper配置注册器的工厂
 * 
 * @author jinming.wu
 * @date 2015-4-8
 */
@Component("zkConfigRegisterFactory")
public class ZKConfigRegisterFactory implements ConfigRegisterFactory {

    @Override
    public ConfigRegister generateConfigRegister(Environment environment) throws Exception {

        Preconditions.checkNotNull(environment, "Environment is not exsit. ");

        String hosts = environment.getHosts();
        ConfigRegister configRegister = null;

        switch (environment.getRegisterType()) {
            case CUROTR:
                configRegister = new CuratorConfigRegister(hosts);
                break;
            case ZK:
                configRegister = new ZKConfigRegister(hosts);
                break;
            default:
                configRegister = new CuratorConfigRegister(hosts);
        }

        configRegister.init();

        return configRegister;
    }
}
