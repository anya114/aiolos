package com.edwin.aiolosregister.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.edwin.aiolosregister.RegisterType;

/**
 * 开发环境表（测试、线上等）
 * 
 * @author jinming.wu
 * @date 2015-4-8
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Environment extends Base {

    private static final long serialVersionUID = 1L;

    /** 环境名称 eg.online */
    private String            name;

    /** 环境中文标识 eg.线上 */
    private String            label;

    /** 环境对应的一个zookeeper集群{host1:port,host2:port} */
    private String            hosts;

    /** 注册器类型 */
    private RegisterType      registerType;
}
