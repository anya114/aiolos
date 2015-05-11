package com.edwin.aiolosregister.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jinming.wu
 * @date 2015-4-8
 */
public class Base implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    protected int             id;

    @Setter
    @Getter
    protected Date            addTime;

    @Setter
    @Getter
    protected Date            updateTime;

}
