package com.edwin.aiolosclient.spring;

import java.io.IOException;
import java.util.Properties;

import lombok.Setter;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.edwin.aiolosclient.BeansInitException;
import com.edwin.aiolosclient.curator.ConfigSyncer;
import com.edwin.aiolosclient.curator.CuratorWrapper;
import com.edwin.common.tools.io.ResourceHelper;
import com.google.common.base.Strings;

/**
 * spring加载时替换bean中的${x}变量，默认读取顺序：本地properties文件->zookeeper
 * 
 * @author jinming.wu
 * @date 2015-5-12
 */
public class PropertyReplaceBean implements BeanFactoryPostProcessor, BeanFactoryAware, BeanNameAware {

    private static Logger  logger = LoggerFactory.getLogger(PropertyReplaceBean.class);

    private CuratorWrapper curatorWrapper;

    private BeanFactory    beanFactory;

    private String         beanName;

    private Properties     localProps;

    /** zk集群连接地址 */
    @Setter
    private String         connectionString;

    /** 是否使用本地属性覆盖 */
    @Setter
    private boolean        useLocalProps;

    /** 本地配置路径，多个以逗号分隔 */
    @Setter
    private String         propertiesPaths;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configBeanFactory) throws BeansException {

        try {

            init();

        } catch (Exception e) {
            throw new BeansInitException("Init bean exception. ", e);
        }
    }

    private void init() throws IOException {

        // 初始化本地配置
        initLocalProps();

        // 初始化zk客户端
        initZKClient();
    }

    private void initZKClient() {
        curatorWrapper = CuratorWrapper.getInstance(connectionString);
        curatorWrapper.init();
    }

    private void initLocalProps() throws IOException {

        this.localProps = new Properties();

        if (!Strings.isNullOrEmpty(propertiesPaths)) {
            String[] paths = propertiesPaths.split(",");
            if (!ArrayUtils.isEmpty(paths)) {
                for (String path : paths) {
                    try {
                        this.localProps.load(ResourceHelper.getInputStream(path));
                    } catch (Exception e) {
                        logger.warn("Load properties fail. ", e);
                    }
                }
            }
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
