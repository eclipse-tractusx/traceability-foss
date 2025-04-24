/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.configuration.application.mapper;

import configuration.response.TriggerConfigurationResponse;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;

public class TriggerConfigurationResponseMapper {

    public static TriggerConfigurationResponse from(final TriggerConfiguration triggerConfiguration) {

        TriggerConfigurationResponse.TriggerConfigurationResponseBuilder triggerConfigurationResponse = TriggerConfigurationResponse.builder();

        if (triggerConfiguration == null) {
            return triggerConfigurationResponse.build();
        }
        triggerConfigurationResponse.id(triggerConfiguration.getId());
        triggerConfigurationResponse.cronExpressionMapCompletedOrders(triggerConfiguration.getCronExpressionMapCompletedOrders());
        triggerConfigurationResponse.cronExpressionRegisterOrderTTLReached(triggerConfiguration.getCronExpressionRegisterOrderTTLReached());
        triggerConfigurationResponse.aasTTL(triggerConfiguration.getAasTTL());
        triggerConfigurationResponse.partTTL(triggerConfiguration.getPartTTL());
        triggerConfigurationResponse.cronExpressionAASLookup(triggerConfiguration.getCronExpressionAASLookup());
        triggerConfigurationResponse.cronExpressionAASCleanup(triggerConfiguration.getCronExpressionAASCleanup());
        triggerConfigurationResponse.cronExpressionPublishAssets(triggerConfiguration.getCronExpressionPublishAssets());
        triggerConfigurationResponse.aasLimit(triggerConfiguration.getAasLimit());
        triggerConfigurationResponse.fetchLimit(triggerConfiguration.getFetchLimit());
        return triggerConfigurationResponse.build();
    }

}
