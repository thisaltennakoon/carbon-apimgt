/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
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

package org.wso2.carbon.apimgt.notification.event;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract Event that have common attributes.
 */
public abstract class Event {
    protected String eventId;
    protected long timeStamp;
    protected String type;
    protected int tenantId;
    protected String tenantDomain;
    private Map<String, String> properties = new HashMap<String, String>();

    public String getEventId() {

        return eventId;
    }

    public void setEventId(String eventId) {

        this.eventId = eventId;
    }

    public long getTimeStamp() {

        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {

        this.timeStamp = timeStamp;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public int getTenantId() {

        return tenantId;
    }

    public void setTenantId(int tenantId) {

        this.tenantId = tenantId;
    }

    public String getTenantDomain() {

        return tenantDomain;
    }

    public void setTenantDomain(String tenantDomain) {

        this.tenantDomain = tenantDomain;
    }

    public Map<String, String> getProperties() {

        return properties;
    }

    public void setProperties(Map<String, String> properties) {

        this.properties = properties;
    }
}
