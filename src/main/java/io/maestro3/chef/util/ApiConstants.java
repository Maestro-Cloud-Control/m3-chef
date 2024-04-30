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


/**
 * <p><b>Author:</b> Dmytro_Kurkin </p>
 * <p><b>Date:</b> 26.12.12 </p>
 */
public final class ApiConstants {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
    public static final String UTC_TIME_ZONE = "UTC";
    public static final String USERDATA_URL = "/autoconfiguration/instance";
    public static final String PUBLIC_USERDATA_URL = "/autoconfiguration/instance/public";
    public static final String AUTOCONFIGURATION_CHEF_URL = "/autoconfiguration/chef";
    public static final String AUTOCONFIGURATION_DOCKER_URL = "/autoconfiguration/docker";
    public static final String AUTOCONFIGURATION_RDB_URL = "/autoconfiguration/rdb";
    public static final String AUTOCONFIGURATION_KEY_URL = "/autoconfiguration/key";
    public static final String AUTOCONFIGURATION_CLI_URL = "/autoconfiguration/cli";
    public static final String OPEN_STACK_NOTIFICATION = "/openstack/notification";
    public static final String AWS_SSM_URL = "/aws/ssm";
    public static final String SSM_ACTIVATION = "/activation";
    public static final String STATE = "/state";
    public static final String STORAGE = "/storage/**";
    public static final String RUNNING = "/running";
    public static final String SSH = "/ssh";
    public static final String CONFIG = "/config";
    public static final String METADATA = "/metadata";
    public static final String SECRET = "/secret";

    public static final String RPC_URL = "/rpc";
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String XML_CONTENT_TYPE = "application/xml";

    private ApiConstants() {
        throw new UnsupportedOperationException("Instantiation is forbidden.");
    }
}
