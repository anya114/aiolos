package com.edwin.aiolosclient.spring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import lombok.Setter;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionVisitor;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.util.StringValueResolver;

import com.edwin.aiolosclient.AiolosContext;
import com.edwin.aiolosclient.BeansInitException;
import com.edwin.aiolosclient.Constants;
import com.edwin.aiolosclient.Environment;
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

    /** 是否使用本地属性覆盖 */
    @Setter
    private boolean        useLocalProps;

    /** 本地配置路径，多个以逗号分隔 */
    @Setter
    private String         propertiesPaths;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configBeanFactory) throws BeansException {

        try {
            init(configBeanFactory);
        } catch (Exception e) {
            throw new BeansInitException("Init bean exception. ", e);
        }
    }

    private void init(ConfigurableListableBeanFactory configBeanFactory) throws Exception {

        // 初始化本地配置
        initLocalProps();

        // 初始化zk客户端
        initZKClient();

        initBean(configBeanFactory);
    }

    private void initBean(ConfigurableListableBeanFactory configBeanFactory) {

        StringValueResolver valueResolver;
        List<String> keyList = new ArrayList<String>();
        for (Object key : localProps.keySet()) {
            String value = localProps.getProperty((String) key);
            if (!(value.startsWith(Constants.DEFAULT_PLACEHOLDER_PREFIX) && value.endsWith(Constants.DEFAULT_PLACEHOLDER_SUFFIX))) {
                keyList.add((String) key);
            }
        }
        for (String key : keyList) {
            this.localProps.remove(key);
        }
        valueResolver = new PropertyReplaceResolver(curatorWrapper, this.localProps);

        String[] beanNames = configBeanFactory.getBeanDefinitionNames();
        if (ArrayUtils.isEmpty(beanNames)) {
            logger.info("No bean defined. ");
            return;
        }

        BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);
        for (int i = 0; i < beanNames.length; i++) {
            if (!(beanNames[i].equals(beanName) && configBeanFactory.equals(this.beanFactory))) {
                BeanDefinition beanDefine = configBeanFactory.getBeanDefinition(beanNames[i]);
                MutablePropertyValues mpvs = beanDefine.getPropertyValues();
                PropertyValue[] pvs = mpvs.getPropertyValues();
                if (!ArrayUtils.isEmpty(pvs)) {
                    for (PropertyValue pv : pvs) {
                        Object value = pv.getValue();
                        if (value instanceof TypedStringValue) {
                            String value_ = ((TypedStringValue) value).getValue();
                            if (value_.startsWith("${") && value_.endsWith("}")) {
                                value_ = value_.substring(2);
                                value_ = value_.substring(0, value_.length() - 1);
                            }
                        }
                    }
                }
                visitor.visitBeanDefinition(beanDefine);
            }
        }

        configBeanFactory.resolveAliases(valueResolver);
    }

    private void initZKClient() {
        curatorWrapper = CuratorWrapper.getInstance(Environment.getZkserver());
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

        AiolosContext.getInstance().setLocalProps(localProps);
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
