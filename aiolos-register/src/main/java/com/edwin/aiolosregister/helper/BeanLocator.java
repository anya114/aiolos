package com.edwin.aiolosregister.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author jinming.wu
 * @date 2015-3-30
 */
public class BeanLocator implements ApplicationContextAware{
    
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanLocator.applicationContext = applicationContext;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        return  (T) applicationContext.getBean(name);
    }
    
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
