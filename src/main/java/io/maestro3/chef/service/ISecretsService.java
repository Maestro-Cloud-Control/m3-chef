package io.maestro3.chef.service;

public interface ISecretsService {

    String getSecretValue(String secretKey);

    void saveSecret(String secretKey, String secretValue);

    void deleteSecret(String secretKey);

    boolean exists(String secretKey);
}
