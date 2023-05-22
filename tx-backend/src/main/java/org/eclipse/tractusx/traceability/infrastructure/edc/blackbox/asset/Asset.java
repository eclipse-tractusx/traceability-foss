/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.asset;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


/**
 * The {@link Asset} contains the metadata and describes the data itself or a collection of data.
 */
@Slf4j
@ToString
@JsonDeserialize(builder = Asset.Builder.class)
public class Asset {

    private static final String ASSET_VALUE_NOTIFICATION_METHOD_UPDATE = "update";
    private static final String ASSET_VALUE_NOTIFICATION_METHOD_RECEIVE = "receive";
    public static final String ASSET_KEY_NOTIFICATION_TYPE = "asset:prop:notificationtype";
    public static final String ASSET_VALUE_QUALITY_INVESTIGATION = "qualityinvestigation";
    public static final String PROPERTY_ID = "asset:prop:id";
    public static final String PROPERTY_NAME = "asset:prop:name";
    public static final String PROPERTY_DESCRIPTION = "asset:prop:description";
    public static final String PROPERTY_VERSION = "asset:prop:version";
    public static final String PROPERTY_CONTENT_TYPE = "asset:prop:contenttype";
    public static final String PROPERTY_NOTIFICATION_METHOD = "asset:prop:notificationmethod";
    public static final String PROPERTY_NOTIFICATION_TYPE = "asset:prop:notificationtype";

    private Map<String, Object> properties;

    protected Asset() {
        properties = new HashMap<>();
    }

    @JsonIgnore
    public String getId() {
        return getPropertyAsString(PROPERTY_ID);
    }

    @JsonIgnore
    public String getName() {
        return getPropertyAsString(PROPERTY_NAME);
    }

    @JsonIgnore
    public String getDescription() {
        return getPropertyAsString(PROPERTY_DESCRIPTION);
    }

    @JsonIgnore
    public String getVersion() {
        return getPropertyAsString(PROPERTY_VERSION);
    }

    @JsonIgnore
    public String getContentType() {
        return getPropertyAsString(PROPERTY_CONTENT_TYPE);
    }

    @JsonIgnore
    public String getPropertyNotificationMethod() {
        return getPropertyAsString(PROPERTY_NOTIFICATION_METHOD);
    }

    @JsonIgnore
    public String getPropertyNotificationType() {
        return getPropertyAsString(PROPERTY_NOTIFICATION_TYPE);
    }

    @JsonIgnore
    public boolean isQualityInvestigationReceive() {
        return ASSET_VALUE_QUALITY_INVESTIGATION.equals(this.getPropertyNotificationType()) && ASSET_VALUE_NOTIFICATION_METHOD_RECEIVE.equals(this.getPropertyNotificationMethod());
    }

    @JsonIgnore
    public boolean isQualityInvestigationUpdate() {
        return ASSET_VALUE_QUALITY_INVESTIGATION.equals(this.getPropertyNotificationType()) && ASSET_VALUE_NOTIFICATION_METHOD_UPDATE.equals(this.getPropertyNotificationMethod());
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    @JsonIgnore
    public Object getProperty(String key) {
        return properties.get(key);
    }

    private String getPropertyAsString(String key) {
        var val = getProperty(key);
        return val != null ? val.toString() : null;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder<B extends Builder<B>> {
        protected final Asset asset;

        protected Builder(Asset asset) {
            this.asset = asset;
            asset.properties.put(PROPERTY_ID, UUID.randomUUID().toString()); //must always have an ID
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder(new Asset());
        }

        public B id(String id) {
            asset.properties.put(PROPERTY_ID, id);
            return (B) this;
        }

        public B name(String title) {
            asset.properties.put(PROPERTY_NAME, title);
            return (B) this;
        }

        public B description(String description) {
            asset.properties.put(PROPERTY_DESCRIPTION, description);
            return (B) this;
        }

        public B version(String version) {
            asset.properties.put(PROPERTY_VERSION, version);
            return (B) this;
        }

        public B contentType(String contentType) {
            asset.properties.put(PROPERTY_CONTENT_TYPE, contentType);
            return (B) this;
        }

        public B properties(Map<String, Object> properties) {
            asset.properties = Objects.requireNonNull(properties);
            return (B) this;
        }

        public B property(String key, Object value) {
            asset.properties.put(key, value);
            return (B) this;
        }

        public Asset build() {
            return asset;
        }
    }

}
