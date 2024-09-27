/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.api.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents a LLM (Large Language Model) Provider.
 */
public class LLMProvider implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id = null;
    private String name = null;
    private String description = null;
    private String apiVersion = null;
    private String organization = null;
    private String apiDefinition = null;
    private String configurations = null;
    private boolean builtInSupport = false;

    public LLMProvider(String name, String apiVersion, String organization) {

        this.name = name;
        this.apiVersion = apiVersion;
        this.organization = organization;
    }

    public LLMProvider() {}

    public boolean isBuiltInSupport() {

        return builtInSupport;
    }

    public void setBuiltInSupport(boolean builtInSupport) {

        this.builtInSupport = builtInSupport;
    }

    public String getConfigurations() {

        return this.configurations;
    }

    public void setConfigurations(String configurations) {

        this.configurations = configurations;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApiDefinition() {
        return apiDefinition;
    }

    public void setApiDefinition(String apiDefinition) {
        this.apiDefinition = apiDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LLMProvider that = (LLMProvider) o;
        return Objects.equals(name, that.name) && Objects.equals(apiVersion, that.apiVersion) &&
                Objects.equals(organization, that.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, apiVersion, organization);
    }
}
