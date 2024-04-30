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

package io.maestro3.chef.client.serialization;

import io.maestro3.chef.client.command.IChefCommand;
import io.maestro3.chef.client.http.client.serialization.Serializer;
import io.maestro3.chef.client.http.client.serialization.impl.CommonGsonFactory;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.entity.StringEntity;

public class ChefRequestSerializer implements Serializer {

    @Override
    public void serialize(Object data, HttpEntityEnclosingRequest request) throws Exception {
        if (data == null) {
            return;
        }

        Object toSerialize;
        if (data instanceof IChefCommand) {
            toSerialize = ((IChefCommand<?>) data).getRequestEntity();
        } else {
            toSerialize = data;
        }
        request.setEntity(new StringEntity(CommonGsonFactory.create().toJson(toSerialize)));
    }
}
