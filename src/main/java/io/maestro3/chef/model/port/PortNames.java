package io.maestro3.chef.model.port;

import java.util.HashSet;
import java.util.Set;

/**
 * Class contains services' port names.
 * Please, ensure you add port name to PORT_NAMES_SET when creating new one.
 *
 */
public abstract class PortNames {

    public static final String DOCKER_HYPERVISOR_PORT = "EO_DOCKER_HYPERVISOR";
    public static final String DOCKER_SWARM_PORT = "EO_DOCKER_SWARM";
    public static final String DOCKER_WEB_UI_PORT = "EO_DOCKER_WEB_UI";
    public static final String DOCKER_REGISTRY_PORT = "EO_DOCKER_REGISTRY";

    public static final String JENKINS_WEB_UI = "EO_JENKINS_WEB_UI";
    public static final String MESSAGING_WEB_UI = "EO_MESSAGING_WEB_UI";

    public static final String SPLUNK_WEB_UI = "EO_SPLUNK_WEB_UI";
    public static final String SPLUNK_API = "EO_SPLUNK_API";
    public static final String SPLUNK_DEFAULT_HTTP_PORT = "EO_SPLUNK_HTTP_PORT";
    public static final String SPLUNK_PROXY_SERVER_PORT = "EO_SPLUNK_PROXY_SERVER_PORT";

    public static final String AEM_AUTHOR_WEB_UI = "EO_AEM_AUTHOR_WEB_UI";
    public static final String AEM_PUBLISH_WEB_UI = "EO_AEM_PUBLISH_WEB_UI";

    public static final String GRAYLOG_PORT = "EO_GRAYLOG_PORT";
    public static final String GRAYLOG_WEB_UI_PORT = "EO_GRAYLOG_WEB_UI_PORT";

    public static final String ZABBIX_SERVER_PORT = "EO_ZABBIX_SERVER_PORT";
    public static final String ZABBIX_CLIENT_PORT = "EO_ZABBIX_CLIENT_PORT";

    public static final String GNOCCHI_SERVER_PORT = "EO_GNOCCHI_SERVER_PORT";

    public static final String LOAD_BALANCER_PORT = "EO_LOAD_BALANCER_PORT";

    public static final String AMBARI_UI_PORT = "EO_AMBARI_UI_PORT";
    public static final String CHEF_SERVER_SECURE_PORT = "EO_CHEF_SERVER_SECURE_PORT";
    public static final String CHEF_SERVER_INSECURE_PORT = "EO_CHEF_SERVER_INSECURE_PORT";
    public static final String HYBRIS_PORT = "EO_HYBRIS_SERVICE_PORT";

    // should not be added to PORT_NAMES_SET to prevent processing it as a chef role port
    public static final String LOAD_BALANCER_CLIENT_PORT = "EO_LOAD_BALANCER_CLIENT_PORT";

    private static final Set<String> PORT_NAMES_SET = new HashSet<>();

    static {
        PORT_NAMES_SET.add(DOCKER_HYPERVISOR_PORT);
        PORT_NAMES_SET.add(DOCKER_SWARM_PORT);
        PORT_NAMES_SET.add(DOCKER_WEB_UI_PORT);
        PORT_NAMES_SET.add(DOCKER_REGISTRY_PORT);
        PORT_NAMES_SET.add(GRAYLOG_PORT);
        PORT_NAMES_SET.add(GRAYLOG_WEB_UI_PORT);
        PORT_NAMES_SET.add(ZABBIX_SERVER_PORT);
        PORT_NAMES_SET.add(ZABBIX_CLIENT_PORT);
        PORT_NAMES_SET.add(GNOCCHI_SERVER_PORT);
        PORT_NAMES_SET.add(LOAD_BALANCER_PORT);
        PORT_NAMES_SET.add(AMBARI_UI_PORT);
        PORT_NAMES_SET.add(CHEF_SERVER_SECURE_PORT);
        PORT_NAMES_SET.add(CHEF_SERVER_INSECURE_PORT);
        PORT_NAMES_SET.add(JENKINS_WEB_UI);
        PORT_NAMES_SET.add(MESSAGING_WEB_UI);
        PORT_NAMES_SET.add(AEM_AUTHOR_WEB_UI);
        PORT_NAMES_SET.add(AEM_PUBLISH_WEB_UI);
        PORT_NAMES_SET.add(HYBRIS_PORT);
        PORT_NAMES_SET.add(SPLUNK_WEB_UI);
        PORT_NAMES_SET.add(SPLUNK_API);
        PORT_NAMES_SET.add(SPLUNK_DEFAULT_HTTP_PORT);
        PORT_NAMES_SET.add(SPLUNK_PROXY_SERVER_PORT);
    }

    public static boolean contains(String portName) {
        return PORT_NAMES_SET.contains(portName);
    }
}
