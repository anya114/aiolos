package com.edwin.aiolosregister.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目表
 * 
 * @author: edwin
 * @date: 15-5-5 15:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Project extends Base {

    private static final long serialVersionUID = 1L;

    /** 项目名称 */
    private String            projectName;
}
