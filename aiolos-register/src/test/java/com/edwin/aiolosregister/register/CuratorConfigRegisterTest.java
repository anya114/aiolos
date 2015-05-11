package com.edwin.aiolosregister.register;

import org.junit.Test;

/**
 * @author: edwin
 * @date: 15-5-11 13:25
 */
public class CuratorConfigRegisterTest {

    private CuratorConfigRegister curatorConfigRegister;

    @Test
    public void unregisterTest() throws Exception {

        curatorConfigRegister = new CuratorConfigRegister("192.168.7.41:2181");

        curatorConfigRegister.init();

        curatorConfigRegister.unregister("test");
    }
}
