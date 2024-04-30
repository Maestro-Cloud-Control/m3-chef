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

import io.maestro3.chef.client.IChefClient;
import io.maestro3.chef.client.command.IChefCommand;
import io.maestro3.chef.client.command.databag.CreateDataBagCommand;
import io.maestro3.chef.client.command.databag.CreateDataBagItemCommand;
import io.maestro3.chef.client.command.databag.DeleteDataBagCommand;
import io.maestro3.chef.client.command.databag.GetDataBagCommand;
import io.maestro3.chef.client.command.databag.GetDataBagItemCommand;
import io.maestro3.chef.client.command.databag.GetRolesFromChefCommand;
import io.maestro3.chef.client.command.databag.PushRolesToChefCommand;
import io.maestro3.chef.client.command.databag.UpdateDataBagItemCommand;
import io.maestro3.chef.client.command.node.CreateNodeCommand;
import io.maestro3.chef.client.context.IChefContext;
import io.maestro3.chef.client.context.IChefContextFactory;
import io.maestro3.chef.client.entity.NodeEntity;
import io.maestro3.chef.client.exception.ChefClientException;
import io.maestro3.chef.client.factory.IChefClientFactory;
import io.maestro3.chef.client.response.IChefResponse;
import io.maestro3.sdk.internal.util.CollectionUtils;
import io.maestro3.sdk.internal.util.JsonUtils;
import io.maestro3.sdk.internal.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChefDataBagService implements IChefDataBagService {
    private static final Logger LOG = LoggerFactory.getLogger(ChefDataBagService.class);
    public static final int GCM_AUTH_TAG_LENGTH = 128;
    private static final Pattern CHEF_ROLE_PATTERN = Pattern.compile("role\\[([^]]+)]");


    @Autowired
    private IChefContextFactory contextFactory;
    @Autowired
    private IChefClientFactory clientFactory;
    @Autowired
    private IChefSecretProvider secretProvider;

    @Value("${chef.data.bag.enabled:false}")
    private boolean chefDataBagEnabled;

    @Override
    public boolean isChefDataBagsEnabled() {
        return chefDataBagEnabled;
    }

    @Override
    public void createDataBag(IChefContext context, String name) {
        Assert.hasText(name, "name must not be null or empty");
        Assert.notNull(context, "context must not be null");

        IChefClient client = clientFactory.getInstance(context);

        IChefCommand<CreateDataBagCommand.Result> command = new CreateDataBagCommand(name);
        try {
            IChefResponse<CreateDataBagCommand.Result> response = client.execute(command);
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.", response.getCode(), response.getDescription()));
            }
        } catch (ChefClientException e) {
            LOG.warn("Could not create dataBag from chef server: Error message: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean dataBagExists(IChefContext context, String name) {
        Assert.hasText(name, "name must not be null or empty");
        Assert.notNull(context, "context must not be null");

        IChefClient client = clientFactory.getInstance(context);

        IChefCommand<Map> command = new GetDataBagCommand(name);
        try {
            IChefResponse<Map> response = client.execute(command);
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.", response.getCode(), response.getDescription()));
                return false;
            }
        } catch (ChefClientException e) {
            LOG.warn("Could not get dataBag from chef server: Error message: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void createItem(IChefContext context, String dataBag, String name, Map<String, String> data) {
        Assert.hasText(name, "name must not be null or empty");
        Assert.notNull(context, "context must not be null");

        IChefClient client = clientFactory.getInstance(context);

        try {
            Map<String, Object> item = encryptData(dataBag, data);
            IChefCommand<CreateDataBagItemCommand.Result> command = new CreateDataBagItemCommand(dataBag, name, item);
            IChefResponse<CreateDataBagItemCommand.Result> response = client.execute(command);
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.", response.getCode(), response.getDescription()));
            }
        } catch (ChefClientException e) {
            LOG.warn("Could not create dataBag from chef server: Error message: " + e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            LOG.error("Could not cipher databag values: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> encryptData(String databag, Map<String, String> data) throws GeneralSecurityException {
        String key = secretProvider.get(databag);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] sha256 = digest.digest(key.getBytes());

        SecretKey originalKey = new SecretKeySpec(sha256, 0, sha256.length, "AES");

        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (entry.getValue() != null) {
                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

                SecureRandom randomSecureRandom = new SecureRandom();
                byte[] iv = new byte[12];
                randomSecureRandom.nextBytes(iv);
                GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_AUTH_TAG_LENGTH, iv);

                cipher.init(Cipher.ENCRYPT_MODE, originalKey, gcmParameterSpec);

                Map<String, Object> value = new HashMap<>();
                Map<String, String> dataMap = MapUtils.<String, String>builder().withPair("json_wrapper", entry.getValue()).build();
                byte[] encryptedData = cipher.doFinal(JsonUtils.convertObjectToJson(dataMap).getBytes());
                byte[] authTag = Arrays.copyOfRange(encryptedData, encryptedData.length - (GCM_AUTH_TAG_LENGTH / Byte.SIZE), encryptedData.length);
                encryptedData = Arrays.copyOfRange(encryptedData, 0, encryptedData.length - (GCM_AUTH_TAG_LENGTH / Byte.SIZE));
                value.put("encrypted_data", Base64.getEncoder().encodeToString(encryptedData));
                value.put("version", 3);
                value.put("iv", Base64.getEncoder().encodeToString(iv));
                value.put("cipher", "aes-256-gcm");
                value.put("auth_tag", Base64.getEncoder().encodeToString(authTag));
                resultMap.put(entry.getKey(), value);
            }
        }
        return resultMap;
    }

    @Override
    public void updateOrCreateItem(IChefContext context, String dataBag, String name, Map<String, String> data) {
        Assert.hasText(name, "name must not be null or empty");
        Assert.notNull(context, "context must not be null");

        if (!dataBagItemExists(context, dataBag, name)) {
            createItem(context, dataBag, name, data);
            return;
        }

        IChefClient client = clientFactory.getInstance(context);

        try {
            if (CollectionUtils.isEmpty(data)){
                return;
            }
            Map<String, Object> item = encryptData(dataBag, data);
            IChefCommand<Map> command = new UpdateDataBagItemCommand(dataBag, name, item);
            //if success response equals empty map
            IChefResponse<Map> response = client.execute(command);
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.", response.getCode(), response.getDescription()));
            }
        } catch (ChefClientException e) {
            LOG.warn("Could not update dataBag from chef server: Error message: " + e.getMessage(), e);
        } catch (GeneralSecurityException e) {
            LOG.error("Could not cipher databag values: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean dataBagItemExists(IChefContext context, String dataBag, String name) {
        Assert.hasText(name, "name must not be null or empty");
        Assert.notNull(context, "context must not be null");

        IChefClient client = clientFactory.getInstance(context);

        IChefCommand<Map> command = new GetDataBagItemCommand(dataBag, name);
        try {
            IChefResponse<Map> response = client.execute(command);
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.", response.getCode(), response.getDescription()));
                return false;
            }
        } catch (ChefClientException e) {
            LOG.warn("Could not get dataBag item from chef server: Error message: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public void deleteDatabagAndCredentials(IChefContext context, String name) {
        Assert.hasText(name, "name must not be null or empty");
        Assert.notNull(context, "context must not be null");

        IChefClient client = clientFactory.getInstance(context);

        IChefCommand<Map> command = new DeleteDataBagCommand(name);
        try {
            IChefResponse<Map> response = client.execute(command);
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.", response.getCode(), response.getDescription()));
            }
            //removes key which was used for databag items encryption
            secretProvider.delete(name);
        } catch (ChefClientException e) {
            LOG.warn("Could not delete dataBag from chef server: Error message: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteItem(IChefContext context, String dataBag, String name) {

    }

    @Override
    public void pushChefRole(IChefContext context, String instanceId, String[] chefRoles) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(instanceId, "name must not be null or empty");
        Assert.notEmpty(chefRoles, "chef roles must not be null or empty");

        IChefClient client = clientFactory.getInstance(context);

        try {
            IChefCommand<Map> command = new PushRolesToChefCommand(instanceId, chefRoles);
            //if success response equals empty map
            IChefResponse<Map> response = client.execute(command);
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.", response.getCode(), response.getDescription()));
            }
        } catch (ChefClientException e) {
            LOG.warn("Could not update dataBag from chef server: Error message: " + e.getMessage(), e);
        }
    }

    @Override
    public void createNode(IChefContext context, String instanceId) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(instanceId, "name must not be null or empty");

        IChefClient client = clientFactory.getInstance(context);

        try {
            CreateNodeCommand createNodeCommand = new CreateNodeCommand();
            NodeEntity nodeToCreate = new NodeEntity();
            nodeToCreate.setName(instanceId);
            createNodeCommand.setNodeToCreate(nodeToCreate);
            IChefResponse<CreateNodeCommand.Result> response = client.execute(createNodeCommand);
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.", response.getCode(), response.getDescription()));
            }
        } catch (ChefClientException e) {
            LOG.warn("Could not update dataBag from chef server: Error message: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getChefRoles(IChefContext context, String instanceId) {
        Assert.notNull(context, "context must not be null");
        Assert.hasText(instanceId, "instanceId must not be null or empty");

        IChefClient client = clientFactory.getInstance(context);
        List<String> chefRoles = new ArrayList<>();
        try {
            IChefCommand<Map> command = new GetRolesFromChefCommand(instanceId);
            IChefResponse<Map> response = client.execute(command);
            Map result = response.getResult();
            Object runListObject = result.get("run_list");
            if (runListObject != null) {
                Iterable runList = (Iterable) runListObject;
                for (Object listItemObject : runList) {
                    String listItem = (String) listItemObject;
                    Matcher matcher = CHEF_ROLE_PATTERN.matcher(listItem);
                    if (matcher.find()) {
                        chefRoles.add(matcher.group(1));
                    }
                }
            }
            if (response.getErrorOccurred()) {
                LOG.info(String.format("Chef error status code: %s, Response Description: %s.",
                        response.getCode(), response.getDescription()));
            }
        } catch (ChefClientException e) {
            LOG.warn("Could not get dataBag from chef server: Error message: " + e.getMessage(), e);
        }
        return chefRoles;
    }
}
