package org.wso2.carbon.apimgt.rest.api.publisher.v1;

import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.SamplesDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.SampleApiService;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.impl.SampleApiServiceImpl;
import org.wso2.carbon.apimgt.api.APIManagementException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.inject.Inject;

import io.swagger.annotations.*;
import java.io.InputStream;

import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import java.util.Map;
import java.util.List;
import javax.validation.constraints.*;
@Path("/sample")

@Api(description = "the sample API")




public class SampleApi  {

  @Context MessageContext securityContext;

SampleApiService delegate = new SampleApiServiceImpl();


    @GET
    
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Retrieve sample items ", notes = "Retrieve sample items ", response = SamplesDTO.class, responseContainer = "List", authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:api_view", description = "View API")
        })
    }, tags={ "Sample" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. Sample Items returned. ", response = SamplesDTO.class, responseContainer = "List") })
    public Response getSamples() throws APIManagementException{
        return delegate.getSamples(securityContext);
    }
}
