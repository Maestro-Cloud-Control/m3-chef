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

package io.maestro3.chef.client.command.client;

import io.maestro3.chef.client.utils.ChefUtils;

/**
 * Represents data needed to create chef API client. <br/>
 * Client name is used everywhere. You will get and RSA key stuff to
 * use for requests to server so it will authenticate you without password (login-password is usually the case for most
 * of servers).<br/>
 * Clients with admin rights can delete stuff and create new clients. Funny enough, client without admin rights can
 * CREATE nodes, but cannot DELETE them. You can read more about permissions and groups on chef server
 * <a href="http://docs.opscode.com/auth.html">here</a>, there is just too much stuff to mention in javadoc.
 *
 */
public class CreateClientCommandInput {
    private String name;
    private Boolean admin;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return ChefUtils.buildString("CreateClientCommandInput{name=", name,
                ", admin=", admin,
                '}');
    }
}
