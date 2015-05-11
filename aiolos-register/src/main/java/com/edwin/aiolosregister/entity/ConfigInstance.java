package com.edwin.aiolosregister.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 配置项的实例（主要是一个配置项可能对应多个环境，在每个环境中配置项即是一个配置实例）
 * 
 * @author: edwin
 * @date: 15-5-5 15:52
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ConfigInstance extends Base {

    private static final long serialVersionUID = 1L;

    /** 配置项ID eg.config.id */
    private int               configId;

    /** 环境ID eg.environment.id */
    private int               envId;

    /** 配置项在当前环境下的描述 */
    private String            desc;

    /** 配置项在当前环境下的值 */
    private String            Value;
}
