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


import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

public class RealHostResolver {
    private final HttpServletRequest request;

    public RealHostResolver(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * User this method to get real referer if proxy is installed.
     *
     * @return
     */
    public String getRealHost() {
        String header = request.getHeader("X-Forwarded-For");
        return header == null ? request.getRemoteHost() : StringUtils.split(header, ',')[0];
    }

    public String getRealIp() {
        String header = request.getHeader("X-Real-IP");
        return header == null ? request.getRemoteAddr() : header;
    }
}
