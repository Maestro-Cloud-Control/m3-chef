/*
 *
 *  Copyright 2018 EPAM Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package io.maestro3.chef.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OsType {

    LINUX("l", "Linux", 30, "linux_init.sh"),
    WINDOWS("w", "Windows", 127, "windows_init.ps1"),
    MACOS("m", "MacOs", 30, "macos-init.sh"),
    OTHER("o", "Other", 30, "init");

    private final String shortLabel;
    private final String label;
    private final int defaultDiskSpaceGB;
    private final String initScriptDefaultName;

    OsType(String shortLabel, String label, int defaultDiskSpaceGB, String initScriptDefaultName) {
        this.shortLabel = shortLabel;
        this.label = label;
        this.defaultDiskSpaceGB = defaultDiskSpaceGB;
        this.initScriptDefaultName = initScriptDefaultName;
    }

    public String getLabel() {
        return label;
    }

    public String getShortLabel() {
        return shortLabel;
    }

    public int getDefaultDiskSpaceGB() {
        return defaultDiskSpaceGB;
    }

    public String getInitScriptDefaultName() {
        return initScriptDefaultName;
    }

    @JsonCreator
    public static OsType fromValue(String label) {
        if (label != null) {
            for (OsType s : OsType.values()) {
                if (s.label.equalsIgnoreCase(label))
                    return s;
            }
        }
        return null;
    }

    public static OsType fromName(String name) {
        for (OsType osType : values()) {
            if (osType.name().equalsIgnoreCase(name)) {
                return osType;
            }
        }
        return null;
    }

    public static OsType fromShortLabel(String shortLabel) {
        if (shortLabel != null) {
            for (OsType s : OsType.values()) {
                if (s.shortLabel.equalsIgnoreCase(shortLabel))
                    return s;
            }
        }
        return null;
    }
}
