/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
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
package org.wso2.carbon.apimgt.gateway.mediators;

import software.amazon.awssdk.services.sts.model.Credentials;

import java.util.HashMap;
import java.util.Map;

/**
 * Credentials Cache Singleton Implementation to store AWS Credentials temporarily
 */
public class CredentialsCache {
    private static final CredentialsCache instance = new CredentialsCache();
    private final Map<String, Credentials> credentialsMap = new HashMap<>();

    /**
     * Private constructor
     */
    private CredentialsCache() {

    }

    public static CredentialsCache getInstance() {
        return instance;
    }

    public Map<String, Credentials> getCredentialsMap() {
        return credentialsMap;
    }
}
