package org.wso2.carbon.apimgt.rest.api.publisher.v1;

import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.LabelListDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.LabelsApiService;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.impl.LabelsApiServiceImpl;
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
@Path("/labels")

@Api(description = "the labels API")




public class LabelsApi  {

  @Context MessageContext securityContext;

LabelsApiService delegate = new LabelsApiServiceImpl();


    @GET
    
    
    @Produces({ "application/json" })
    @ApiOperation(value = "Get all Labels", notes = "Get all Labels ", response = LabelListDTO.class, authorizations = {
        @Authorization(value = "OAuth2Security", scopes = {
            @AuthorizationScope(scope = "apim:api_view", description = "View API"),
            @AuthorizationScope(scope = "apim:api_manage", description = "Manage all API related operations")
        })
    }, tags={ "Labels (Collection)" })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "OK. Labels returned ", response = LabelListDTO.class) })
    public Response getAllLabels() throws APIManagementException{
        return delegate.getAllLabels(securityContext);
    }
}
