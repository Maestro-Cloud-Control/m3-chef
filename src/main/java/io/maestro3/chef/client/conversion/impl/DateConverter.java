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

package io.maestro3.chef.client.conversion.impl;

import io.maestro3.chef.client.conversion.ObjectStringConverter;

import java.util.Date;

public class DateConverter implements ObjectStringConverter<Date> {

    @Override
    public String toString(Date date) {
        if (date == null) {
            return "null";
        }
        long millis = date.getTime();
        return String.valueOf(millis);
    }

    @Override
    public Date fromString(String value) {
        if (value == null || value.equals("null")) {
            return null;
        }
        return new Date(Long.parseLong(value));

    }
}
