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

package io.maestro3.chef.client.command.node;

import io.maestro3.chef.client.command.base.BasicChefCommand;
import io.maestro3.chef.client.entity.NodeEntity;
import io.maestro3.chef.client.http.client.RequestMethod;
import io.maestro3.sdk.internal.util.Assert;

/**
 * Creates new node. Returns node uri as a result.
 * See REST description for this command at <a href="http://docs.opscode.com/api_chef_server.html#id26">Chef Server API</a>
 *
 */
public class CreateNodeCommand extends BasicChefCommand<CreateNodeCommand.Result> {
    private static final String COMMAND_RESOURCE_NAME = "nodes";

    private NodeEntity nodeToCreate;

    public CreateNodeCommand() {
        super(RequestMethod.POST, COMMAND_RESOURCE_NAME);
    }

    public NodeEntity getNodeToCreate() {
        return nodeToCreate;
    }

    public void setNodeToCreate(NodeEntity nodeToCreate) {
        this.nodeToCreate = nodeToCreate;
    }

    @Override
    public Object getRequestEntity() {
        Assert.notNull(nodeToCreate, "You must set nodeToCreate before executing this command");

        return nodeToCreate;
    }

    public static class Result {
        private String uri;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        @Override
        public String toString() {
            return "CreateNodeCommandResult{uri=" + uri + '}';
        }
    }
}
