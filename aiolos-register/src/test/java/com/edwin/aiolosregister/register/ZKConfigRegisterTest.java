package com.edwin.aiolosregister.register;

import org.junit.Test;

/**
 * @author: edwin
 * @date: 15-5-11 13:31
 */
public class ZKConfigRegisterTest {

    private ZKConfigRegister zkConfigRegister;

    @Test
    public void testRegisterValue() throws Exception {

    }

    @Test
    public void testUnregister() throws Exception {

        zkConfigRegister = new ZKConfigRegister("192.168.7.41:2181");

        zkConfigRegister.init();

        zkConfigRegister.unregister("test");
    }

    @Test
    public void testDestroy() throws Exception {

    }
}
