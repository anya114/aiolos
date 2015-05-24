package com.edwin.aiolosclient.helper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author jinming.wu
 * @date 2015-5-23
 */
public class ClassHelper {

    public static List<Class<?>> getAllClassByInterface(Class<?> clazz) throws Exception {
        List<Class<?>> returnClassList = Lists.newArrayList();
        if (clazz.isInterface()) {
            String packageName = clazz.getPackage().getName();
            List<Class<?>> allClass = getClasses(packageName);
            for (int i = 0; i < allClass.size(); i++) {
                if (clazz.isAssignableFrom(allClass.get(i))) {
                    if (!clazz.equals(allClass.get(i))) {
                        returnClassList.add(allClass.get(i));
                    }
                }
            }
        }
        return returnClassList;
    }

    private static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = Lists.newArrayList();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = Lists.newArrayList();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = Lists.newArrayList();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
