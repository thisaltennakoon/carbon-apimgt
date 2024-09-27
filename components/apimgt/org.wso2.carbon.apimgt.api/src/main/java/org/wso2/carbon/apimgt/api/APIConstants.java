/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.apimgt.api;

import java.io.File;

/**
 * This class contains common constants for APIs.
 */
public class APIConstants {
    public static final String GATEWAY_ENV_TYPE_HYBRID = "hybrid";
    public static final String GATEWAY_ENV_TYPE_PRODUCTION = "production";
    public static final String GATEWAY_ENV_TYPE_SANDBOX = "sandbox";

    public static final String HTTPS_PROTOCOL_URL_PREFIX = "https://";
    public static final String HTTP_PROTOCOL_URL_PREFIX = "http://";

    public static final String WS_PROTOCOL_URL_PREFIX = "ws://";
    public static final String WSS_PROTOCOL_URL_PREFIX = "wss://";

    public static final String EMAIL_DOMAIN_SEPARATOR = "@";
    public static final String EMAIL_DOMAIN_SEPARATOR_REPLACEMENT = "-AT-";
    public static final String DEFAULT_KEY_MANAGER_HOST = "https://localhost:9443";

    public static final String ENDPOINT_SECURITY_TYPE = "type";
    public static final String ENDPOINT_SECURITY_TYPE_BASIC = "BASIC";
    public static final String ENDPOINT_SECURITY_TYPE_DIGEST = "DIGEST";
    public static final String ENDPOINT_SECURITY_TYPE_OAUTH = "oauth";
    public static final String ENDPOINT_SECURITY = "endpoint_security";
    public static final String ENDPOINT_SECURITY_PRODUCTION = "production";
    public static final String ENDPOINT_SECURITY_SANDBOX = "sandbox";
    public static final String ENDPOINT_CONFIG_SESSION_TIMEOUT = "sessionTimeOut";

    public static class AIAPIConstants {
        public static final String AI_API_REQUEST_METADATA = "AI_API_REQUEST_METADATA";
        public static final String AI_API_RESPONSE_METADATA = "AI_API_RESPONSE_METADATA";
        public static final String INPUT_SOURCE_PAYLOAD = "payload";
        public static final String CONNECTOR_TYPE = "connectorType";
        public static final String LLM_PROVIDER_ID = "id";
        public static final String LLM_PROVIDER_NAME = "name";
        public static final String LLM_PROVIDER_IS_EMPTY_PAYLOAD = "IS_EMPTY_PAYLOAD";
        public static final String LLM_PROVIDER_API_VERSION = "apiVersion";
        public static final String LLM_PROVIDER_CONFIGURATIONS = "configurations";
        public static final String LLM_CONFIGS_ENDPOINT = "/llm-providers";
        public static final String CONFIGURATIONS = "configurations";
        public static final String AI_API_DEFINITION_FILE_PATH = File.separator + "repository" + File.separator +
                "resources" + File.separator + "api_definitions" + File.separator;
        public static final String LLM_PROVIDER_SERVICE_AZURE_OPENAI_NAME = "AzureOpenAI";
        public static final String LLM_PROVIDER_SERVICE_AZURE_OPENAI_VERSION = "1.0.0";
        public static final String LLM_PROVIDER_SERVICE_AZURE_OPENAI_CONNECTOR = "azureOpenAi_1.0.0";
        public static final String LLM_PROVIDER_SERVICE_AZURE_OPENAI_KEY = "api-key";
        public static final String LLM_PROVIDER_SERVICE_AZURE_OPENAI_API_DEFINITION_FILE_NAME = "azure_api.yaml";
        public static final String LLM_PROVIDER_SERVICE_AZURE_OPENAI_DESCRIPTION = "Azure OpenAI service";
        public static final String LLM_PROVIDER_SERVICE_OPENAI_NAME = "OpenAI";
        public static final String LLM_PROVIDER_SERVICE_OPENAI_VERSION = "1.0.0";
        public static final String LLM_PROVIDER_SERVICE_OPENAI_CONNECTOR = "openAi_1.0.0";
        public static final String LLM_PROVIDER_SERVICE_OPENAI_KEY = "Authorization";
        public static final String LLM_PROVIDER_SERVICE_OPENAI_API_DEFINITION_FILE_NAME = "openai_api.yaml";
        public static final String LLM_PROVIDER_SERVICE_OPENAI_DESCRIPTION = "New OpenAI service";
        public static final String LLM_PROVIDER_SERVICE_MISTRALAI_NAME = "MistralAI";
        public static final String LLM_PROVIDER_SERVICE_MISTRALAI_VERSION = "1.0.0";
        public static final String LLM_PROVIDER_SERVICE_MISTRALAI_CONNECTOR = "mistralAi_1.0.0";
        public static final String LLM_PROVIDER_SERVICE_MISTRALAI_KEY = "Authorization";
        public static final String LLM_PROVIDER_SERVICE_MISTRALAI_API_DEFINITION_FILE_NAME = "mistral_api.yaml";
        public static final String LLM_PROVIDER_SERVICE_MISTRALAI_DESCRIPTION = "Mistral AI service";
        public static final String LLM_PROVIDER_SERVICE_METADATA_MODEL = "model";
        public static final String LLM_PROVIDER_SERVICE_METADATA_PROMPT_TOKEN_COUNT = "promptTokenCount";
        public static final String LLM_PROVIDER_SERVICE_METADATA_COMPLETION_TOKEN_COUNT = "completionTokenCount";
        public static final String LLM_PROVIDER_SERVICE_METADATA_TOTAL_TOKEN_COUNT = "totalTokenCount";
        public static final String LLM_PROVIDER_SERVICE_METADATA_IDENTIFIER_MODEL = "$.model";
        public static final String LLM_PROVIDER_SERVICE_METADATA_IDENTIFIER_PROMPT_TOKEN_COUNT = "$.usage" +
                ".prompt_tokens";
        public static final String LLM_PROVIDER_SERVICE_METADATA_IDENTIFIER_COMPLETION_TOKEN_COUNT = "$.usage" +
                ".completion_tokens";
        public static final String LLM_PROVIDER_SERVICE_METADATA_IDENTIFIER_TOTAL_TOKEN_COUNT = "$.usage.total_tokens";
        public static final String LLM_PROVIDER_SERVICE_DEFAULT = "default";
    }
}
