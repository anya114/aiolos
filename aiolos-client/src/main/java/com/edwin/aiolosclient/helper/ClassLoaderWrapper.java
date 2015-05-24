package com.edwin.aiolosclient.helper;

import java.io.InputStream;
import java.net.URL;

/**
 * ClassLoader包装类
 * 
 * @author jinming.wu
 * @date 2015-1-5
 */
public class ClassLoaderWrapper {

    private ClassLoader systemClassLoader;

    public ClassLoaderWrapper() {
        systemClassLoader = ClassLoader.getSystemClassLoader();
    }

    public Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(null));
    }

    public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(classLoader));
    }

    public URL getURL(String resource) {
        return getResourceAsURL(resource, getClassLoaders(null));
    }

    public URL getURL(String resource, ClassLoader classLoader) {
        return getResourceAsURL(resource, getClassLoaders(classLoader));
    }

    public InputStream getStream(String resource) {
        return getResourceAsStream(resource, getClassLoaders(null));
    }

    public InputStream getStream(String resource, ClassLoader classLoader) {
        return getResourceAsStream(resource, getClassLoaders(classLoader));
    }

    /**
     * 从资源获取输入流
     * 
     * @param resource
     * @param classLoader
     * @return
     */
    private InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {

        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                InputStream returnValue = cl.getResourceAsStream(resource);

                if (null == returnValue) {
                    returnValue = cl.getResourceAsStream("/" + resource);
                }
                if (null != returnValue) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    /**
     * 从资源处获取URL
     * 
     * @param resource
     * @param classLoader
     * @return
     */
    private URL getResourceAsURL(String resource, ClassLoader[] classLoader) {

        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                URL url = cl.getResource(resource);
                if (null == url) {
                    url = cl.getResource("/" + resource);
                }
                if (null != url) {
                    return url;
                }
            }
        }
        return null;
    }

    /**
     * 按ClassLoader加载类
     * 
     * @param name
     * @param classLoader
     * @return
     * @throws ClassNotFoundException
     */
    private Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {

        for (ClassLoader cl : classLoader) {
            if (null != cl) {
                try {
                    Class<?> clazz = Class.forName(name, true, cl);
                    if (null != clazz) {
                        return clazz;
                    }
                } catch (Exception e) {
                }
            }
        }

        throw new ClassNotFoundException("Cannot find class: " + name);
    }

    /**
     * 顺序获取classLoader
     * 
     * @param classLoader
     * @return
     */
    private ClassLoader[] getClassLoaders(ClassLoader classLoader) {
        return new ClassLoader[] { classLoader, Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader(), systemClassLoader };
    }
}
