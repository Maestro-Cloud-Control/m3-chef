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

import com.google.common.net.InetAddresses;
import io.maestro3.chef.exception.PermissionException;
import io.maestro3.chef.model.ChefInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public final class IpUtils {

    private IpUtils() {
        throw new UnsupportedOperationException("Instantiation is forbidden.");
    }

    public static void assertInstanceIp(ChefInstance instance) throws PermissionException {
        if (instance != null) {
            String ipAddress = instance.getPrivateIp();
            if (StringUtils.isNotBlank(instance.getPublicIp())) {
                ipAddress = instance.getPublicIp();
            }
            // TODO: 7/22/2020 add check of true instance ip adress (not only public)
//            if (StringUtils.isBlank(ipAddress)) {
//                throw new PermissionException("Instance " + instance.getInstanceId() + " without IP yet");
//            }
        }
    }

    // TODO: 11/5/2020 support tenant in region ip whitelist
    public static void checkIpIsValid(String requestIp, String... instanceIps) throws PermissionException {
        Assert.notNull(instanceIps, "instance can't be null");
        if (StringUtils.isEmpty(requestIp)) {
            throw new PermissionException("Access denied. Request IP is empty");
        }
        boolean validIp = false;
        for (String instanceIp : instanceIps) {
            if (StringUtils.isNotBlank(instanceIp) && instanceIp.equals(requestIp)) {
                validIp = true;
                break;
            }
        }
        if (!validIp) {
            throw new PermissionException("Access denied. Request IP is not in a white list");
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    public static boolean isIP(String instanceParam) {
        return InetAddresses.isInetAddress(instanceParam);
    }
}
