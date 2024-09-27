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

package org.wso2.carbon.apimgt.rest.api.admin.v1.impl;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIAdmin;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.LLMProvider;
import org.wso2.carbon.apimgt.impl.APIAdminImpl;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.rest.api.admin.v1.LlmProvidersApiService;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.LLMProviderResponseDTO;
import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.LLMProviderSummaryResponseListDTO;
import org.wso2.carbon.apimgt.rest.api.admin.v1.utils.mappings.LLMProviderMappingUtil;
import org.wso2.carbon.apimgt.rest.api.common.RestApiCommonUtil;
import org.wso2.carbon.apimgt.rest.api.common.RestApiConstants;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.InputStream;
import javax.ws.rs.core.Response;

public class LlmProvidersApiServiceImpl implements LlmProvidersApiService {

    private static final Log log = LogFactory.getLog(LlmProvidersApiServiceImpl.class);

    /**
     * Adds a new LLM Provider.
     *
     * @return Response containing the created LLM Provider or an error message.
     * @throws APIManagementException If an error occurs while adding the provider.
     */
    @Override
    public Response addLLMProvider(String id, String name, String apiVersion, String description,
                                   String configurations, InputStream apiDefinitionInputStream,
                                   Attachment apiDefinitionDetail, MessageContext messageContext) throws APIManagementException {

        APIAdmin apiAdmin = new APIAdminImpl();
        try {
            LLMProvider provider = new LLMProvider();
            provider.setName(name);
            provider.setApiVersion(apiVersion);
            provider.setOrganization(RestApiUtil.getValidatedOrganization(messageContext));
            provider.setDescription(description);
            provider.setBuiltInSupport(false);
            provider.setConfigurations(configurations);
            if (apiDefinitionInputStream != null) {
                provider.setApiDefinition(IOUtils.toString(apiDefinitionInputStream, StandardCharsets.UTF_8));
            }
            LLMProvider result = apiAdmin.addLLMProvider(provider);
            if (result != null) {
                LLMProviderResponseDTO llmProviderResponseDTO =
                        LLMProviderMappingUtil.fromProviderToProviderResponseDTO(result);
                URI location = new URI(RestApiConstants.RESOURCE_PATH_LLM_PROVIDER + "/" + result.getId());
                return Response.created(location).entity(llmProviderResponseDTO).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        } catch (IOException e) {
            log.warn("Error occurred trying to read api definition file");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (URISyntaxException e) {
            log.warn("Error while creating URI for new LLM Provider");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes a LLM provider by its ID.
     *
     * @param llmProviderId  The ID of the LLM provider to be deleted.
     * @param messageContext The message context containing necessary information for the operation.
     * @return A Response object indicating the result of the delete operation.
     * @throws APIManagementException If an error occurs while deleting the LLM provider.
     */
    @Override
    public Response deleteLLMProvider(String llmProviderId, MessageContext messageContext) throws APIManagementException {

        APIAdmin apiAdmin = new APIAdminImpl();
        String organization = RestApiUtil.getValidatedOrganization(messageContext);
        try {
            LLMProvider provider = apiAdmin.deleteLLMProvider(organization, llmProviderId, false);
            if (provider != null) {
                String info = String.format("{\"id\":\"%s\"}", llmProviderId);
                APIUtil.logAuditMessage(
                        APIConstants.AuditLogConstants.GATEWAY_ENVIRONMENTS,
                        info,
                        APIConstants.AuditLogConstants.DELETED,
                        RestApiCommonUtil.getLoggedInUsername()
                );
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        } catch (APIManagementException e) {
            log.warn("Error while deleting LLM Provider");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a list of all LLM providers in the organization.
     *
     * @param messageContext The message context containing necessary information for the operation.
     * @return A Response object containing the list of LLM providers.
     * @throws APIManagementException If an error occurs while retrieving the LLM providers.
     */
    @Override
    public Response getLLMProviders(MessageContext messageContext) throws APIManagementException {

        APIAdmin apiAdmin = new APIAdminImpl();
        String organization = RestApiUtil.getValidatedOrganization(messageContext);
        try {
            List<LLMProvider> LLMProviderList = apiAdmin.getLLMProviders(organization, null, null, null);
            LLMProviderSummaryResponseListDTO providerListDTO =
                    LLMProviderMappingUtil.fromProviderSummaryListToProviderSummaryListDTO(LLMProviderList);
            return Response.ok().entity(providerListDTO).build();
        } catch (APIManagementException e) {
            log.warn("Error while trying to retrieve LLM Providers");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates an existing LLM Provider.
     *
     * @param llmProviderId The ID of the LLM Provider to update.
     * @param id The ID of the provider (unused in logic).
     * @param name The name of the provider (unused in logic).
     * @param apiVersion The API version of the provider (unused in logic).
     * @param description The description of the provider.
     * @param configurations The configurations of the provider.
     * @param apiDefinitionInputStream The InputStream for the API definition.
     * @param apiDefinitionDetail The attachment containing API definition details.
     * @param messageContext The message context for the request.
     * @return The response with the updated LLM Provider or an error message.
     * @throws APIManagementException If an error occurs while updating the provider.
     */
    @Override
    public Response updateLLMProvider(String llmProviderId, String id, String name, String apiVersion,
                                      String description, String configurations, InputStream apiDefinitionInputStream
            , Attachment apiDefinitionDetail, MessageContext messageContext) throws APIManagementException {

        APIAdmin apiAdmin = new APIAdminImpl();
        try {
            LLMProvider provider = new LLMProvider();
            provider.setId(llmProviderId);
            provider.setDescription(description);
            if (apiDefinitionInputStream != null) {
                provider.setApiDefinition(IOUtils.toString(apiDefinitionInputStream, StandardCharsets.UTF_8));
            }
            provider.setOrganization(RestApiUtil.getValidatedOrganization(messageContext));
            provider.setConfigurations(configurations);
            LLMProvider result = apiAdmin.updateLLMProvider(provider);
            if (result != null) {
                LLMProviderResponseDTO llmProviderResponseDTO =
                        LLMProviderMappingUtil.fromProviderToProviderResponseDTO(result);
                URI location = new URI(RestApiConstants.RESOURCE_PATH_LLM_PROVIDER + "/" + result.getId());
                String info = "{'id':'" + llmProviderId + "'}";
                APIUtil.logAuditMessage(APIConstants.AuditLogConstants.GATEWAY_ENVIRONMENTS, info,
                        APIConstants.AuditLogConstants.UPDATED, RestApiCommonUtil.getLoggedInUsername());
                return Response.ok(location).entity(llmProviderResponseDTO).build();
            } else {
                return Response.status(Response.Status.NO_CONTENT).build();
            }
        } catch (IOException e) {
            log.warn("Error occurred trying to read api definition file");
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (URISyntaxException e) {
            log.warn("Error occurred while creating URI for new LLM Provider");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (APIManagementException e) {
            log.warn("Error occurred while update LLM Provider");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieves a specific LLM provider by its ID.
     *
     * @param llmProviderId  The ID of the LLM provider to be retrieved.
     * @param messageContext The message context containing necessary information for the operation.
     * @return A Response object containing the LLM provider details.
     * @throws APIManagementException If an error occurs while retrieving the LLM provider.
     */
    @Override
    public Response getLLMProvider(String llmProviderId, MessageContext messageContext) throws APIManagementException {

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
}
