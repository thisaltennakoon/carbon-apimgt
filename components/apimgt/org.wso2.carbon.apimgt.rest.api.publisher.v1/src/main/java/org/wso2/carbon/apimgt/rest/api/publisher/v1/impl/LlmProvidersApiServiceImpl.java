/*
 * Copyright (c) 2024 WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.rest.api.publisher.v1.impl;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIAdmin;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.LLMProviderConfiguration;
import org.wso2.carbon.apimgt.api.model.LLMProvider;
import org.wso2.carbon.apimgt.impl.APIAdminImpl;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.*;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.*;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.LLMProviderSummaryResponseListDTO;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;

import java.util.List;
import javax.ws.rs.core.Response;

public class LlmProvidersApiServiceImpl implements LlmProvidersApiService {

    private static final Log log = LogFactory.getLog(LlmProvidersApiServiceImpl.class);

    @Override
    public Response getLLMProvider(String llmProviderId, MessageContext messageContext)
            throws APIManagementException {

        APIAdmin apiAdmin = new APIAdminImpl();
        String organization = RestApiUtil.getValidatedOrganization(messageContext);
        try {
            LLMProviderResponseDTO result =
                    LLMProviderMappingUtil.fromProviderToProviderResponseDTO(apiAdmin.getLLMProvider(organization,
                            llmProviderId));
            return Response.ok().entity(result).build();
        } catch (APIManagementException e) {
            log.warn("Error while retrieving LLM Provider");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public Response getLLMProviderApiDefinition(String name, String apiVersion, MessageContext messageContext)
            throws APIManagementException {

        APIAdmin apiAdmin = new APIAdminImpl();
        String organization = RestApiUtil.getValidatedOrganization(messageContext);
        try {
            List<LLMProvider> providerList = apiAdmin
                    .getLLMProviders(organization, name, apiVersion, null);
            String apiDefinition = providerList.get(0).getApiDefinition();
            return Response.ok().entity(apiDefinition).build();
        } catch (APIManagementException e) {
            log.warn("Error while trying to retrieve LLM Provider's API definition");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public Response getLLMProviderEndpointConfiguration(String name, String apiVersion, MessageContext messageContext)
            throws APIManagementException {

        APIAdmin apiAdmin = new APIAdminImpl();
        String organization = RestApiUtil.getValidatedOrganization(messageContext);
        try {
            List<LLMProvider> providerList = apiAdmin
                    .getLLMProviders(organization, name, apiVersion, null);
            LLMProviderConfiguration providerConfiguration = new Gson()
                    .fromJson(providerList.get(0).getConfigurations(), LLMProviderConfiguration.class);
            LLMProviderEndpointConfigurationDTO endpointConfigurationDTO = new LLMProviderEndpointConfigurationDTO();
            if (providerConfiguration.getAuthHeader() != null) {
                endpointConfigurationDTO.setAuthHeader(providerConfiguration.getAuthHeader());
            }
            if (providerConfiguration.getAuthQueryParameter() != null) {
                endpointConfigurationDTO.setAuthQueryParameter(providerConfiguration.getAuthQueryParameter());
            }
            return Response.ok().entity(endpointConfigurationDTO).build();
        } catch (APIManagementException e) {
            log.warn("Error while trying to retrieve LLM Provider's API definition");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Response getLLMProviders(MessageContext messageContext)
            throws APIManagementException {

        APIAdmin apiAdmin = new APIAdminImpl();
        String organization = RestApiUtil.getValidatedOrganization(messageContext);
        try {
            List<LLMProvider> LLMProviderList = apiAdmin
                    .getLLMProviders(organization, null, null, null);
            LLMProviderSummaryResponseListDTO providerListDTO =
                    LLMProviderMappingUtil.fromProviderSummaryListToProviderSummaryListDTO(LLMProviderList);
            return Response.ok().entity(providerListDTO).build();
        } catch (APIManagementException e) {
            log.warn("Error while trying to retrieve LLM Providers");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
