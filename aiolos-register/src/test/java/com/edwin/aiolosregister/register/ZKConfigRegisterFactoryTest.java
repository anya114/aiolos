package com.edwin.aiolosregister.register;

import org.junit.Test;

import com.edwin.aiolosregister.ConfigRegister;
import com.edwin.aiolosregister.ConfigRegisterFactory;
import com.edwin.aiolosregister.entity.Environment;

/**
 * @author: edwin
 * @date: 15-5-11 11:57
 */
public class ZKConfigRegisterFactoryTest {

    @Test
    public void generateConfigRegisterTest() throws Exception {

        ConfigRegisterFactory configRegisterFactory = new ZKConfigRegisterFactory();

        Environment environment = new Environment();

        environment.setHosts("192.168.7.41:2181");

        ConfigRegister configRegister = configRegisterFactory.generateConfigRegister(environment);

        configRegister.registerValue("test","test23131");
    }
}
