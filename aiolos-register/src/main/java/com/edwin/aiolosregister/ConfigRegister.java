package com.edwin.aiolosregister;

/**
 * 配置注册器接口
 * 
 * @author jinming.wu
 * @date 2015-4-8
 */
public interface ConfigRegister {

    /**
     * 初始化
     * 
     * @throws Exception
     */
    public void init() throws Exception;

    /**
     * 向ZK注入value
     * 
     * @param key
     * @param value
     * @throws Exception
     */
    public void registerValue(String key, String value) throws Exception;

    /**
     * 注销某个配置
     * 
     * @param key
     * @throws Exception
     */
    public void unregister(String key) throws Exception;

    /**
     * 销毁实例
     */
    public void destroy() throws Exception;
}
