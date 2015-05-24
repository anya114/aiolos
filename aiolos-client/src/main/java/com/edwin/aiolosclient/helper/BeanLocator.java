package com.edwin.aiolosclient.helper;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author jinming.wu
 * @date 2015-3-30
 */
@Component
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
