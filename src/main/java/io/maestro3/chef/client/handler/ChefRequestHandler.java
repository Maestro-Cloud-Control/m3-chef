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

package io.maestro3.chef.client.handler;

import io.maestro3.chef.client.command.IChefCommand;
import io.maestro3.chef.client.context.IChefContext;
import io.maestro3.chef.client.http.client.RequestMethod;
import io.maestro3.chef.client.http.client.exception.SimpleHttpClientException;
import io.maestro3.chef.client.http.client.handler.RequestHandler;
import io.maestro3.chef.client.utils.ChefUtils;
import io.maestro3.chef.model.ChefVersion;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ChefRequestHandler implements RequestHandler {
    public static final String CONTENT_TYPE = "application/json";

    private static final String SIGN_HEADER_VALUE = "version=1.0";
    private static final String API_VERSION_HEADER_VALUE = "1";
    private static final String ACCEPT_ENCODING_HEADER_VALUE = "gzip;q=1.0,deflate;q=0.6,identity;q=0.3";
    private static final String TIMESTAMP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String TIMESTAMP_ZONE = "UTC";

    private static final DateFormat TIMESTAMP_FORMATTER;

    private static final String URL_SEPARATOR = "/";

    private static final Map<String, String> CHEF_VERSION_MAPPING;
    static {
        TIMESTAMP_FORMATTER = new SimpleDateFormat(TIMESTAMP_DATE_FORMAT);
        TIMESTAMP_FORMATTER.setTimeZone(TimeZone.getTimeZone(TIMESTAMP_ZONE));

        CHEF_VERSION_MAPPING = new HashMap<>();
        CHEF_VERSION_MAPPING.put("11", "11.4.0");
        CHEF_VERSION_MAPPING.put("12", "12.7.2");
        CHEF_VERSION_MAPPING.put("13", "15.3.14");
    }

    private IChefContext context;
    private IChefCommand command;

    public ChefRequestHandler(IChefContext context, IChefCommand command) {
        this.context = context;
        this.command = command;
    }

    @Override
    public void process(HttpRequest request, HttpContext unused) throws SimpleHttpClientException {
        String username = context.getUsername();
        String resource = command.getResourceName();
        ChefVersion version = ChefVersion.forVersion(context.getChefVersion());
        if (ChefVersion.supportOrganization(version)) {
            resource = context.getOrganizationResourcePrefix() + resource;
        }

        String body = "";
        if (command.getMethod() == RequestMethod.POST || command.getMethod() == RequestMethod.PUT) {
            try {
                InputStream stream = ((HttpEntityEnclosingRequest) request).getEntity().getContent();
                StringWriter writer = new StringWriter();
                IOUtils.copy(stream, writer);
                body = writer.toString();
            } catch (IOException e) {
                throw new SimpleHttpClientException("Can't read request body", e);
            }
        }

        String hashedPath;
        String hashedBody;
        try {
            hashedPath = ChefUtils.getHash(URL_SEPARATOR + resource);
            hashedBody = ChefUtils.getHash(body);
        } catch (Exception e) {
            throw new SimpleHttpClientException(e);
        }

        String timestamp = getTimestamp();
        String authenticationString = buildAuthenticationString(hashedPath, hashedBody, timestamp, username);
        String signedAuthenticationString;
        try {
            signedAuthenticationString = ChefUtils.signAuthenticationString(authenticationString, context.getAuthenticationKey());
        } catch (Exception e) {
            throw new SimpleHttpClientException(e);
        }
        String[] authenticationHeaders = ChefUtils.splitHeaders(signedAuthenticationString);
        String chefVersion = CHEF_VERSION_MAPPING.get(context.getChefVersion());

        request.addHeader(ChefRequestHeader.HEADER_CONTENT_TYPE, CONTENT_TYPE);
        request.addHeader(ChefRequestHeader.HEADER_TIMESTAMP, timestamp);
        request.addHeader(ChefRequestHeader.HEADER_USER_ID, username);
        request.addHeader(ChefRequestHeader.HEADER_CHEF_VERSION, chefVersion);
        request.addHeader(ChefRequestHeader.HEADER_ACCEPT, CONTENT_TYPE);
        request.addHeader(ChefRequestHeader.HEADER_CONTENT_HASH, hashedBody);
        request.addHeader(ChefRequestHeader.HEADER_SIGN_VERSION, SIGN_HEADER_VALUE);
        request.addHeader(ChefRequestHeader.HEADER_SERVER_API_VERSION, API_VERSION_HEADER_VALUE);
        request.addHeader(ChefRequestHeader.HEADER_ACCEPT_ENCODING, ACCEPT_ENCODING_HEADER_VALUE);

        for (int index = 0; index < authenticationHeaders.length; index++) {
            request.addHeader(ChefRequestHeader.HEADER_AUTHORIZATION + (index + 1), authenticationHeaders[index]);
        }
    }

    private String buildAuthenticationString(String hashedPath, String hashedBody, String timestamp, String username) {
        String methodName = command.getMethod().name();

        StringBuilder builder = new StringBuilder();
        builder.append(ChefAuthorizationField.AUTHORIZATION_METHOD).append(methodName).append(ChefAuthorizationField.AUTHORIZATION_FIELD_SEPARATOR);
        builder.append(ChefAuthorizationField.AUTHORIZATION_PATH_HASH).append(hashedPath).append(ChefAuthorizationField.AUTHORIZATION_FIELD_SEPARATOR);
        builder.append(ChefAuthorizationField.AUTHORIZATION_CONTENT_HASH).append(hashedBody).append(ChefAuthorizationField.AUTHORIZATION_FIELD_SEPARATOR);
        builder.append(ChefAuthorizationField.AUTHORIZATION_TIMESTAMP).append(timestamp).append(ChefAuthorizationField.AUTHORIZATION_FIELD_SEPARATOR);
        builder.append(ChefAuthorizationField.AUTHORIZATION_USER_ID).append(username);
        return builder.toString();
    }

    private synchronized String getTimestamp() {
        return TIMESTAMP_FORMATTER.format(new Date());
    }

    public interface ChefRequestHeader {
        String HEADER_ACCEPT = "Accept";
        String HEADER_AUTHORIZATION = "X-Ops-Authorization-";
        String HEADER_CHEF_VERSION = "X-Chef-Version";
        String HEADER_CONTENT_HASH = "X-Ops-Content-Hash";
        String HEADER_CONTENT_TYPE = "Content-type";
        String HEADER_SIGN_VERSION = "X-Ops-Sign";
        String HEADER_TIMESTAMP = "X-Ops-Timestamp";
        String HEADER_USER_ID = "X-Ops-Userid";
        String HEADER_SERVER_API_VERSION = "X-Ops-Server-API-Version";
        String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    }

    public interface ChefAuthorizationField {
        String AUTHORIZATION_CONTENT_HASH = "X-Ops-Content-Hash:";
        String AUTHORIZATION_FIELD_SEPARATOR = "\n";
        String AUTHORIZATION_METHOD = "Method:";
        String AUTHORIZATION_PATH_HASH = "Hashed Path:";
        String AUTHORIZATION_TIMESTAMP = "X-Ops-Timestamp:";
        String AUTHORIZATION_USER_ID = "X-Ops-UserId:";
    }
}
