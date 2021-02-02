package org.wso2.carbon.apimgt.rest.api.publisher.v1.impl;

import org.wso2.carbon.apimgt.rest.api.publisher.v1.*;
import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.*;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.rest.api.publisher.v1.dto.SamplesDTO;

import java.util.List;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;


public class SampleApiServiceImpl implements SampleApiService {

    public Response getSamples(MessageContext messageContext) {
        SamplesDTO samplesDTO = new SamplesDTO();
        samplesDTO.setSample1("Sample 1");
        samplesDTO.setSample2("Sample 2");
        return Response.ok().entity(samplesDTO).build();
    }
}
