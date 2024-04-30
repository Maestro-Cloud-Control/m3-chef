/*
 * Copyright 2023 Maestro Cloud Control LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.maestro3.chef.util;

import io.maestro3.chef.model.ChefInstance;
import io.maestro3.chef.model.InstanceProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

public final class PropertyUtils {

    private static final String APPEND_SEPARATOR = ",";
    private static final String INNER_PROPERTY_KEY_VALUE_SEPARATOR = "=";

    private PropertyUtils() {
        throw new UnsupportedOperationException("Instantiation is forbidden.");
    }

    public static InstanceProperty getProperty(ChefInstance instance, String instancePropertyKey) {
        return instance.getInstanceProperties().stream()
                .filter(property -> instancePropertyKey.equalsIgnoreCase(property.getKey()))
                .findFirst()
                .orElse(null);
    }

    public static String getInnerValue(InstanceProperty property, String innerKey) {
        if (property == null || StringUtils.isBlank(innerKey) || StringUtils.isBlank(property.getValue())) {
            return null;
        }

        String[] keyValues = property.getValue().split(",");
        for (String keyValue : keyValues) {
            int separatorIndex = keyValue.indexOf(INNER_PROPERTY_KEY_VALUE_SEPARATOR);
            if (separatorIndex < 0) {
                continue;
            }

            String key = keyValue.substring(0, separatorIndex);
            if (key.equals(innerKey)) {
                return keyValue.substring(separatorIndex + 1);
            }
        }

        return null;
    }

    /**
     * Sets inner property with given key to given value. Inner property means that given property has such value, as
     * {@code "key1=value1,key2=value2,key3=value3"}: here given property has three inner keys and values. This method
     * will set existing key to given value or append a new {@code "key=value"} pair to property value.<br/><br/>
     * NOTICE: method does not create new property instance, but changes given one.
     *
     * @param property   the property to change value of
     * @param innerKey   the inner key to look for
     * @param innerValue the value to set the key to
     */
    public static void setInnerValue(InstanceProperty property, String innerKey, String innerValue) {
        if (property == null || StringUtils.isBlank(innerKey)) {
            return;
        }
        if (StringUtils.isBlank(property.getValue())) {
            property.setValue(innerKey + INNER_PROPERTY_KEY_VALUE_SEPARATOR + innerValue);
            return;
        }

        List<String> keyValues = Arrays.asList(property.getValue().split(","));
        StringBuilder builder = new StringBuilder();
        String prefix = "";
        boolean givenKeyExists = false;
        for (String keyValue : keyValues) {
            int separatorIndex = keyValue.indexOf(INNER_PROPERTY_KEY_VALUE_SEPARATOR);
            if (separatorIndex < 0) {
                continue;
            }

            builder.append(prefix);
            String key = keyValue.substring(0, separatorIndex);
            if (key.equals(innerKey)) {
                builder.append(innerKey).append(INNER_PROPERTY_KEY_VALUE_SEPARATOR).append(innerValue);
                givenKeyExists = true;
            } else {
                builder.append(keyValue);
            }

            prefix = APPEND_SEPARATOR;
        }

        if (!givenKeyExists) {
            builder.append(prefix);
            builder.append(innerKey).append(INNER_PROPERTY_KEY_VALUE_SEPARATOR).append(innerValue);
        }

        property.setValue(builder.toString());
    }

    /**
     * Merges two lists of properties overriding or appending old ones with new ones.
     *
     * @param oldProperties the list of existing properties
     * @param newProperties the list of properties to add, override or append
     * @param append        {@code true} if you want to preserve old values of properties that are contained both in old
     *                      and new properties list, {@code false} if you want to override old properties with new ones
     * @return the list of merged properties
     */
    public static List<InstanceProperty> mergeResourceProperties(List<InstanceProperty> oldProperties, List<InstanceProperty> newProperties, boolean append) {
        if (CollectionUtils.isEmpty(newProperties)) {
            if (CollectionUtils.isEmpty(oldProperties)) {
                return newLinkedList();
            }
            return newLinkedList(oldProperties);
        }
        if (CollectionUtils.isEmpty(oldProperties)) {
            return newLinkedList(newProperties);
        }
        List<InstanceProperty> mergedProperties = newLinkedList(oldProperties);
        newPropsLoop:
        for (InstanceProperty newProperty : newProperties) {
            for (InstanceProperty oldProperty : mergedProperties) {
                if (oldProperty.getKey().equals(newProperty.getKey())) {
                    if (append) {
                        String appendSeparator = StringUtils.EMPTY;
                        if (StringUtils.isNotEmpty(oldProperty.getValue()) && StringUtils.isNotEmpty(newProperty.getValue())) {
                            appendSeparator = APPEND_SEPARATOR;
                        }
                        oldProperty.setValue(oldProperty.getValue() + appendSeparator + newProperty.getValue());
                    } else {
                        mergedProperties.remove(oldProperty);
                        mergedProperties.add(newProperty);
                    }
                    continue newPropsLoop;
                }
            }
            mergedProperties.add(newProperty);
        }
        return mergedProperties;
    }
}
