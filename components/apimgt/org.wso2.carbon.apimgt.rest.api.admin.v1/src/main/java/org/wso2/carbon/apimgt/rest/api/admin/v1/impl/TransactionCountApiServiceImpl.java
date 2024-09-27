/*
 *  Copyright (c) 2024, WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.rest.api.admin.v1.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.impl.dao.TransactionCountDAO;
import org.wso2.carbon.apimgt.impl.dto.TransactionCountDTO;
import org.wso2.carbon.apimgt.rest.api.admin.v1.*;
import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.*;

import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.MessageContext;

import org.wso2.carbon.apimgt.rest.api.admin.v1.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class TransactionCountApiServiceImpl implements TransactionCountApiService {

    private static final Log log = LogFactory.getLog(TransactionCountApiServiceImpl.class);

    public Response transactionCountGet(String startTime, String endTime, MessageContext messageContext) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
            // Convert start and end times to the start and end of the respective days
            String startTimeTimestamp = Timestamp.from(
                    Instant.ofEpochSecond(Long.parseLong(startTime)).atZone(zoneId).toLocalDate().atStartOfDay(zoneId)
                            .toInstant()).toString();
            String endTimeTimestamp = Timestamp.from(
                    Instant.ofEpochSecond(Long.parseLong(endTime)).atZone(zoneId).toLocalDate().atTime(LocalTime.MAX)
                            .atZone(zoneId).toInstant()).toString();
            TransactionCountDAO transactionCountDAO = TransactionCountDAO.getInstance();
            TransactionCountDTO transactionCountDTO = transactionCountDAO.getTransactionCount(startTimeTimestamp,
                    endTimeTimestamp);

            return Response.ok().entity(transactionCountDTO).build();
        } catch (APIManagementException e) {
            String errorMessage = "Error while retrieving transaction count.";
            RestApiUtil.handleInternalServerError(errorMessage, e, log);
        }
        return null;
    }
}
