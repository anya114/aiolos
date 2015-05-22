package com.edwin.aiolosclient;

import org.springframework.beans.BeansException;

/**
 * @author jinming.wu
 * @date 2015-5-12
 */
public class BeansInitException extends BeansException {

    private static final long serialVersionUID = 1L;

    public BeansInitException(String msg) {
        super(msg);
    }

    public BeansInitException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
