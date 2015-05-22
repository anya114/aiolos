package com.edwin.aiolosclient;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * spring unit
 * 
 * @author jinming.wu
 * @date 2015-5-11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:appcontext-*.xml")
public class BaseTest {
}
