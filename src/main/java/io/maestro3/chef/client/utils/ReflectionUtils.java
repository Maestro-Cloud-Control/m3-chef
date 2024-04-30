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

package io.maestro3.chef.client.utils;

import io.maestro3.chef.client.conversion.IntegrationIdentifier;
import io.maestro3.chef.client.conversion.ObjectStringConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ReflectionUtils {

    private ReflectionUtils() {
        throw new UnsupportedOperationException("Instantiation is forbidden.");
    }

    /**
     * Determine Generic class parameter type
     *
     * @see <a href="http://habrahabr.ru/post/66593/">Generic class parameter type</a>
     */
    public static Type getGenericParameterClass(Class actualClass, int parameterIndex) {
        Type result;
        Type genericSuperclass = actualClass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            result = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[parameterIndex];
        } else {
            result = genericSuperclass;
        }
        return result;
    }

    public static List<Field> getAllFields(Object obj) {
        return getFieldsRecursively(obj.getClass());
    }

    public static List<Field> getFieldsRecursively(Class klass) {
        List<Field> fields = new ArrayList<>();
        if (klass == Object.class) {
            return fields;
        }
        fields.addAll(Arrays.asList(klass.getDeclaredFields()));
        if (klass.getSuperclass() != null) {
            fields.addAll(getFieldsRecursively(klass.getSuperclass()));
        }
        return fields;
    }

    public static String getIntegrationIdentifier(Field field) {
        IntegrationIdentifier integrationIdentifier = field.getAnnotation(IntegrationIdentifier.class);
        if (integrationIdentifier == null) {
            return null;
        }
        return integrationIdentifier.value();
    }

    public static ObjectStringConverter getConverter(Field field) throws IllegalAccessException, InstantiationException {
        IntegrationIdentifier integrationIdentifier = field.getAnnotation(IntegrationIdentifier.class);
        if (integrationIdentifier == null) {
            return null;
        }
        Class<? extends ObjectStringConverter> converterClass = integrationIdentifier.converter();
        ObjectStringConverter converter = converterClass.newInstance();
        return converter;
    }


    public static Map<String, Field> createMapOfAnnotatedFields(Iterable<Field> fields) {
        Map<String, Field> result = new HashMap<>();
        for (Field field : fields) {
            if (field.isAnnotationPresent(IntegrationIdentifier.class)) {
                String integrationIdentifier = ReflectionUtils.getIntegrationIdentifier(field);
                result.put(integrationIdentifier, field);
            }
        }
        return result;
    }

    public static void setFieldValue(Object target, String name, Object value) throws NoSuchFieldException {
        Field field = org.springframework.util.ReflectionUtils.findField(target.getClass(), name);
        if (field == null) {
            throw new NoSuchFieldException(name);
        }
        field.setAccessible(true);
        org.springframework.util.ReflectionUtils.setField(field, target, value);
    }

    public static Object getFieldValue(Field field, Object object) {
        if (field == null) {
            return null;
        }
        field.setAccessible(true);

        Object value = null;
        try {
            value = field.get(object);
        } catch (IllegalAccessException ignore) {
            // You will never get this exception.
        }
        return value;
    }
}
