package io.maestro3.chef.model;


import java.util.HashMap;
import java.util.Map;


public enum ChefRole {

    BASE(
            "base",
            "common"
    );

    private static final Map<String, ChefRole> ROLES_MAP = new HashMap<>();

    static {
        ChefRole[] roles = ChefRole.values();
        for (ChefRole role : roles) {
            ROLES_MAP.put(role.getName(), role);
        }
    }

    private String name;
    private String dataBagItemName;

    ChefRole(String name, String dataBagItemName) {
        this.name = name;
        this.dataBagItemName = dataBagItemName;
    }

    public static ChefRole fromName(String name) {
        return ROLES_MAP.get(name);
    }

    public String getName() {
        return name;
    }

    public String getDataBagItemName() {
        return dataBagItemName;
    }
}
