/*
 * Copyright (c) 2021 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
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
package org.wso2.carbon.apimgt.gateway.mediators.webhooks;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.wso2.carbon.apimgt.gateway.exception.DataNotFoundException;
import org.wso2.carbon.apimgt.gateway.handlers.analytics.Constants;
import org.wso2.carbon.apimgt.gateway.utils.WebhooksUtils;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dto.WebhooksDTO;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * This mediator would load the subscriber's list from the in-memory map of the tenant.
 */
public class SubscribersLoader extends AbstractMediator {

    @Override
    public boolean mediate(MessageContext messageContext) {
        messageContext.setProperty(Constants.REQUEST_START_TIME_PROPERTY, System.currentTimeMillis());
        //Set user agent in request flow
        String userAgent = getUserAgent(messageContext);
        messageContext.setProperty(Constants.USER_AGENT_PROPERTY, userAgent);
        try {
            List<WebhooksDTO> subscribers = WebhooksUtils.getSubscribersListFromInMemoryMap(messageContext);
            messageContext.setProperty(APIConstants.Webhooks.SUBSCRIBERS_LIST_PROPERTY, subscribers);
            if (subscribers != null) {
                messageContext.setProperty(APIConstants.Webhooks.SUBSCRIBERS_COUNT_PROPERTY, subscribers.size());
            } else {
                messageContext.setProperty(APIConstants.Webhooks.SUBSCRIBERS_COUNT_PROPERTY, 0);
            }
        } catch (URISyntaxException | DataNotFoundException e) {
            handleException("Error while getting subscribers count", e, messageContext);
        }
        return true;

    }

    private String getUserAgent(MessageContext messageContext) {
        Map<?, ?> headers = (Map<?, ?>) ((Axis2MessageContext) messageContext).getAxis2MessageContext()
                .getProperty(org.apache.axis2.context.MessageContext.TRANSPORT_HEADERS);
        return (String) headers.get(APIConstants.USER_AGENT);
    }


}
