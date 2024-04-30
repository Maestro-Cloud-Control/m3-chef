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

import com.fasterxml.jackson.core.type.TypeReference;
import io.maestro3.sdk.internal.util.JsonUtils;
import org.apache.commons.codec.binary.Base64;

public class ChefUtils {

    private static final String SEP = "::";

    public static String toResourceId(String tenant, String region, String id) {
        return tenant + SEP + region + SEP + id;
    }

    public static String encodeJson(Object data) {
        String jsonData = JsonUtils.convertObjectToJson(data);
        return new String(Base64.encodeBase64(jsonData.getBytes()));
    }

    public static <T> T decodeJson(String data, TypeReference<T> reference) {
        String jsonData = new String(Base64.decodeBase64(data));
        return JsonUtils.parseJson(jsonData, reference);
    }
}
