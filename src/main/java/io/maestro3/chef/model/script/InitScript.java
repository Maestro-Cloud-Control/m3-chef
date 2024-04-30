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

package io.maestro3.chef.model.script;

public class InitScript {

    private String content;
    private String userScriptId;
    private String userScriptName;

    public InitScript(String content) {
        this.content = content;
    }

    public void replaceParameter(String parameter, String value) {
        content = content.replace(parameter, value);
    }

    public void prependContent(String prefix) {
        content = prefix + content;
    }

    public void appendContent(String suffix) {
        content = content + suffix;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserScriptId() {
        return userScriptId;
    }

    public void setUserScriptId(String userScriptId) {
        this.userScriptId = userScriptId;
    }

    public String getUserScriptName() {
        return userScriptName;
    }

    public void setUserScriptName(String userScriptName) {
        this.userScriptName = userScriptName;
    }
}
