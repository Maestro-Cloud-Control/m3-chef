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

package io.maestro3.chef.service;

import io.maestro3.chef.exception.ChefServerUnexpectedErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


/**
 * <p>Title: ChefManagementService </p>
 * <p>Description: </p>
 * <p>Author: Andrii_Gusiev </p>
 * Date: 17.12.13
 */
@Service
public class ChefConfigurationManager implements IChefConfigurationManager {

    private static final Logger LOG = LoggerFactory.getLogger(ChefConfigurationManager.class);

    private IChefService chefService;

    @Autowired
    public ChefConfigurationManager(IChefService chefService) {
        this.chefService = chefService;
    }

    @Override
    public Set<String> describeChefRoles(String zoneName) {
        Set<String> rolesSet = new HashSet<>();

        try {
            Set<String> defaultChefRoles = chefService.describeDefaultChefRoles(zoneName);
            rolesSet.addAll(defaultChefRoles);
        } catch (ChefServerUnexpectedErrorException e) {
            LOG.warn(e.getMessage(), e);
        }

        return rolesSet;
    }

    @Override
    public Set<String> describeChefRolesForServer(String serverId) {
        Set<String> rolesSet = new HashSet<>();

        try {
            Set<String> defaultChefRoles = chefService.describeChefRolesForServer(serverId);
            rolesSet.addAll(defaultChefRoles);
        } catch (ChefServerUnexpectedErrorException e) {
            LOG.warn(e.getMessage(), e);
        }

        return rolesSet;
    }

}
