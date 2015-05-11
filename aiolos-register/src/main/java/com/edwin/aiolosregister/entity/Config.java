package com.edwin.aiolosregister.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 配置项存储表
 * 
 * @author: edwin.
 * @date: 15-5-5 15:09.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Config extends Base {

    private static final long serialVersionUID = 1L;

    /** 配置项key eg.activity-web.config.key */
    private String            key;

    /** 配置项描述 */
    private String            desc;

    /** 配置项类型 eg.1-String 2-Number 3-Boolean 4-List 5-Map 6-Pojo 7-Ref */
    private int               type;

    /** 配置项所属的项目Id eg.project.id */
    private int               projectId;
}
