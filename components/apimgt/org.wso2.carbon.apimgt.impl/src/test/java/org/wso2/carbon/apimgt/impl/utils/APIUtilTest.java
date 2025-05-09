/*
 *
 *   Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.apimgt.impl.utils;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.model.API;
import org.wso2.carbon.apimgt.api.model.APICategory;
import org.wso2.carbon.apimgt.api.model.APIIdentifier;
import org.wso2.carbon.apimgt.api.model.CORSConfiguration;
import org.wso2.carbon.apimgt.api.model.Documentation;
import org.wso2.carbon.apimgt.api.model.DocumentationType;
import org.wso2.carbon.apimgt.api.model.Tier;
import org.wso2.carbon.apimgt.api.model.URITemplate;
import org.wso2.carbon.apimgt.api.model.policy.PolicyConstants;
import org.wso2.carbon.apimgt.api.model.policy.QuotaPolicy;
import org.wso2.carbon.apimgt.api.model.policy.RequestCountLimit;
import org.wso2.carbon.apimgt.api.model.policy.SubscriptionPolicy;
import org.wso2.carbon.apimgt.impl.*;
import org.wso2.carbon.apimgt.impl.config.APIMConfigService;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.dto.ConditionDto;
import org.wso2.carbon.apimgt.api.model.Environment;
import org.wso2.carbon.apimgt.impl.dto.ThrottleProperties;
import org.wso2.carbon.apimgt.impl.factory.KeyManagerHolder;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.base.ServerConfiguration;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.governance.api.common.dataobjects.GovernanceArtifact;
import org.wso2.carbon.governance.api.exception.GovernanceException;
import org.wso2.carbon.governance.api.generic.GenericArtifactManager;
import org.wso2.carbon.governance.api.generic.dataobjects.GenericArtifact;
import org.wso2.carbon.governance.api.util.GovernanceArtifactConfiguration;
import org.wso2.carbon.governance.api.util.GovernanceUtils;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.registry.core.Collection;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.RegistryConstants;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.Tag;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.core.service.RegistryService;
import org.wso2.carbon.registry.core.session.UserRegistry;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.wso2.carbon.apimgt.impl.utils.APIUtil.DISABLE_ROLE_VALIDATION_AT_SCOPE_CREATION;
import static org.wso2.carbon.apimgt.impl.utils.APIUtil.getOAuthConfigurationFromAPIMConfig;
import static org.wso2.carbon.apimgt.impl.utils.APIUtil.getOAuthConfigurationFromTenantRegistry;
import static org.wso2.carbon.base.CarbonBaseConstants.CARBON_HOME;

@RunWith(PowerMockRunner.class)
@PrepareForTest(
        {LogFactory.class, APIUtil.class, ServiceReferenceHolder.class, SSLSocketFactory.class, CarbonUtils.class,
                GovernanceUtils.class, MultitenantUtils.class,
                GenericArtifactManager.class, KeyManagerHolder.class, ApiMgtDAO.class, PrivilegedCarbonContext.class,
                IdentityUtil.class})
@PowerMockIgnore("javax.net.ssl.*")
public class APIUtilTest {

    private String tenantDomain = "Wso2.com";

    @Test
    public void testGetAPINamefromRESTAPI() throws Exception {

        String restAPI = "admin--map";
        String apiName = APIUtil.getAPINamefromRESTAPI(restAPI);

        Assert.assertEquals(apiName, "map");
    }



    @Test
    public void testIsValidURL() throws Exception {

        String validURL = "http://fsdfsfd.sda";

        Assert.assertTrue(APIUtil.isValidURL(validURL));

        String invalidURL = "sadafvsdfwef";

        Assert.assertFalse(APIUtil.isValidURL(invalidURL));
        Assert.assertFalse(APIUtil.isValidURL(null));
    }

    @Test
    public void testgGetUserNameWithTenantSuffix() throws Exception {

        String plainUserName = "john";

        String userNameWithTenantSuffix = APIUtil.getUserNameWithTenantSuffix(plainUserName);

        Assert.assertEquals("john@carbon.super", userNameWithTenantSuffix);

        String userNameWithDomain = "john@smith.com";

        userNameWithTenantSuffix = APIUtil.getUserNameWithTenantSuffix(userNameWithDomain);

        Assert.assertEquals("john@smith.com", userNameWithTenantSuffix);
    }

    @Test
    public void testGetRESTAPIScopesFromConfig() throws Exception {

        File siteConfFile = new File(Thread.currentThread().getContextClassLoader().
                getResource("tenant-conf.json").getFile());

        String tenantConfValue = FileUtils.readFileToString(siteConfFile);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(tenantConfValue);
        JSONObject restapiScopes = (JSONObject) json.get("RESTAPIScopes");

        Map<String, String> expectedScopes = new HashMap<String, String>();
        JSONArray scopes = (JSONArray) restapiScopes.get("Scope");
        JSONObject roleMappings = (JSONObject) restapiScopes.get("RoleMappings");

        for (Object scopeObj : scopes) {
            JSONObject scope = (JSONObject) scopeObj;
            String name = (String) scope.get("Name");
            String roles = (String) scope.get("Roles");
            expectedScopes.put(name, roles);
        }

        Map<String, String> restapiScopesFromConfig = APIUtil.getRESTAPIScopesFromConfig(restapiScopes, roleMappings);

        Assert.assertEquals(expectedScopes, restapiScopesFromConfig);
    }

    @Test
    public void testGetRESTAPIScopesFromConfigWithRoleMappings() throws Exception {

        File siteConfFile = new File(Thread.currentThread().getContextClassLoader().
                getResource("tenant-conf.json").getFile());

        String tenantConfValue = FileUtils.readFileToString(siteConfFile);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(tenantConfValue);
        JSONObject restapiScopes = (JSONObject) json.get("RESTAPIScopes");

        Map<String, String> expectedScopes = new HashMap<String, String>();
        JSONArray scopes = (JSONArray) restapiScopes.get("Scope");
        JSONObject roleMappings = new JSONObject();
        roleMappings.put("Internal/publisher", "publisher");

        for (Object scopeObj : scopes) {
            JSONObject scope = (JSONObject) scopeObj;
            String name = (String) scope.get("Name");
            String roles = (String) scope.get("Roles");
            //replace Internal/publisher role for publisher role and remove white spaces
            roles = roles.replace("Internal/publisher", "publisher");
            roles = roles.replace(" ", "");
            expectedScopes.put(name, roles);
        }

        Map<String, String> restapiScopesFromConfig = APIUtil.getRESTAPIScopesFromConfig(restapiScopes, roleMappings);

        Assert.assertEquals(expectedScopes, restapiScopesFromConfig);
    }

    @Test
    public void testIsSandboxEndpointsExists() throws Exception {

        API api = Mockito.mock(API.class);

        JSONObject sandboxEndpoints = new JSONObject();
        sandboxEndpoints.put("url", "https:\\/\\/localhost:9443\\/am\\/sample\\/pizzashack\\/v1\\/api\\/");
        sandboxEndpoints.put("config", null);

        JSONObject root = new JSONObject();
        root.put("sandbox_endpoints", sandboxEndpoints);
        root.put("endpoint_type", "http");

        Mockito.when(api.getEndpointConfig()).thenReturn(root.toJSONString());

        Assert.assertTrue("Cannot find sandbox endpoint", APIUtil.isSandboxEndpointsExists(root.toJSONString()));
    }

    @Test
    public void testIsSandboxEndpointsNotExists() throws Exception {

        API api = Mockito.mock(API.class);

        JSONObject productionEndpoints = new JSONObject();
        productionEndpoints.put("url", "https:\\/\\/localhost:9443\\/am\\/sample\\/pizzashack\\/v1\\/api\\/");
        productionEndpoints.put("config", null);

        JSONObject root = new JSONObject();
        root.put("production_endpoints", productionEndpoints);
        root.put("endpoint_type", "http");

        Mockito.when(api.getEndpointConfig()).thenReturn(root.toJSONString());

        Assert.assertFalse("Unexpected sandbox endpoint found", APIUtil.isSandboxEndpointsExists(root.toJSONString
                ()));
    }

    @Test
    public void testIsProductionEndpointsExists() throws Exception {

        API api = Mockito.mock(API.class);

        JSONObject productionEndpoints = new JSONObject();
        productionEndpoints.put("url", "https:\\/\\/localhost:9443\\/am\\/sample\\/pizzashack\\/v1\\/api\\/");
        productionEndpoints.put("config", null);

        JSONObject root = new JSONObject();
        root.put("production_endpoints", productionEndpoints);
        root.put("endpoint_type", "http");

        Mockito.when(api.getEndpointConfig()).thenReturn(root.toJSONString());

        Assert.assertTrue("Cannot find production endpoint", APIUtil.isProductionEndpointsExists(root.toJSONString
                ()));
    }

    @Test
    public void testIsProductionEndpointsNotExists() throws Exception {

        API api = Mockito.mock(API.class);

        JSONObject sandboxEndpoints = new JSONObject();
        sandboxEndpoints.put("url", "https:\\/\\/localhost:9443\\/am\\/sample\\/pizzashack\\/v1\\/api\\/");
        sandboxEndpoints.put("config", null);

        JSONObject root = new JSONObject();
        root.put("sandbox_endpoints", sandboxEndpoints);
        root.put("endpoint_type", "http");

        Mockito.when(api.getEndpointConfig()).thenReturn(root.toJSONString());

        Assert.assertFalse("Unexpected production endpoint found", APIUtil.isProductionEndpointsExists(root
                .toJSONString()));
    }

    @Test
    public void testIsProductionSandboxEndpointsExists() throws Exception {

        API api = Mockito.mock(API.class);

        JSONObject productionEndpoints = new JSONObject();
        productionEndpoints.put("url", "https:\\/\\/localhost:9443\\/am\\/sample\\/pizzashack\\/v1\\/api\\/");
        productionEndpoints.put("config", null);

        JSONObject sandboxEndpoints = new JSONObject();
        sandboxEndpoints.put("url", "https:\\/\\/localhost:9443\\/am\\/sample\\/pizzashack\\/v1\\/api\\/");
        sandboxEndpoints.put("config", null);

        JSONObject root = new JSONObject();
        root.put("production_endpoints", productionEndpoints);
        root.put("sandbox_endpoints", sandboxEndpoints);
        root.put("endpoint_type", "http");

        Mockito.when(api.getEndpointConfig()).thenReturn(root.toJSONString());

        Assert.assertTrue("Cannot find production endpoint", APIUtil.isProductionEndpointsExists(root.toJSONString
                ()));
        Assert.assertTrue("Cannot find sandbox endpoint", APIUtil.isSandboxEndpointsExists(root.toJSONString()));
    }

    @Test
    public void testIsProductionEndpointsInvalidJSON() throws Exception {

        Log log = Mockito.mock(Log.class);
        PowerMockito.mockStatic(LogFactory.class);
        Mockito.when(LogFactory.getLog(Mockito.any(Class.class))).thenReturn(log);

        API api = Mockito.mock(API.class);

        Mockito.when(api.getEndpointConfig()).thenReturn("</SomeXML>");

        Assert.assertFalse("Unexpected production endpoint found", APIUtil.isProductionEndpointsExists("</SomeXML>"));

        JSONObject productionEndpoints = new JSONObject();
        productionEndpoints.put("url", "https:\\/\\/localhost:9443\\/am\\/sample\\/pizzashack\\/v1\\/api\\/");
        productionEndpoints.put("config", null);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(productionEndpoints);

        Mockito.when(api.getEndpointConfig()).thenReturn(jsonArray.toJSONString());

        Assert.assertFalse("Unexpected production endpoint found", APIUtil.isProductionEndpointsExists(jsonArray
                .toJSONString()));
    }

    @Test
    public void testIsSandboxEndpointsInvalidJSON() throws Exception {

        Log log = Mockito.mock(Log.class);
        PowerMockito.mockStatic(LogFactory.class);
        Mockito.when(LogFactory.getLog(Mockito.any(Class.class))).thenReturn(log);

        API api = Mockito.mock(API.class);

        Mockito.when(api.getEndpointConfig()).thenReturn("</SomeXML>");
        Assert.assertFalse("Unexpected sandbox endpoint found", APIUtil.isSandboxEndpointsExists("</SomeXML>"));
        JSONObject sandboxEndpoints = new JSONObject();
        sandboxEndpoints.put("url", "https:\\/\\/localhost:9443\\/am\\/sample\\/pizzashack\\/v1\\/api\\/");
        sandboxEndpoints.put("config", null);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(sandboxEndpoints);

        Mockito.when(api.getEndpointConfig()).thenReturn(jsonArray.toJSONString());

        Assert.assertFalse("Unexpected sandbox endpoint found", APIUtil.isSandboxEndpointsExists(jsonArray
                .toJSONString()));
    }

    @Test
    public void testGetAPIInformation() throws Exception {
        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            GovernanceArtifact artifact = Mockito.mock(GovernanceArtifact.class);
            Registry registry = Mockito.mock(Registry.class);
            Resource resource = Mockito.mock(Resource.class);

            API expectedAPI = getUniqueAPI();

            String artifactPath = "";
            PowerMockito.mockStatic(GovernanceUtils.class);
            Mockito.when(GovernanceUtils.getArtifactPath(registry, expectedAPI.getUUID())).thenReturn(artifactPath);
            Mockito.when(registry.get(artifactPath)).thenReturn(resource);
            Mockito.when(resource.getLastModified()).thenReturn(expectedAPI.getLastUpdated());

            DateFormat df = new SimpleDateFormat("E MMM dd HH:mm:ss zzz yyyy", Locale.US);
            Date createdTime = df.parse(expectedAPI.getCreatedTime());
            Mockito.when(resource.getCreatedTime()).thenReturn(createdTime);

            ServiceReferenceHolderMockCreator holderMockCreator = new ServiceReferenceHolderMockCreator(1);
            APIManagerConfiguration apimConfiguration = holderMockCreator.getConfigurationServiceMockCreator().
                    getConfigurationMockCreator().getMock();

            CORSConfiguration corsConfiguration = expectedAPI.getCorsConfiguration();

            Mockito.when(apimConfiguration.getFirstProperty(APIConstants.CORS_CONFIGURATION_ACCESS_CTL_ALLOW_HEADERS)).
                    thenReturn(corsConfiguration.getAccessControlAllowHeaders().toString());
            Mockito.when(apimConfiguration.getFirstProperty(APIConstants.CORS_CONFIGURATION_ACCESS_CTL_ALLOW_METHODS)).
                    thenReturn(corsConfiguration.getAccessControlAllowMethods().toString());
            Mockito.when(apimConfiguration.getFirstProperty(APIConstants.CORS_CONFIGURATION_ACCESS_CTL_ALLOW_ORIGIN)).
                    thenReturn(corsConfiguration.getAccessControlAllowOrigins().toString());

            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_PROVIDER)).
                    thenReturn(expectedAPI.getId().getProviderName());
            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_NAME)).
                    thenReturn(expectedAPI.getId().getApiName());
            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_VERSION)).
                    thenReturn(expectedAPI.getId().getVersion());
            Mockito.when(artifact.getId()).thenReturn(expectedAPI.getUUID());

            ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
            PowerMockito.mockStatic(ApiMgtDAO.class);
            Mockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
            Mockito.when(apiMgtDAO.getAllEnvironments(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME))
                    .thenReturn(new ArrayList<org.wso2.carbon.apimgt.api.model.Environment>());
            API api = APIUtil.getAPIInformation(artifact, registry);

            Assert.assertEquals(expectedAPI.getId(), api.getId());
            Assert.assertEquals(expectedAPI.getUUID(), api.getUUID());

            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_PROVIDER);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_NAME);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_VERSION);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_THUMBNAIL_URL);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getLifecycleState();
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_CONTEXT);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_VISIBILITY);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_VISIBLE_ROLES);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_VISIBLE_TENANTS);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_TRANSPORTS);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_INSEQUENCE);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_OUTSEQUENCE);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_FAULTSEQUENCE);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_DESCRIPTION);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_REDIRECT_URL);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_BUSS_OWNER);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_OWNER);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_ADVERTISE_ONLY);
            Mockito.verify(artifact, Mockito.atLeastOnce()).getAttribute(APIConstants.API_OVERVIEW_ENVIRONMENTS);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }


    @Test
    public void testIsPerAPISequence() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "in" + RegistryConstants.PATH_SEPARATOR;

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);
        Mockito.when(registry.resourceExists(eq(path))).thenReturn(true);

        Collection collection = Mockito.mock(Collection.class);
        Mockito.when(registry.get(eq(path))).thenReturn(collection);

        String[] childPaths = {"test"};
        Mockito.when(collection.getChildren()).thenReturn(childPaths);

        InputStream sampleSequence = new FileInputStream(Thread.currentThread().getContextClassLoader().
                getResource("sampleSequence.xml").getFile());
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(registry.get(eq("test"))).thenReturn(resource);
        Mockito.when(resource.getContentStream()).thenReturn(sampleSequence);

        boolean isPerAPiSequence = APIUtil.isPerAPISequence("sample", 1, apiIdentifier, "in");

        Assert.assertTrue(isPerAPiSequence);
    }

    @Test
    public void testIsPerAPISequenceResourceMissing() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "in" + RegistryConstants.PATH_SEPARATOR;

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);
        Mockito.when(registry.resourceExists(eq(path))).thenReturn(false);

        boolean isPerAPiSequence = APIUtil.isPerAPISequence("sample", 1, apiIdentifier, "in");

        Assert.assertFalse(isPerAPiSequence);
    }

    @Test
    public void testIsPerAPISequenceSequenceMissing() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "in" + RegistryConstants.PATH_SEPARATOR;

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);
        Mockito.when(registry.resourceExists(eq(path))).thenReturn(true);
        Mockito.when(registry.get(eq(path))).thenReturn(null);

        boolean isPerAPiSequence = APIUtil.isPerAPISequence("sample", 1, apiIdentifier, "in");

        Assert.assertFalse(isPerAPiSequence);
    }

    @Test
    public void testIsPerAPISequenceNoPathsInCollection() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "in" + RegistryConstants.PATH_SEPARATOR;

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);
        Mockito.when(registry.resourceExists(eq(path))).thenReturn(false);

        Collection collection = Mockito.mock(Collection.class);
        Mockito.when(registry.get(eq(path))).thenReturn(collection);

        String[] childPaths = {};
        Mockito.when(collection.getChildren()).thenReturn(childPaths);

        boolean isPerAPiSequence = APIUtil.isPerAPISequence("sample", 1, apiIdentifier, "in");

        Assert.assertFalse(isPerAPiSequence);
    }

    @Test
    public void testGetCustomInSequence() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);

        Collection collection = Mockito.mock(Collection.class);
        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "in" + RegistryConstants.PATH_SEPARATOR;

        Mockito.when(registry.get(eq(path))).thenReturn(collection);

        String[] childPaths = {"test"};
        Mockito.when(collection.getChildren()).thenReturn(childPaths);

        InputStream sampleSequence = new FileInputStream(Thread.currentThread().getContextClassLoader().
                getResource("sampleSequence.xml").getFile());

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(registry.get(eq("test"))).thenReturn(resource);
        Mockito.when(resource.getContentStream()).thenReturn(sampleSequence);

        OMElement customSequence = APIUtil.getCustomSequence("sample", 1, "in", apiIdentifier);

        Assert.assertNotNull(customSequence);
        sampleSequence.close();
    }

    @Test
    public void testGetCustomOutSequence() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);

        Collection collection = Mockito.mock(Collection.class);
        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "out" + RegistryConstants.PATH_SEPARATOR;

        Mockito.when(registry.get(eq(path))).thenReturn(collection);

        String[] childPaths = {"test"};
        Mockito.when(collection.getChildren()).thenReturn(childPaths);

        InputStream sampleSequence = new FileInputStream(Thread.currentThread().getContextClassLoader().
                getResource("sampleSequence.xml").getFile());

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(registry.get(eq("test"))).thenReturn(resource);
        Mockito.when(resource.getContentStream()).thenReturn(sampleSequence);

        OMElement customSequence = APIUtil.getCustomSequence("sample", 1, "out", apiIdentifier);

        Assert.assertNotNull(customSequence);
        sampleSequence.close();
    }

    @Test
    public void testGetCustomFaultSequence() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);

        Collection collection = Mockito.mock(Collection.class);
        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "fault" + RegistryConstants.PATH_SEPARATOR;

        Mockito.when(registry.get(eq(path))).thenReturn(collection);

        String[] childPaths = {"test"};
        Mockito.when(collection.getChildren()).thenReturn(childPaths);

        InputStream sampleSequence = new FileInputStream(Thread.currentThread().getContextClassLoader().
                getResource("sampleSequence.xml").getFile());

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(registry.get(eq("test"))).thenReturn(resource);
        Mockito.when(resource.getContentStream()).thenReturn(sampleSequence);

        OMElement customSequence = APIUtil.getCustomSequence("sample", 1, "fault", apiIdentifier);

        Assert.assertNotNull(customSequence);
        sampleSequence.close();
    }

    @Test
    public void testGetCustomSequenceNotFound() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);

        Collection collection = Mockito.mock(Collection.class);
        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "custom" + RegistryConstants.PATH_SEPARATOR;

        Mockito.when(registry.get(eq(path))).thenReturn(null, collection);

        String[] childPaths = {"test"};
        Mockito.when(collection.getChildren()).thenReturn(childPaths);

        String expectedUUID = UUID.randomUUID().toString();

        InputStream sampleSequence = new FileInputStream(Thread.currentThread().getContextClassLoader().
                getResource("sampleSequence.xml").getFile());

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(registry.get(eq("test"))).thenReturn(resource);
        Mockito.when(resource.getContentStream()).thenReturn(sampleSequence);
        Mockito.when(resource.getUUID()).thenReturn(expectedUUID);

        OMElement customSequence = APIUtil.getCustomSequence("sample", 1, "custom", apiIdentifier);

        Assert.assertNotNull(customSequence);
        sampleSequence.close();
    }

    @Test
    public void testGetCustomSequenceNull() throws Exception {

        APIIdentifier apiIdentifier = Mockito.mock(APIIdentifier.class);

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RegistryService registryService = Mockito.mock(RegistryService.class);
        UserRegistry registry = Mockito.mock(UserRegistry.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
        Mockito.when(registryService.getGovernanceSystemRegistry(eq(1))).thenReturn(registry);

        Collection collection = Mockito.mock(Collection.class);
        String artifactPath = APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getProviderName() + RegistryConstants.PATH_SEPARATOR +
                apiIdentifier.getApiName() + RegistryConstants.PATH_SEPARATOR + apiIdentifier
                .getVersion();
        String path = artifactPath + RegistryConstants.PATH_SEPARATOR + "custom" + RegistryConstants.PATH_SEPARATOR;

        Mockito.when(registry.get(eq(path))).thenReturn(null, null);

        String[] childPaths = {"test"};
        Mockito.when(collection.getChildren()).thenReturn(childPaths);

        String expectedUUID = UUID.randomUUID().toString();

        InputStream sampleSequence = new FileInputStream(Thread.currentThread().getContextClassLoader().
                getResource("sampleSequence.xml").getFile());

        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(registry.get(eq("test"))).thenReturn(resource);
        Mockito.when(resource.getContentStream()).thenReturn(sampleSequence);
        Mockito.when(resource.getUUID()).thenReturn(expectedUUID);

        OMElement customSequence = APIUtil.getCustomSequence("sample", 1, "custom", apiIdentifier);

        Assert.assertNull(customSequence);
        sampleSequence.close();
    }

    @Test
    public void testCreateSwaggerJSONContent() throws Exception {

        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
            APIManagerConfigurationService apiManagerConfigurationService = Mockito.mock(APIManagerConfigurationService
                    .class);
            APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
            Environment environment = Mockito.mock(Environment.class);
            Map<String, Environment> environmentMap = new HashMap<String, Environment>();
            environmentMap.put("Production", environment);

            PowerMockito.mockStatic(ServiceReferenceHolder.class);
            Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
            Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).thenReturn
                    (apiManagerConfigurationService);
            Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
            Mockito.when(apiManagerConfiguration.getApiGatewayEnvironments()).thenReturn(environmentMap);
            Mockito.when(environment.getApiGatewayEndpoint()).thenReturn("");

            ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
            PowerMockito.mockStatic(ApiMgtDAO.class);
            Mockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
            Mockito.when(apiMgtDAO.getAllEnvironments(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME))
                    .thenReturn(new ArrayList<org.wso2.carbon.apimgt.api.model.Environment>());
            String swaggerJSONContent = APIUtil.createSwaggerJSONContent(getUniqueAPI());

            Assert.assertNotNull(swaggerJSONContent);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    @Test
    public void testIsRoleNameExist() throws Exception {

        String userName = "John";
        String roleName = "developer";

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RealmService realmService = Mockito.mock(RealmService.class);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(realmService.getTenantUserRealm(Mockito.anyInt())).thenReturn(userRealm);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        Mockito.when(userStoreManager.isExistingRole(roleName)).thenReturn(true);

        Mockito.when(userStoreManager.isExistingRole("NonExistingDomain/role")).thenThrow(UserStoreException.class);
        Mockito.when(userStoreManager.isExistingRole("NonExistingDomain/")).thenThrow(UserStoreException.class);

        Assert.assertTrue(APIUtil.isRoleNameExist(userName, roleName));
        Assert.assertFalse(APIUtil.isRoleNameExist(userName, "NonExistingDomain/role"));
        Assert.assertFalse(APIUtil.isRoleNameExist(userName, "NonExistingDomain/"));
        Assert.assertTrue(APIUtil.isRoleNameExist(userName, ""));//allow adding empty role
    }

    @Test
    public void testIsRoleNameNotExist() throws Exception {

        String userName = "John";
        String roleName = "developer";

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        RealmService realmService = Mockito.mock(RealmService.class);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(realmService.getTenantUserRealm(Mockito.anyInt())).thenReturn(userRealm);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        Mockito.when(userStoreManager.isExistingRole(roleName)).thenReturn(false);

        Assert.assertFalse(APIUtil.isRoleNameExist(userName, roleName));
    }

    @Test
    public void testIsRoleNameExistDisableRoleValidation() throws Exception {

        String userName = "John";
        String roleName = "developer";

        System.setProperty(DISABLE_ROLE_VALIDATION_AT_SCOPE_CREATION, "true");

        Assert.assertTrue(APIUtil.isRoleNameExist(userName, roleName));

        Assert.assertTrue(APIUtil.isRoleNameExist(userName, null));

        Assert.assertTrue(APIUtil.isRoleNameExist(userName, ""));

        System.clearProperty(DISABLE_ROLE_VALIDATION_AT_SCOPE_CREATION);
    }

    @Test
    public void testGetDocumentation() throws GovernanceException, APIManagementException {

        PowerMockito.mockStatic(CarbonUtils.class);
        ServerConfiguration serverConfiguration = Mockito.mock(ServerConfiguration.class);
        Mockito.when(serverConfiguration.getFirstProperty("WebContextRoot")).thenReturn("/abc").thenReturn("/");
        PowerMockito.when(CarbonUtils.getServerConfiguration()).thenReturn(serverConfiguration);
        GenericArtifact genericArtifact = Mockito.mock(GenericArtifact.class);
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_TYPE)).thenReturn(DocumentationType.HOWTO.getType
                ()).thenReturn(DocumentationType.PUBLIC_FORUM.getType()).thenReturn(DocumentationType.SUPPORT_FORUM
                .getType())
                .thenReturn(DocumentationType.API_MESSAGE_FORMAT.getType()).thenReturn(DocumentationType
                .SAMPLES.getType())
                .thenReturn(DocumentationType.OTHER.getType());
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_NAME)).thenReturn("Docname");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_VISIBILITY)).thenReturn(null).thenReturn
                (Documentation.DocumentVisibility.API_LEVEL.name()).thenReturn(Documentation.DocumentVisibility
                .PRIVATE.name())
                .thenReturn(Documentation.DocumentVisibility.OWNER_ONLY.name());
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_SOURCE_TYPE)).thenReturn(Documentation
                .DocumentSourceType.URL
                .name())
                .thenReturn(Documentation.DocumentSourceType.FILE.name());
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_SOURCE_URL)).thenReturn("https://localhost");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_FILE_PATH)).thenReturn("file://abc");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_OTHER_TYPE_NAME)).thenReturn("abc");
        APIUtil.getDocumentation(genericArtifact);
        APIUtil.getDocumentation(genericArtifact);
        APIUtil.getDocumentation(genericArtifact);
        APIUtil.getDocumentation(genericArtifact);
        APIUtil.getDocumentation(genericArtifact);
        APIUtil.getDocumentation(genericArtifact);

    }

    @Test
    public void testGetDocumentationByDocCreator() throws Exception {

        PowerMockito.mockStatic(CarbonUtils.class);
        ServerConfiguration serverConfiguration = Mockito.mock(ServerConfiguration.class);
        Mockito.when(serverConfiguration.getFirstProperty("WebContextRoot")).thenReturn("/abc").thenReturn("/");
        PowerMockito.when(CarbonUtils.getServerConfiguration()).thenReturn(serverConfiguration);
        GenericArtifact genericArtifact = Mockito.mock(GenericArtifact.class);
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_TYPE)).thenReturn(DocumentationType.HOWTO.getType
                ()).thenReturn(DocumentationType.PUBLIC_FORUM.getType()).thenReturn(DocumentationType.SUPPORT_FORUM
                .getType())
                .thenReturn(DocumentationType.API_MESSAGE_FORMAT.getType()).thenReturn(DocumentationType
                .SAMPLES.getType())
                .thenReturn(DocumentationType.OTHER.getType());
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_NAME)).thenReturn("Docname");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_VISIBILITY)).thenReturn(null).thenReturn
                (Documentation.DocumentVisibility.API_LEVEL.name()).thenReturn(Documentation.DocumentVisibility
                .PRIVATE.name())
                .thenReturn(Documentation.DocumentVisibility.OWNER_ONLY.name());
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_SOURCE_TYPE)).thenReturn(Documentation
                .DocumentSourceType.URL
                .name())
                .thenReturn(Documentation.DocumentSourceType.FILE.name());
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_SOURCE_URL)).thenReturn("https://localhost");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_FILE_PATH)).thenReturn("file://abc");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_OTHER_TYPE_NAME)).thenReturn("abc");
        APIUtil.getDocumentation(genericArtifact, "admin");
        APIUtil.getDocumentation(genericArtifact, "admin");
        APIUtil.getDocumentation(genericArtifact, "admin");
        APIUtil.getDocumentation(genericArtifact, "admin");
        APIUtil.getDocumentation(genericArtifact, "admin");
        APIUtil.getDocumentation(genericArtifact, "admin");
        APIUtil.getDocumentation(genericArtifact, "admin@wso2.com");
    }

    @Test
    public void testVisibilityOfDoc() throws Exception {

        PowerMockito.mockStatic(CarbonUtils.class);
        ServerConfiguration serverConfiguration = Mockito.mock(ServerConfiguration.class);
        Mockito.when(serverConfiguration.getFirstProperty("WebContextRoot")).thenReturn("/abc").thenReturn("/");
        PowerMockito.when(CarbonUtils.getServerConfiguration()).thenReturn(serverConfiguration);
        GenericArtifact genericArtifact = Mockito.mock(GenericArtifact.class);
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_TYPE)).thenReturn(DocumentationType.HOWTO.getType
                ()).thenReturn(DocumentationType.PUBLIC_FORUM.getType()).thenReturn(DocumentationType.SUPPORT_FORUM
                .getType())
                .thenReturn(DocumentationType.API_MESSAGE_FORMAT.getType()).thenReturn(DocumentationType
                .SAMPLES.getType())
                .thenReturn(DocumentationType.OTHER.getType());
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_NAME)).thenReturn("Docname");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_SOURCE_TYPE)).thenReturn(Documentation
                .DocumentSourceType.URL
                .name())
                .thenReturn(Documentation.DocumentSourceType.FILE.name());
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_SOURCE_URL)).thenReturn("https://localhost");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_FILE_PATH)).thenReturn("file://abc");
        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_OTHER_TYPE_NAME)).thenReturn("abc");

        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_VISIBILITY)).thenReturn(null);
        Assert.assertEquals(APIUtil.getDocumentation(genericArtifact, "admin@wso2.com").getVisibility()
                .name(), Documentation.DocumentVisibility.API_LEVEL.name());

        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_VISIBILITY)).
                thenReturn(Documentation.DocumentVisibility.API_LEVEL.name());
        Assert.assertEquals(APIUtil.getDocumentation(genericArtifact, "admin@wso2.com").getVisibility().
                name(), Documentation.DocumentVisibility.API_LEVEL.name());

        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_VISIBILITY)).
                thenReturn(Documentation.DocumentVisibility.PRIVATE.name());
        Assert.assertEquals(APIUtil.getDocumentation(genericArtifact, "admin@wso2.com").getVisibility().
                name(), Documentation.DocumentVisibility.PRIVATE.name());

        Mockito.when(genericArtifact.getAttribute(APIConstants.DOC_VISIBILITY)).
                thenReturn(Documentation.DocumentVisibility.OWNER_ONLY.name());
        Assert.assertEquals(APIUtil.getDocumentation(genericArtifact, "admin@wso2.com").getVisibility().
                name(), Documentation.DocumentVisibility.OWNER_ONLY.name());
    }

    @Test
    public void testCreateDocArtifactContent() throws GovernanceException, APIManagementException {

        API api = getUniqueAPI();
        PowerMockito.mockStatic(CarbonUtils.class);
        ServerConfiguration serverConfiguration = Mockito.mock(ServerConfiguration.class);
        Mockito.when(serverConfiguration.getFirstProperty("WebContextRoot")).thenReturn("/abc").thenReturn("/");
        PowerMockito.when(CarbonUtils.getServerConfiguration()).thenReturn(serverConfiguration);
        GenericArtifact genericArtifact = Mockito.mock(GenericArtifact.class);
        Documentation documentation = new Documentation(DocumentationType.HOWTO, "this is a doc");
        documentation.setSourceType(Documentation.DocumentSourceType.FILE);
        documentation.setCreatedDate(new Date(System.currentTimeMillis()));
        documentation.setSummary("abcde");
        documentation.setVisibility(Documentation.DocumentVisibility.API_LEVEL);
        documentation.setSourceUrl("/abcd/def");
        documentation.setOtherTypeName("aa");
        APIUtil.createDocArtifactContent(genericArtifact, api.getId(), documentation);
        documentation.setSourceType(Documentation.DocumentSourceType.INLINE);
        APIUtil.createDocArtifactContent(genericArtifact, api.getId(), documentation);
        documentation.setSourceType(Documentation.DocumentSourceType.URL);
        APIUtil.createDocArtifactContent(genericArtifact, api.getId(), documentation);

        try {
            documentation.setSourceType(Documentation.DocumentSourceType.URL);
            Mockito.doThrow(GovernanceException.class).when(genericArtifact).setAttribute(APIConstants
                            .DOC_SOURCE_URL,
                    documentation.getSourceUrl());
            APIUtil.createDocArtifactContent(genericArtifact, api.getId(), documentation);
            Assert.fail();
        } catch (APIManagementException ex) {
            Assert.assertTrue(ex.getMessage().contains("Failed to create doc artifact content from :"));
        }
    }

    @Test
    public void testGetArtifactManager() {

        PowerMockito.mockStatic(GenericArtifactManager.class);
        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        Registry registry = Mockito.mock(UserRegistry.class);
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            PowerMockito.mockStatic(GovernanceUtils.class);
            PowerMockito.doNothing().when(GovernanceUtils.class, "loadGovernanceArtifacts", (UserRegistry) registry);
            Mockito.when(GovernanceUtils.findGovernanceArtifactConfiguration(APIConstants.API_KEY, registry))
                    .thenReturn(Mockito.mock(GovernanceArtifactConfiguration.class)).thenReturn(null).thenThrow
                    (RegistryException.class);
            GenericArtifactManager genericArtifactManager = Mockito.mock(GenericArtifactManager.class);
            PowerMockito.whenNew(GenericArtifactManager.class).withArguments(registry, APIConstants.API_KEY)
                    .thenReturn(genericArtifactManager);
            GenericArtifactManager retrievedGenericArtifactManager = APIUtil.getArtifactManager(registry,
                    APIConstants.API_KEY);
            Assert.assertEquals(genericArtifactManager, retrievedGenericArtifactManager);
            APIUtil.getArtifactManager(registry, APIConstants.API_KEY);
            APIUtil.getArtifactManager(registry, APIConstants.API_KEY);
            Assert.fail();
        } catch (APIManagementException ex) {
            Assert.assertTrue(ex.getMessage().contains("Failed to initialize GenericArtifactManager"));
        } catch (RegistryException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

    }

    @Test
    public void testGetAPIWithGovernanceArtifact() throws Exception {

        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            API expectedAPI = getUniqueAPI();

            final String provider = expectedAPI.getId().getProviderName();
            final String tenantDomain = org.wso2.carbon.utils.multitenancy.MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME;

            final int tenantId = -1234;

            System.setProperty("carbon.home", "");

            File siteConfFile = new File(Thread.currentThread().getContextClassLoader().
                    getResource("tenant-conf.json").getFile());

            String tenantConfValue = FileUtils.readFileToString(siteConfFile);

            GovernanceArtifact artifact = Mockito.mock(GovernanceArtifact.class);
            Registry registry = Mockito.mock(Registry.class);
            ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
            Resource resource = Mockito.mock(Resource.class);
            ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
            RealmService realmService = Mockito.mock(RealmService.class);
            TenantManager tenantManager = Mockito.mock(TenantManager.class);
            APIManagerConfigurationService apiManagerConfigurationService = Mockito.mock
                    (APIManagerConfigurationService.class);
            APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
            ThrottleProperties throttleProperties = Mockito.mock(ThrottleProperties.class);
            SubscriptionPolicy policy = Mockito.mock(SubscriptionPolicy.class);
            SubscriptionPolicy[] policies = new SubscriptionPolicy[]{policy};
            QuotaPolicy quotaPolicy = Mockito.mock(QuotaPolicy.class);
            RequestCountLimit limit = Mockito.mock(RequestCountLimit.class);
            PrivilegedCarbonContext carbonContext = Mockito.mock(PrivilegedCarbonContext.class);
            RegistryService registryService = Mockito.mock(RegistryService.class);
            UserRegistry userRegistry = Mockito.mock(UserRegistry.class);

            PowerMockito.mockStatic(ApiMgtDAO.class);
            PowerMockito.mockStatic(GovernanceUtils.class);
            PowerMockito.mockStatic(MultitenantUtils.class);
            PowerMockito.mockStatic(ServiceReferenceHolder.class);

            Mockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
            Mockito.when(apiMgtDAO.getAPIID(Mockito.any(String.class))).thenReturn(123);
            Mockito.when(apiMgtDAO.getPolicyNames(PolicyConstants.POLICY_LEVEL_SUB, provider)).thenReturn(new
                    String[]{"Unlimited"});
            Mockito.when(artifact.getId()).thenReturn("");
            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_PROVIDER)).thenReturn(provider);
            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_CACHE_TIMEOUT)).thenReturn("15");
            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_TIER)).thenReturn("Unlimited");
            Mockito.when(MultitenantUtils.getTenantDomain(provider)).thenReturn(tenantDomain);
            Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
            Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
            APIMConfigService apimConfigService = Mockito.mock(APIMConfigService.class);
            Mockito.when(serviceReferenceHolder.getApimConfigService()).thenReturn(apimConfigService);
            Mockito.when(apimConfigService.getTenantConfig(tenantDomain)).thenReturn(tenantConfValue);

            Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
            Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
            Mockito.when(tenantManager.getTenantId(tenantDomain)).thenReturn(tenantId);
            Mockito.when(registryService.getConfigSystemRegistry(tenantId)).thenReturn(userRegistry);
            String artifactPath = "";
            Mockito.when(GovernanceUtils.getArtifactPath(registry, "")).thenReturn(artifactPath);
            Mockito.when(registry.get(artifactPath)).thenReturn(resource);
            Mockito.when(resource.getLastModified()).thenReturn(expectedAPI.getLastUpdated());
            Mockito.when(resource.getCreatedTime()).thenReturn(expectedAPI.getLastUpdated());
            Mockito.when(resource.getContent()).thenReturn(tenantConfValue.getBytes());
            Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).thenReturn
                    (apiManagerConfigurationService);
            Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn
                    (apiManagerConfiguration);
            Mockito.when(apiManagerConfiguration.getThrottleProperties()).thenReturn(throttleProperties);
            Mockito.when(apiMgtDAO.getSubscriptionPolicies(tenantId)).thenReturn(policies);
            Mockito.when(policy.getDefaultQuotaPolicy()).thenReturn(quotaPolicy);
            Mockito.when(quotaPolicy.getLimit()).thenReturn(limit);
            Mockito.when(registry.getTags(artifactPath)).thenReturn(getTagsFromSet(expectedAPI.getTags()));

            ArrayList<URITemplate> urlList = getURLTemplateList(expectedAPI.getUriTemplates());
            Mockito.when(apiMgtDAO.getAllURITemplates(Mockito.anyString(), Mockito.anyString())).thenReturn(urlList);

            CORSConfiguration corsConfiguration = expectedAPI.getCorsConfiguration();

            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants
                    .CORS_CONFIGURATION_ACCESS_CTL_ALLOW_HEADERS)).
                    thenReturn(corsConfiguration.getAccessControlAllowHeaders().toString());
            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants
                    .CORS_CONFIGURATION_ACCESS_CTL_ALLOW_METHODS)).
                    thenReturn(corsConfiguration.getAccessControlAllowMethods().toString());
            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants
                    .CORS_CONFIGURATION_ACCESS_CTL_ALLOW_ORIGIN)).
                    thenReturn(corsConfiguration.getAccessControlAllowOrigins().toString());

            API api = APIUtil.getAPI(artifact);

            Assert.assertNotNull(api);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    @Test
    public void testGetAPIWithGovernanceArtifactAdvancedThrottlingDisabled() throws Exception {

        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            API expectedAPI = getUniqueAPI();

            final String provider = expectedAPI.getId().getProviderName();
            final String tenantDomain =
                    org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_DOMAIN_NAME;
            final int tenantId = -1234;

            System.setProperty("carbon.home", "");

            File siteConfFile = new File(Thread.currentThread().getContextClassLoader().
                    getResource("tenant-conf.json").getFile());

            String tenantConfValue = FileUtils.readFileToString(siteConfFile);

            GovernanceArtifact artifact = Mockito.mock(GovernanceArtifact.class);
            Registry registry = Mockito.mock(Registry.class);
            ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
            Resource resource = Mockito.mock(Resource.class);
            ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
            RealmService realmService = Mockito.mock(RealmService.class);
            TenantManager tenantManager = Mockito.mock(TenantManager.class);
            APIManagerConfigurationService apiManagerConfigurationService = Mockito.mock(APIManagerConfigurationService
                    .class);
            APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
            ThrottleProperties throttleProperties = Mockito.mock(ThrottleProperties.class);
            SubscriptionPolicy policy = Mockito.mock(SubscriptionPolicy.class);
            SubscriptionPolicy[] policies = new SubscriptionPolicy[]{policy};
            QuotaPolicy quotaPolicy = Mockito.mock(QuotaPolicy.class);
            RequestCountLimit limit = Mockito.mock(RequestCountLimit.class);
            RegistryService registryService = Mockito.mock(RegistryService.class);
            UserRegistry userRegistry = Mockito.mock(UserRegistry.class);
            APIMConfigService apimConfigService = Mockito.mock(APIMConfigService.class);
            Mockito.when(serviceReferenceHolder.getApimConfigService()).thenReturn(apimConfigService);
            Mockito.when(apimConfigService.getTenantConfig(tenantDomain)).thenReturn(tenantConfValue);
            PowerMockito.mockStatic(ApiMgtDAO.class);
            PowerMockito.mockStatic(GovernanceUtils.class);
            PowerMockito.mockStatic(MultitenantUtils.class);
            PowerMockito.mockStatic(ServiceReferenceHolder.class);
            Mockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
            Mockito.when(apiMgtDAO.getAPIID(Mockito.any(String.class))).thenReturn(123);
            Mockito.when(apiMgtDAO.getPolicyNames(PolicyConstants.POLICY_LEVEL_SUB, provider)).thenReturn(new
                    String[]{"Unlimited"});
            Mockito.when(artifact.getId()).thenReturn("");
            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_PROVIDER)).thenReturn(provider);
            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_CACHE_TIMEOUT)).thenReturn("15");
            Mockito.when(artifact.getAttribute(APIConstants.API_OVERVIEW_TIER)).thenReturn("Unlimited");
            Mockito.when(MultitenantUtils.getTenantDomain(provider)).thenReturn(tenantDomain);
            Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
            Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
            Mockito.when(serviceReferenceHolder.getRegistryService()).thenReturn(registryService);
            Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
            Mockito.when(tenantManager.getTenantId(tenantDomain)).thenReturn(tenantId);
            Mockito.when(registryService.getConfigSystemRegistry(tenantId)).thenReturn(userRegistry);
            String artifactPath = "";
            Mockito.when(GovernanceUtils.getArtifactPath(registry, "")).thenReturn(artifactPath);
            Mockito.when(registry.get(artifactPath)).thenReturn(resource);
            Mockito.when(resource.getLastModified()).thenReturn(expectedAPI.getLastUpdated());
            Mockito.when(resource.getCreatedTime()).thenReturn(expectedAPI.getLastUpdated());
            Mockito.when(resource.getContent()).thenReturn(tenantConfValue.getBytes());
            Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService()).thenReturn
                    (apiManagerConfigurationService);
            Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration())
                    .thenReturn(apiManagerConfiguration);
            Mockito.when(apiManagerConfiguration.getThrottleProperties()).thenReturn(throttleProperties);
            Mockito.when(apiMgtDAO.getSubscriptionPolicies(tenantId)).thenReturn(policies);
            Mockito.when(policy.getDefaultQuotaPolicy()).thenReturn(quotaPolicy);
            Mockito.when(quotaPolicy.getLimit()).thenReturn(limit);
            Mockito.when(registry.getTags(artifactPath)).thenReturn(getTagsFromSet(expectedAPI.getTags()));

            ArrayList<URITemplate> urlList = getURLTemplateList(expectedAPI.getUriTemplates());
            Mockito.when(apiMgtDAO.getAllURITemplates(Mockito.anyString(), Mockito.anyString())).thenReturn(urlList);

            CORSConfiguration corsConfiguration = expectedAPI.getCorsConfiguration();

            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants
                    .CORS_CONFIGURATION_ACCESS_CTL_ALLOW_HEADERS)).
                    thenReturn(corsConfiguration.getAccessControlAllowHeaders().toString());
            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants
                    .CORS_CONFIGURATION_ACCESS_CTL_ALLOW_METHODS)).
                    thenReturn(corsConfiguration.getAccessControlAllowMethods().toString());
            Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants
                    .CORS_CONFIGURATION_ACCESS_CTL_ALLOW_ORIGIN)).
                    thenReturn(corsConfiguration.getAccessControlAllowOrigins().toString());

            API api = APIUtil.getAPI(artifact);

            Assert.assertNotNull(api);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    private API getUniqueAPI() {

        APIIdentifier apiIdentifier = new APIIdentifier(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                UUID.randomUUID().toString());
        API api = new API(apiIdentifier);
        api.setStatus(APIConstants.CREATED);
        api.setContext(UUID.randomUUID().toString());

        Set<String> environments = new HashSet<String>();
        environments.add(UUID.randomUUID().toString());

        URITemplate uriTemplate = new URITemplate();
        uriTemplate.setAuthType("None");
        uriTemplate.setHTTPVerb("GET");
        uriTemplate.setThrottlingTier("Unlimited");
        uriTemplate.setUriTemplate("/*");
        Set<URITemplate> uriTemplates = new HashSet<URITemplate>();
        uriTemplates.add(uriTemplate);

        uriTemplate = new URITemplate();
        uriTemplate.setAuthType("None");
        uriTemplate.setHTTPVerb("GET");
        uriTemplate.setThrottlingTier("Unlimited");
        uriTemplate.setUriTemplate("/get");
        uriTemplates.add(uriTemplate);

        uriTemplate = new URITemplate();
        uriTemplate.setAuthType("None");
        uriTemplate.setHTTPVerb("POST");
        uriTemplate.setThrottlingTier("Unlimited");
        uriTemplate.setUriTemplate("/*");
        uriTemplates.add(uriTemplate);

        uriTemplate = new URITemplate();
        uriTemplate.setAuthType("None");
        uriTemplate.setHTTPVerb("POST");
        uriTemplate.setThrottlingTier("Unlimited");
        uriTemplate.setUriTemplate("/post");
        uriTemplates.add(uriTemplate);

        uriTemplate = new URITemplate();
        uriTemplate.setAuthType("None");
        uriTemplate.setHTTPVerb("DELETE");
        uriTemplate.setThrottlingTier("Unlimited");
        uriTemplate.setUriTemplate("/*");
        uriTemplates.add(uriTemplate);

        uriTemplate = new URITemplate();
        uriTemplate.setAuthType("None");
        uriTemplate.setHTTPVerb("PUT");
        uriTemplate.setThrottlingTier("Unlimited");
        uriTemplate.setUriTemplate("/*");
        uriTemplates.add(uriTemplate);

        uriTemplate = new URITemplate();
        uriTemplate.setAuthType("None");
        uriTemplate.setHTTPVerb("PUT");
        uriTemplate.setThrottlingTier("Unlimited");
        uriTemplate.setUriTemplate("/put");
        uriTemplates.add(uriTemplate);

        api.setUriTemplates(uriTemplates);

        api.setEnvironments(environments);
        api.setUUID(UUID.randomUUID().toString());
        api.setThumbnailUrl(UUID.randomUUID().toString());
        api.setVisibility(UUID.randomUUID().toString());
        api.setVisibleRoles(UUID.randomUUID().toString());
        api.setVisibleTenants(UUID.randomUUID().toString());
        api.setTransports(UUID.randomUUID().toString());
        api.setInSequence(UUID.randomUUID().toString());
        api.setOutSequence(UUID.randomUUID().toString());
        api.setFaultSequence(UUID.randomUUID().toString());
        api.setDescription(UUID.randomUUID().toString());
        api.setRedirectURL(UUID.randomUUID().toString());
        api.setBusinessOwner(UUID.randomUUID().toString());
        api.setApiOwner(UUID.randomUUID().toString());
        api.setAdvertiseOnly(true);

        CORSConfiguration corsConfiguration = new CORSConfiguration(true, Arrays.asList("*"),
                true, Arrays.asList("*"), Arrays.asList("*"));

        api.setCorsConfiguration(corsConfiguration);
        api.setLastUpdated(new Date());
        api.setCreatedTime(new Date().toString());

        Set<Tier> tierSet = new HashSet<Tier>();
        tierSet.add(new Tier("Unlimited"));
        tierSet.add(new Tier("Gold"));
        api.addAvailableTiers(tierSet);
        Set<String> tags = new HashSet<String>();
        tags.add("stuff");
        api.addTags(tags);

        return api;
    }

    private Tag[] getTagsFromSet(Set<String> tagSet) {

        String[] tagNames = tagSet.toArray(new String[tagSet.size()]);

        Tag[] tags = new Tag[tagNames.length];

        for (int i = 0; i < tagNames.length; i++) {
            Tag tag = new Tag();
            tag.setTagName(tagNames[i]);
            tags[i] = tag;
        }

        return tags;
    }

    private HashMap<String, String> getURLTemplatePattern(Set<URITemplate> uriTemplates) {

        HashMap<String, String> pattern = new HashMap<String, String>();

        for (URITemplate uriTemplate : uriTemplates) {
            String key = uriTemplate.getUriTemplate() + "::" + uriTemplate.getHTTPVerb() + "::" +
                    uriTemplate.getAuthType() + "::" + uriTemplate.getThrottlingTier();
            pattern.put(key, uriTemplate.getHTTPVerb());
        }

        return pattern;
    }

    private ArrayList<URITemplate> getURLTemplateList(Set<URITemplate> uriTemplates) {

        ArrayList<URITemplate> list = new ArrayList<URITemplate>();
        list.addAll(uriTemplates);

        return list;

    }

    @Test
    public void testWsdlDefinitionFilePath() {

        Assert.assertEquals(APIUtil.getWSDLDefinitionFilePath("test", "1.0.0", "publisher1")
                , APIConstants.API_ROOT_LOCATION + RegistryConstants.PATH_SEPARATOR +
                        "publisher1" + RegistryConstants.PATH_SEPARATOR +
                        "test" + RegistryConstants.PATH_SEPARATOR +
                        "1.0.0" + RegistryConstants.PATH_SEPARATOR + "publisher1" + "--" + "test" + "1.0.0" + ".wsdl");
    }

    @Test
    public void testGetOAuthConfigurationFromTenantRegistry() throws Exception {
        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            final int tenantId = -1234;
            final String property = "AuthorizationHeader";

            ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
            PowerMockito.mockStatic(ServiceReferenceHolder.class);
            Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
            File siteConfFile = new File(Thread.currentThread().getContextClassLoader().
                    getResource("tenant-conf.json").getFile());
            String tenantConfValue = FileUtils.readFileToString(siteConfFile);
            APIMConfigService apimConfigService = Mockito.mock(APIMConfigService.class);
            Mockito.when(serviceReferenceHolder.getApimConfigService()).thenReturn(apimConfigService);
            Mockito.when(apimConfigService.getTenantConfig(tenantDomain)).thenReturn(tenantConfValue);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(tenantConfValue);
            String authorizationHeader = (String) json.get(property);
            String authHeader = getOAuthConfigurationFromTenantRegistry(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME, property);
            Assert.assertEquals(authorizationHeader, authHeader);
        }finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    @Test
    public void testGetOAuthConfigurationFromAPIMConfig() throws Exception {

        String property = "AuthorizationHeader";
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        APIManagerConfigurationService apiManagerConfigurationService =
                Mockito.mock(APIManagerConfigurationService.class);
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                .thenReturn(apiManagerConfigurationService);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.OAUTH_CONFIGS + property))
                .thenReturn("APIM_AUTH");

        String authHeader = getOAuthConfigurationFromAPIMConfig(property);
        Assert.assertEquals("APIM_AUTH", authHeader);
    }

    @Test
    public void testGetGatewayEndpoint() throws Exception {

        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);

            Environment environment = new Environment();
            environment.setType("Production");
            environment.setName("Production");
            environment.setApiGatewayEndpoint("http://localhost:8280,https://localhost:8243");
            Map<String, Environment> environmentMap = new HashMap<String, Environment>();
            environmentMap.put("Production", environment);

            ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
            PowerMockito.mockStatic(ServiceReferenceHolder.class);
            APIManagerConfigurationService apiManagerConfigurationService =
                    Mockito.mock(APIManagerConfigurationService.class);
            APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
            Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
            Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                    .thenReturn(apiManagerConfigurationService);
            Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
            Mockito.when(apiManagerConfiguration.getApiGatewayEnvironments()).thenReturn(environmentMap);

            ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
            PowerMockito.mockStatic(ApiMgtDAO.class);
            Mockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
            Mockito.when(apiMgtDAO.getAllEnvironments(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME))
                    .thenReturn(new ArrayList<org.wso2.carbon.apimgt.api.model.Environment>());
            String gatewayEndpoint = APIUtil.getGatewayEndpoint("http,https", "Production",
                    "Production", "61416403c40f086ad2dc5eed");
            Assert.assertEquals("https://localhost:8243", gatewayEndpoint);
        } catch (APIManagementException ex) {
            Assert.assertTrue(ex.getMessage().contains("Failed to create API for :"));
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    @Test
    public void testGetConditionDtoListWithHavingIPSpecificConditionOnly() throws ParseException {

        String base64EncodedString = "W3siaXBzcGVjaWZpYyI6eyJzcGVjaWZpY0lwIjoxNjg0MzAwOTAsImludmVydCI6ZmFsc2V9fV0=";
        List<ConditionDto> conditionDtoList = APIUtil.extractConditionDto(base64EncodedString);
        Assert.assertEquals(conditionDtoList.size(), 1);
        ConditionDto conditionDto = conditionDtoList.get(0);
        Assert.assertNotNull(conditionDto.getIpCondition());
    }

    @Test
    public void testGetConditionDtoListWithHavingIPRangeConditionOnly() throws ParseException {

        String base64EncodedString =
                "W3siaXByYW5nZSI6eyJzdGFydGluZ0lwIjoxNjg0MzAwOTAsImVuZGluZ0lwIjoxNjg0MzAwOTEsImludmVydCI6dHJ1ZX19XQ==";
        List<ConditionDto> conditionDtoList = APIUtil.extractConditionDto(base64EncodedString);
        Assert.assertEquals(conditionDtoList.size(), 1);
        ConditionDto conditionDto = conditionDtoList.get(0);
        Assert.assertNotNull(conditionDto.getIpRangeCondition());
        Assert.assertTrue(conditionDto.getIpRangeCondition().isInvert());
    }

    @Test
    public void testGetConditionDtoListWithHavingHeaderConditionOnly() throws ParseException {

        String base64EncodedString = "W3siaGVhZGVyIjp7ImludmVydCI6ZmFsc2UsInZhbHVlcyI6eyJhYmMiOiJkZWYifX19XQo=";
        List<ConditionDto> conditionDtoList = APIUtil.extractConditionDto(base64EncodedString);
        Assert.assertEquals(conditionDtoList.size(), 1);
        ConditionDto conditionDto = conditionDtoList.get(0);
        Assert.assertNotNull(conditionDto.getHeaderConditions());
        Assert.assertEquals(conditionDto.getHeaderConditions().getValues().size(), 1);
    }

    @Test
    public void testGetConditionDtoListWithHavingJWTClaimConditionOnly() throws ParseException {

        String base64EncodedString = "W3siand0Y2xhaW1zIjp7ImludmVydCI6ZmFsc2UsInZhbHVlcyI6eyJhYmMiOiJkZWYifX19XQo=";
        List<ConditionDto> conditionDtoList = APIUtil.extractConditionDto(base64EncodedString);
        Assert.assertEquals(conditionDtoList.size(), 1);
        ConditionDto conditionDto = conditionDtoList.get(0);
        Assert.assertNotNull(conditionDto.getJwtClaimConditions());
        Assert.assertEquals(conditionDto.getJwtClaimConditions().getValues().size(), 1);
    }

    @Test
    public void testGetConditionDtoListWithHavingQueryParamConditionOnly() throws ParseException {

        String base64EncodedString =
                "W3sicXVlcnlwYXJhbWV0ZXJ0eXBlIjp7ImludmVydCI6ZmFsc2UsInZhbHVlcyI6eyJhYmMiOiJkZWYifX19XQo=";
        List<ConditionDto> conditionDtoList = APIUtil.extractConditionDto(base64EncodedString);
        Assert.assertEquals(conditionDtoList.size(), 1);
        ConditionDto conditionDto = conditionDtoList.get(0);
        Assert.assertNotNull(conditionDto.getQueryParameterConditions());
        Assert.assertEquals(conditionDto.getQueryParameterConditions().getValues().size(), 1);
    }

    @Test
    public void testGetConditionDtoListWithHavingMultipleConditionTypes() throws ParseException {

        String base64EncodedString =
                "W3siaXBzcGVjaWZpYyI6eyJzcGVjaWZpY0lwIjoxNzQzMjcxODksImludmVydCI6ZmFsc2V9LCJoZW" +
                        "FkZXIiOnsiaW52ZXJ0IjpmYWxzZSwidmFsdWVzIjp7ImFiYyI6ImRlZiJ9fSwiand0Y2xhaW1zIjp7ImludmVydCI6ZmFsc2UsI" +
                        "nZhbHVlcyI6eyJhYmMiOiJkZWYifX19XQo=";
        List<ConditionDto> conditionDtoList = APIUtil.extractConditionDto(base64EncodedString);
        Assert.assertEquals(conditionDtoList.size(), 1);
        ConditionDto conditionDto = conditionDtoList.get(0);
        Assert.assertNotNull(conditionDto.getIpCondition());
        Assert.assertNotNull(conditionDto.getHeaderConditions());
        Assert.assertNotNull(conditionDto.getJwtClaimConditions());
        Assert.assertEquals(conditionDto.getHeaderConditions().getValues().size(), 1);
        Assert.assertEquals(conditionDto.getJwtClaimConditions().getValues().size(), 1);
    }

    @Test
    public void testGetConditionDtoListWithHavingMultiplePipelines() throws ParseException {

        String base64EncodedString =
                "W3siaGVhZGVyIjp7ImludmVydCI6ZmFsc2UsInZhbHVlcyI6eyJhYmMiOiJkZWYifX19LHsiaXBzcG" +
                        "VjaWZpYyI6eyJzcGVjaWZpY0lwIjoxNjg0MzAwOTAsImludmVydCI6ZmFsc2V9fV0=";
        List<ConditionDto> conditionDtoList = APIUtil.extractConditionDto(base64EncodedString);
        Assert.assertEquals(conditionDtoList.size(), 2);
        Assert.assertNotNull(conditionDtoList.get(0).getIpCondition());
        Assert.assertNotNull(conditionDtoList.get(1).getHeaderConditions());

    }

    @Test
    public void testGetConditionDtoListWithHavingMultiplePipelinesWithVariousConditions() throws ParseException {

        String base64EncodedString =
                "W3siaGVhZGVyIjp7ImludmVydCI6ZmFsc2UsInZhbHVlcyI6eyJhYmMiOiJkZWYifX19LHsiaXBzcGV" +
                        "jaWZpYyI6eyJzcGVjaWZpY0lwIjoxNzQzMjcxODksImludmVydCI6ZmFsc2V9LCJoZWFkZXIiOnsiaW52ZXJ0IjpmYWxzZSwidmF" +
                        "sdWVzIjp7ImFiYyI6ImRlZiJ9fX0seyJqd3RjbGFpbXMiOnsiaW52ZXJ0IjpmYWxzZSwidmFsdWVzIjp7ImFiYyI6ImRlZiJ9fX0" +
                        "seyJpcHNwZWNpZmljIjp7InNwZWNpZmljSXAiOjE3NDMyNzE4OSwiaW52ZXJ0IjpmYWxzZX0sImp3dGNsYWltcyI6eyJpbnZlcnQ" +
                        "iOmZhbHNlLCJ2YWx1ZXMiOnsiYWJjIjoiZGVmIn19fSx7ImlwcmFuZ2UiOnsic3RhcnRpbmdJcCI6MTc0MzI3MTg5LCJlbmRpbmd" +
                        "JcCI6MTc0MzI3MjAwLCJpbnZlcnQiOmZhbHNlfX1d";
        List<ConditionDto> conditionDtoList = APIUtil.extractConditionDto(base64EncodedString);
        Assert.assertEquals(conditionDtoList.size(), 5);
        Assert.assertNotNull(conditionDtoList.get(0).getIpCondition());
        Assert.assertNotNull(conditionDtoList.get(0).getHeaderConditions());
        Assert.assertNotNull(conditionDtoList.get(1).getIpCondition());
        Assert.assertNotNull(conditionDtoList.get(1).getJwtClaimConditions());
        Assert.assertNotNull(conditionDtoList.get(2).getIpRangeCondition());
        Assert.assertNotNull(conditionDtoList.get(3).getHeaderConditions());
        Assert.assertNotNull(conditionDtoList.get(4).getJwtClaimConditions());
    }

    @Test
    public void testGetAppAttributeKeysFromRegistry() throws Exception {

        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            final int tenantId = -1234;
            final String property = APIConstants.ApplicationAttributes.APPLICATION_CONFIGURATIONS;

            ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
            PowerMockito.mockStatic(ServiceReferenceHolder.class);
            RegistryService registryService = Mockito.mock(RegistryService.class);
            Resource resource = Mockito.mock(Resource.class);
            Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
            File siteConfFile = new File(Thread.currentThread().getContextClassLoader().
                    getResource("tenant-conf.json").getFile());
            String tenantConfValue = FileUtils.readFileToString(siteConfFile);
            APIMConfigService apimConfigService = Mockito.mock(APIMConfigService.class);
            Mockito.when(serviceReferenceHolder.getApimConfigService()).thenReturn(apimConfigService);
            Mockito.when(apimConfigService.getTenantConfig(tenantDomain)).thenReturn(tenantConfValue);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(tenantConfValue);
            JSONObject applicationAttributes = (JSONObject) json.get(property);
            JSONObject appAttributes = APIUtil.getAppAttributeKeysFromRegistry(tenantDomain);
            Assert.assertEquals(applicationAttributes, appAttributes);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    @Test
    public void testSanitizeUserRole() throws Exception {

        Assert.assertEquals("Test%26123", APIUtil.sanitizeUserRole("Test&123"));
        Assert.assertEquals("Test%26123%26test", APIUtil.sanitizeUserRole("Test&123&test"));
        Assert.assertEquals("Test123", APIUtil.sanitizeUserRole("Test123"));
        Assert.assertEquals("Role%20A", APIUtil.sanitizeUserRole("Role A"));
    }

    @Test
    public void testIsRoleExistForUser() throws Exception {
        /*
        String userName = "user1";
        String[] userRoleList = {"role1", "role2"};
        PowerMockito.mockStatic(APIUtil.class);
        Mockito.when(APIUtil.getListOfRoles(userName)).thenReturn(userRoleList);
        Assert.assertFalse(APIUtil.isRoleExistForUser(userName, "roleA,roleB"));
        Assert.assertTrue(APIUtil.isRoleExistForUser(userName, "role1,roleB"));
        //Assert.assertTrue(APIUtil.isRoleExistForUser(userName, "role1,role2"));
        Assert.assertFalse(APIUtil.isRoleExistForUser(userName, ""));
        Assert.assertFalse(APIUtil.isRoleExistForUser(userName, null));
        Assert.assertFalse(APIUtil.isRoleExistForUser(userName, "test"));
        */
    }

    @Test
    public void testHasPermissionWhenPermissionCheckDisabled() throws APIManagementException {

        String userNameWithoutChange = "Drake";
        String permission = APIConstants.Permissions.API_PUBLISH;

        Log mockLog = Mockito.mock(Log.class);
        PowerMockito.mockStatic(LogFactory.class);
        Mockito.when(LogFactory.getLog(any(Class.class))).thenReturn(mockLog);
        PowerMockito.stub(PowerMockito.method(APIUtil.class, "isPermissionCheckDisabled")).toReturn(true);

        boolean actualResult = APIUtil.hasPermission(userNameWithoutChange, permission);
        Assert.assertTrue(actualResult);
    }

    @Test
    public void testHasPermissionWithUserNameNull() {

        String userNameWithoutChange = null;
        String permission = APIConstants.Permissions.APIM_ADMIN;
        String expectedExceptionMessage = "Attempt to execute privileged operation as the anonymous user";
        String actualErrorMessage = null;

        try {
            APIUtil.hasPermission(userNameWithoutChange, permission);
        } catch (APIManagementException exception) {
            actualErrorMessage = exception.getMessage();
        }
        Assert.assertEquals(expectedExceptionMessage, actualErrorMessage);
    }

    @Test
    public void testHasPermission() throws Exception {

        int tenantId = 2;
        String userNameWithoutChange = "Drake";
        String permission = APIConstants.Permissions.API_PUBLISH;
        System.setProperty(CARBON_HOME, "");

        PowerMockito.spy(APIUtil.class);
        PowerMockito.doReturn(false).when(APIUtil.class, "isPermissionCheckDisabled");
        PowerMockito.doReturn(1)
                .when(APIUtil.class, "getValueFromCache", APIConstants.API_PUBLISHER_ADMIN_PERMISSION_CACHE,
                        userNameWithoutChange);

        PowerMockito.mockStatic(MultitenantUtils.class);
        Mockito.when(MultitenantUtils.getTenantDomain(userNameWithoutChange)).thenReturn(tenantDomain);

        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.mockStatic(CarbonContext.class);
        CarbonContext carbonContext = Mockito.mock(CarbonContext.class);
        PowerMockito.when(CarbonContext.getThreadLocalCarbonContext()).thenReturn(carbonContext);
        Mockito.when(carbonContext.getTenantDomain()).thenReturn(org.wso2.carbon.base.MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        Mockito.when(carbonContext.getTenantId()).thenReturn(org.wso2.carbon.base.MultitenantConstants.SUPER_TENANT_ID);
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        Mockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);

        RealmService realmService = Mockito.mock(RealmService.class);
        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);

        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);

        Mockito.when(tenantManager.getTenantId(tenantDomain)).thenReturn(tenantId);

        UserRealm userRealm = Mockito.mock(UserRealm.class);
        Mockito.when(realmService.getTenantUserRealm(tenantId)).thenReturn(userRealm);

        org.wso2.carbon.user.api.AuthorizationManager authorizationManager = Mockito
                .mock(org.wso2.carbon.user.api.AuthorizationManager.class);
        Mockito.when(userRealm.getAuthorizationManager()).thenReturn(authorizationManager);

        Mockito.when(MultitenantUtils.getTenantAwareUsername(userNameWithoutChange)).thenReturn(userNameWithoutChange);
        Mockito.when(
                        authorizationManager.isUserAuthorized(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);

        Log logMock = Mockito.mock(Log.class);
        PowerMockito.mockStatic(LogFactory.class);
        Mockito.when(LogFactory.getLog(any(Class.class))).thenReturn(logMock);
        PowerMockito.mockStatic(IdentityUtil.class);
        PowerMockito.doReturn(true).when(IdentityUtil.class, "isUserStoreInUsernameCaseSensitive", userNameWithoutChange);
        boolean expectedResult = APIUtil.hasPermission(userNameWithoutChange, permission);
        Assert.assertEquals(true, expectedResult);
    }

    @Test
    public void testGetORBasedSearchCriteria() {

        String[] statusList = {"PUBLISHED", "PROTOTYPED", "DEPRECATED"};
        String expectedCriteria = "(PUBLISHED OR PROTOTYPED OR DEPRECATED)";

        Assert.assertEquals(expectedCriteria, APIUtil.getORBasedSearchCriteria(statusList));
    }

    @Test
    public void testGetORBasedSearchCriteriaWithEmptyCriteria() {

        String[] statusList = null;
        Assert.assertEquals(null, APIUtil.getORBasedSearchCriteria(statusList));
    }

    @Test
    public void testisAllowDisplayAPIsWithMultipleStatus() {

        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);

        APIManagerConfigurationService apiManagerConfigurationService = Mockito
                .mock(APIManagerConfigurationService.class);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                .thenReturn(apiManagerConfigurationService);

        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_STORE_DISPLAY_ALL_APIS))
                .thenReturn("true");
        Assert.assertEquals(true, APIUtil.isAllowDisplayAPIsWithMultipleStatus());
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_STORE_DISPLAY_ALL_APIS))
                .thenReturn("false");
        Assert.assertEquals(false, APIUtil.isAllowDisplayAPIsWithMultipleStatus());
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_STORE_DISPLAY_ALL_APIS)).thenReturn("");
        Assert.assertEquals(false, APIUtil.isAllowDisplayAPIsWithMultipleStatus());
        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_STORE_DISPLAY_ALL_APIS))
                .thenReturn(null);
        Assert.assertEquals(false, APIUtil.isAllowDisplayAPIsWithMultipleStatus());
    }

    @Test
    public void testGetTiersWhenTierTypeIsAPI() throws Exception {

        System.setProperty(CARBON_HOME, "");
        int tierType = APIConstants.TIER_API_TYPE;
        int tenantID = 1;
        Tier tier1 = Mockito.mock(Tier.class);
        Map<String, Tier> tierMap = new TreeMap<String, Tier>();
        tierMap.put("UNLIMITED", tier1);

        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.doNothing().when(PrivilegedCarbonContext.class, "startTenantFlow");
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        Mockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        doNothing().when(privilegedCarbonContext).setTenantDomain(tenantDomain, true);
        Mockito.when(privilegedCarbonContext.getTenantId()).thenReturn(tenantID);
        PowerMockito.spy(APIUtil.class);
        PowerMockito.doReturn(tenantID).when(APIUtil.class, "getInternalOrganizationId", tenantDomain);
        PowerMockito.doReturn(tierMap)
                .when(APIUtil.class, "getTiersFromPolicies", PolicyConstants.POLICY_LEVEL_SUB, tenantID);
        Map<String, Tier> appTierMap = APIUtil.getTiers(tierType, tenantDomain);
        Assert.assertEquals(tierMap, appTierMap);
    }

    @Test
    public void testGetTiersWhenTierTypeIsResource() throws Exception {

        System.setProperty(CARBON_HOME, "");
        int tierType = APIConstants.TIER_RESOURCE_TYPE;
        int tenantID = 1;
        Tier tier1 = Mockito.mock(Tier.class);
        Map<String, Tier> tierMap = new TreeMap<String, Tier>();
        tierMap.put("UNLIMITED", tier1);

        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.doNothing().when(PrivilegedCarbonContext.class, "startTenantFlow");
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        Mockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        doNothing().when(privilegedCarbonContext).setTenantDomain(tenantDomain, true);
        Mockito.when(privilegedCarbonContext.getTenantId()).thenReturn(tenantID);
        PowerMockito.spy(APIUtil.class);
        PowerMockito.doReturn(tenantID).when(APIUtil.class, "getInternalOrganizationId", tenantDomain);
        PowerMockito.doReturn(tierMap)
                .when(APIUtil.class, "getTiersFromPolicies", PolicyConstants.POLICY_LEVEL_API, tenantID);
        Map<String, Tier> appTierMap = APIUtil.getTiers(tierType, tenantDomain);

        Assert.assertEquals(tierMap, appTierMap);
    }

    @Test
    public void testGetTiersWhenTierTypeIsApplication() throws Exception {

        System.setProperty(CARBON_HOME, "");
        int tierType = APIConstants.TIER_APPLICATION_TYPE;
        int tenantID = 1;
        Tier tier1 = Mockito.mock(Tier.class);
        Map<String, Tier> tierMap = new TreeMap<String, Tier>();
        tierMap.put("UNLIMITED", tier1);

        PowerMockito.mockStatic(PrivilegedCarbonContext.class);
        PowerMockito.doNothing().when(PrivilegedCarbonContext.class, "startTenantFlow");
        PrivilegedCarbonContext privilegedCarbonContext = Mockito.mock(PrivilegedCarbonContext.class);
        Mockito.when(PrivilegedCarbonContext.getThreadLocalCarbonContext()).thenReturn(privilegedCarbonContext);
        doNothing().when(privilegedCarbonContext).setTenantDomain(tenantDomain, true);
        Mockito.when(privilegedCarbonContext.getTenantId()).thenReturn(tenantID);
        PowerMockito.spy(APIUtil.class);
        PowerMockito.doReturn(tenantID).when(APIUtil.class, "getInternalOrganizationId", tenantDomain);
        PowerMockito.doReturn(tierMap)
                .when(APIUtil.class, "getTiersFromPolicies", PolicyConstants.POLICY_LEVEL_APP, tenantID);
        Map<String, Tier> appTierMap = APIUtil.getTiers(tierType, tenantDomain);

        Assert.assertEquals(tierMap, appTierMap);
    }

    @Test
    public void testValidateAPICategoriesWithValidCategories() throws Exception {

        List<APICategory> inputApiCategories = new ArrayList<>();
        List<APICategory> availableApiCategories = new ArrayList<>();
        APICategory apiCategory1 = Mockito.mock(APICategory.class);
        APICategory apiCategory2 = Mockito.mock(APICategory.class);
        ;
        APICategory apiCategory3 = Mockito.mock(APICategory.class);

        inputApiCategories.add(apiCategory1);
        inputApiCategories.add(apiCategory2);
        availableApiCategories.add(apiCategory1);
        availableApiCategories.add(apiCategory2);
        availableApiCategories.add(apiCategory3);
        PowerMockito.spy(APIUtil.class);
        PowerMockito.doReturn(availableApiCategories).when(APIUtil.class, "getAllAPICategoriesOfOrganization", tenantDomain);

        Assert.assertTrue("Failed to Validate API categories",
                APIUtil.validateAPICategories(inputApiCategories, tenantDomain));
    }

    @Test
    public void testValidateAPICategoriesWithInvalidCategories() throws APIManagementException {

        List<APICategory> inputApiCategories = new ArrayList<>();
        List<APICategory> availableApiCategories = new ArrayList<>();
        APICategory apiCategory1 = Mockito.mock(APICategory.class);
        APICategory apiCategory2 = Mockito.mock(APICategory.class);
        ;
        APICategory apiCategory3 = Mockito.mock(APICategory.class);

        inputApiCategories.add(apiCategory1);
        inputApiCategories.add(apiCategory2);
        inputApiCategories.add(apiCategory3);
        availableApiCategories.add(apiCategory1);
        availableApiCategories.add(apiCategory2);
        PowerMockito.mockStatic(APIUtil.class);
        Mockito.when(APIUtil.getAllAPICategoriesOfOrganization(tenantDomain)).thenReturn(availableApiCategories);
        Mockito.when(APIUtil.validateAPICategories(inputApiCategories, tenantDomain)).thenCallRealMethod();

        Assert.assertFalse("Invalid API categories are validate!!!",
                APIUtil.validateAPICategories(inputApiCategories, tenantDomain));
    }

    @Test
    public void testGetListOfRoles() throws Exception {

        String username = "Kelso";
        String[] roles = {"PUBLISHER", "ADMIN", "TEST-ROLE"};
        PowerMockito.mockStatic(IdentityUtil.class);
        PowerMockito.doReturn(true).when(IdentityUtil.class, "isUserStoreInUsernameCaseSensitive", username);
        PowerMockito.spy(APIUtil.class);
        PowerMockito.doReturn(roles)
                .when(APIUtil.class, "getValueFromCache", APIConstants.API_USER_ROLE_CACHE, username);

        Assert.assertEquals(roles, APIUtil.getListOfRoles(username));
    }

    @Test
    public void testGetListOfRolesNonSuperTenant() throws Exception {

        int tenantID = 1;
        String username = "Kelso";
        String[] roles = {"PUBLISHER", "ADMIN", "TEST-ROLE"};
        String tenantDomain = "Insta.com";
        String tenantAwareUsername = "Insta_User";

        PowerMockito.spy(APIUtil.class);
        PowerMockito.doReturn(null)
                .when(APIUtil.class, "getValueFromCache", APIConstants.API_USER_ROLE_CACHE, username);

        PowerMockito.mockStatic(MultitenantUtils.class);
        Mockito.when(MultitenantUtils.getTenantDomain(username)).thenReturn(tenantDomain);
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        RealmService realmService = Mockito.mock(RealmService.class);
        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(tenantManager.getTenantId(tenantDomain)).thenReturn(tenantID);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        Mockito.when(realmService.getTenantUserRealm(tenantID)).thenReturn(userRealm);
        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        Mockito.when(MultitenantUtils.getTenantAwareUsername(username)).thenReturn(tenantAwareUsername);
        Mockito.when(userStoreManager.getRoleListOfUser(tenantAwareUsername)).thenReturn(roles);
        PowerMockito.mockStatic(IdentityUtil.class);
        PowerMockito.doReturn(true).when(IdentityUtil.class, "isUserStoreInUsernameCaseSensitive", username);
        PowerMockito.doNothing().when(APIUtil.class, "addToRolesCache", Mockito.any(), Mockito.any(), Mockito.any());

        Assert.assertEquals(roles, APIUtil.getListOfRoles(username));
    }

    @Test
    public void testGetListOfRolesSuperTenant() throws Exception {

        String username = "Kelso";
        String[] roles = {"PUBLISHER", "ADMIN", "TEST-ROLE"};
        String tenantDomain = "carbon.super";
        String tenantAwareUsername = "Insta_User";
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        RealmService realmService  = Mockito.mock(RealmService.class);
        Mockito.when(serviceReferenceHolder.getRealmService()).thenReturn(realmService);
        TenantManager tenantManager = Mockito.mock(TenantManager.class);
        Mockito.when(realmService.getTenantManager()).thenReturn(tenantManager);
        Mockito.when(tenantManager.getTenantId(tenantDomain)).thenReturn(-1234);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        Mockito.when(realmService.getTenantUserRealm(-1234)).thenReturn(userRealm);
        UserStoreManager userStoreManager = Mockito.mock(UserStoreManager.class);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        PowerMockito.spy(APIUtil.class);
        PowerMockito.doReturn(null)
                .when(APIUtil.class, "getValueFromCache", APIConstants.API_USER_ROLE_CACHE, username);
        PowerMockito.mockStatic(MultitenantUtils.class);
        Mockito.when(MultitenantUtils.getTenantDomain(username)).thenReturn(tenantDomain);
        Mockito.when(MultitenantUtils.getTenantAwareUsername(username)).thenReturn(tenantAwareUsername);
        Mockito.when(userStoreManager.getRoleListOfUser(MultitenantUtils.getTenantAwareUsername(username))).thenReturn(roles);
        PowerMockito.mockStatic(IdentityUtil.class);
        PowerMockito.doReturn(true).when(IdentityUtil.class, "isUserStoreInUsernameCaseSensitive", username);
        PowerMockito.doNothing().when(APIUtil.class, "addToRolesCache", Mockito.any(), Mockito.any(), Mockito.any());

        Assert.assertEquals(roles, APIUtil.getListOfRoles(username));
    }

    @Test
    public void testGetListOfRolesWithNullUsername() {

        String username = null;
        APIManagementException exception = null;
        try {
            APIUtil.getListOfRoles(username);
        } catch (APIManagementException ex) {
            exception = ex;
        }
        Assert.assertEquals("Attempt to execute privileged operation as the anonymous user", exception.getMessage());
    }

    @Test
    public void testCompareRoleList() {

        String accessControlRole = "creator";
        String[] roles = {"PUBLISHER", "ADMIN", "TEST-ROLE", "CREATOR"};
        Assert.assertEquals(true, APIUtil.compareRoleList(roles, accessControlRole));
    }

    @Test
    public void testCompareRoleListWithNewRole() {

        String accessControlRole = "Non-creator";
        String[] roles = {"PUBLISHER", "ADMIN", "TEST-ROLE", "CREATOR"};
        Assert.assertEquals(false, APIUtil.compareRoleList(roles, accessControlRole));
    }

    @Test
    public void testCompareRoleListWithNull() {

        String accessControlRole = "Non-creator";
        String[] roles = null;
        Assert.assertEquals(false, APIUtil.compareRoleList(roles, accessControlRole));
    }

    @Test
    public void testConstructApisGetQuery() throws APIManagementException {

        String searchQuery = "status:PUBLISHED";
        String expectedQuery =
                "status=*published*&type=(HTTP OR WS OR SOAPTOREST OR GRAPHQL OR SOAP OR WEBSUB OR SSE OR ASYNC)";
        Assert.assertEquals(expectedQuery, APIUtil.constructApisGetQuery(searchQuery));
    }

    @Test
    public void testConstructApisGetQuery2() throws APIManagementException {

        String searchQuery = "status PUBLISHED";
        String expectedQuery =
                "name=*status*&name=*PUBLISHED*&type=(HTTP OR WS OR SOAPTOREST OR GRAPHQL OR SOAP OR WEBSUB OR SSE " +
                        "OR ASYNC)";
        Assert.assertEquals(expectedQuery, APIUtil.constructApisGetQuery(searchQuery));
    }

    @Test
    public void testConstructApisGetQuery3() throws APIManagementException {

        String searchQuery = "status:PUBLISHED provider:wso2";
        String expectedQuery = "status=*published*&provider=*wso2*&type=(HTTP OR WS OR SOAPTOREST OR GRAPHQL OR SOAP " +
                "OR WEBSUB OR SSE OR ASYNC)";
        Assert.assertEquals(expectedQuery, APIUtil.constructApisGetQuery(searchQuery));
    }

    @Test
    public void testConstructApisGetQuery4() {

        String searchQuery = "status:PUBLISHED doc:wso2";
        String expectedError = "Invalid query. AND based search is not supported for doc prefix";
        APIManagementException exception = null;
        try {
            APIUtil.constructApisGetQuery(searchQuery);
        } catch (APIManagementException ex) {
            exception = ex;
        }
        Assert.assertEquals(expectedError, exception.getMessage());
    }

    @Test
    public void testGetTokenEndpointsByType() throws Exception {

        System.setProperty("carbon.home", APIUtilTest.class.getResource("/").getFile());
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext.getThreadLocalCarbonContext().setTenantDomain(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            Environment environment = new Environment();
            environment.setType("production");
            environment.setName("Production");
            environment.setDefault(true);
            environment.setApiGatewayEndpoint("http://localhost:8280,https://localhost:8243");
            Map<String, Environment> environmentMap = new HashMap<String, Environment>();
            environmentMap.put("Production", environment);

            ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
            PowerMockito.mockStatic(ServiceReferenceHolder.class);
            APIManagerConfigurationService apiManagerConfigurationService =
                    Mockito.mock(APIManagerConfigurationService.class);
            APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
            Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
            Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                    .thenReturn(apiManagerConfigurationService);
            Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);
            Mockito.when(apiManagerConfiguration.getApiGatewayEnvironments()).thenReturn(environmentMap);

            ApiMgtDAO apiMgtDAO = Mockito.mock(ApiMgtDAO.class);
            PowerMockito.mockStatic(ApiMgtDAO.class);
            Mockito.when(ApiMgtDAO.getInstance()).thenReturn(apiMgtDAO);
            Mockito.when(apiMgtDAO.getAllEnvironments(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME))
                    .thenReturn(new ArrayList<org.wso2.carbon.apimgt.api.model.Environment>());
            String tokenEndpointType = APIUtil.getTokenEndpointsByType("production", "61416403c40f086ad2dc5eef");
            Assert.assertEquals("https://localhost:8243", tokenEndpointType);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }
    
    @Test
    public void testSupportedDefaultFileTypes() throws Exception {
        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        APIManagerConfigurationService apiManagerConfigurationService = Mockito
                .mock(APIManagerConfigurationService.class);
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                .thenReturn(apiManagerConfigurationService);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);

        // Test valid types
        String fileName = "test1.pdf";
        Assert.assertTrue("PDF file type validation failed", APIUtil.isSupportedFileType(fileName));

        fileName = "test1.xls";
        Assert.assertTrue("Excel file type (xls) validation failed", APIUtil.isSupportedFileType(fileName));

        fileName = "test1.xlsx";
        Assert.assertTrue("Excel file type (xlsx)  validation failed", APIUtil.isSupportedFileType(fileName));

        // test invalid types
        fileName = "test1.js";
        Assert.assertFalse("JS file type should not be allowed", APIUtil.isSupportedFileType(fileName));

        fileName = "test1";
        Assert.assertFalse("File without a type should not be allowed", APIUtil.isSupportedFileType(fileName));

    }

    @Test
    public void testSupportedFileTypesByConfig() throws Exception {

        PowerMockito.mockStatic(ServiceReferenceHolder.class);
        ServiceReferenceHolder serviceReferenceHolder = Mockito.mock(ServiceReferenceHolder.class);
        Mockito.when(ServiceReferenceHolder.getInstance()).thenReturn(serviceReferenceHolder);
        APIManagerConfigurationService apiManagerConfigurationService = Mockito
                .mock(APIManagerConfigurationService.class);
        APIManagerConfiguration apiManagerConfiguration = Mockito.mock(APIManagerConfiguration.class);
        Mockito.when(serviceReferenceHolder.getAPIManagerConfigurationService())
                .thenReturn(apiManagerConfigurationService);
        Mockito.when(apiManagerConfigurationService.getAPIManagerConfiguration()).thenReturn(apiManagerConfiguration);

        Mockito.when(apiManagerConfiguration.getFirstProperty(APIConstants.API_PUBLISHER_SUPPORTED_DOC_TYPES))
                .thenReturn("js,java");

        // Test valid types
        String fileName = "test1.js";
        Assert.assertTrue("JS file type validation failed", APIUtil.isSupportedFileType(fileName));

        // test invalid types
        fileName = "test1.pdf";
        Assert.assertFalse("PDF type should not be allowed", APIUtil.isSupportedFileType(fileName));
    }
}
