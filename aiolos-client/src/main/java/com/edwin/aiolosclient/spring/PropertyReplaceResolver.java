package com.edwin.aiolosclient.spring;

import java.util.Properties;
import java.util.Set;

import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import com.edwin.aiolosclient.Constants;
import com.edwin.aiolosclient.curator.CuratorWrapper;
import com.google.common.collect.Sets;

/**
 * 这个类是copy过来的（需要根据特定的应用场景来实现的）
 * 
 * @author jinming.wu
 * @date 2015-5-24
 */
public class PropertyReplaceResolver implements StringValueResolver {

    private CuratorWrapper curatorWrapper;

    private Set<String>    visitedPlaceholders;

    private Properties     localProps;

    public PropertyReplaceResolver(CuratorWrapper curatorWrapper, Properties localProps) {
        this.curatorWrapper = curatorWrapper;
        this.visitedPlaceholders = Sets.newHashSet();
        this.localProps = localProps;
    }

    @Override
    public String resolveStringValue(String strVal) {
        String value = parseStringValue(strVal, visitedPlaceholders);
        return (value.equals("") ? null : value);
    }

    private String parseStringValue(String strVal, Set<String> visitedPlaceholders) {
        StringBuffer buf = new StringBuffer(strVal);
        int startIndex = strVal.indexOf(Constants.DEFAULT_PLACEHOLDER_PREFIX);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(buf, startIndex);
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + Constants.DEFAULT_PLACEHOLDER_PREFIX.length(), endIndex);
                placeholder = parseStringValue(placeholder, visitedPlaceholders);
                String propVal = null;
                String placeholder_ = null;
                if (this.localProps != null) {
                    placeholder_ = this.localProps.getProperty(placeholder);
                }
                if (placeholder_ != null) {
                    if (placeholder_.startsWith(Constants.DEFAULT_PLACEHOLDER_PREFIX)
                        && placeholder_.endsWith(Constants.DEFAULT_PLACEHOLDER_SUFFIX)) {
                        placeholder_ = placeholder_.substring(2);
                        placeholder_ = placeholder_.substring(0, placeholder_.length() - 1);
                        propVal = curatorWrapper.getProperty(placeholder_);
                        placeholder = placeholder_;
                    } else {
                        propVal = curatorWrapper.getProperty(placeholder);
                    }
                } else {
                    propVal = curatorWrapper.getProperty(placeholder);
                }
                if (propVal != null) {
                    propVal = parseStringValue(propVal, visitedPlaceholders);
                    buf.replace(startIndex, endIndex + Constants.DEFAULT_PLACEHOLDER_SUFFIX.length(), propVal);
                    startIndex = buf.indexOf(Constants.DEFAULT_PLACEHOLDER_PREFIX, startIndex + propVal.length());
                }
                visitedPlaceholders.remove(placeholder);
            } else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }

    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + Constants.DEFAULT_PLACEHOLDER_PREFIX.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (StringUtils.substringMatch(buf, index, Constants.DEFAULT_PLACEHOLDER_SUFFIX)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + Constants.DEFAULT_PLACEHOLDER_SUFFIX.length();
                } else {
                    return index;
                }
            } else if (StringUtils.substringMatch(buf, index, Constants.DEFAULT_PLACEHOLDER_PREFIX)) {
                withinNestedPlaceholder++;
                index = index + Constants.DEFAULT_PLACEHOLDER_PREFIX.length();
            } else {
                index++;
            }
        }
        return -1;
    }
}
