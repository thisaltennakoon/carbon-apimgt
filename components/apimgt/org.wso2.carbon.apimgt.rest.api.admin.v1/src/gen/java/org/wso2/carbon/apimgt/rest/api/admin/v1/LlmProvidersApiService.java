package org.wso2.carbon.apimgt.rest.api.admin.v1;

import org.wso2.carbon.apimgt.rest.api.admin.v1.*;
import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.*;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import org.wso2.carbon.apimgt.api.APIManagementException;

import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.ErrorDTO;
import java.io.File;
import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.LLMProviderResponseDTO;
import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.LLMProviderSummaryResponseListDTO;

import java.util.List;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


public interface LlmProvidersApiService {
      public Response addLLMProvider(String id, String name, String apiVersion, String description, String configurations, InputStream apiDefinitionInputStream, Attachment apiDefinitionDetail, MessageContext messageContext) throws APIManagementException;
      public Response deleteLLMProvider(String llmProviderId, MessageContext messageContext) throws APIManagementException;
      public Response getLLMProvider(String llmProviderId, MessageContext messageContext) throws APIManagementException;
      public Response getLLMProviders(MessageContext messageContext) throws APIManagementException;
      public Response updateLLMProvider(String llmProviderId, String id, String name, String apiVersion, String description, String configurations, InputStream apiDefinitionInputStream, Attachment apiDefinitionDetail, MessageContext messageContext) throws APIManagementException;
}
