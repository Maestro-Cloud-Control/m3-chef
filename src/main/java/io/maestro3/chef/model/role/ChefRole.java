package io.maestro3.chef.model.role;


public enum ChefRole {

    BASE(
            "base",
            "common"
    );

    private String name;
    private String dataBagItemName;

    ChefRole(String name, String dataBagItemName) {
        this.name = name;
        this.dataBagItemName = dataBagItemName;
    }

    public static ChefRole fromName(String name) {
        for (ChefRole value : values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDataBagItemName() {
        return dataBagItemName;
    }
}
