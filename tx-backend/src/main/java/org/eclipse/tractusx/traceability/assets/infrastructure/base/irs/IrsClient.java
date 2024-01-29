/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs;

import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterPolicyRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.PolicyResponse;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.IRS_ADMIN_TEMPLATE;
import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.IRS_REGULAR_TEMPLATE;

@Component
public class IrsClient {

    @Qualifier(IRS_ADMIN_TEMPLATE)
    private RestTemplate irsAdminTemplate;
    @Qualifier(IRS_REGULAR_TEMPLATE)
    private RestTemplate irsRegularTemplate;

    private final TraceabilityProperties traceabilityProperties;

    public IrsClient(@Qualifier(IRS_ADMIN_TEMPLATE) RestTemplate irsAdminTemplate,
                     @Qualifier(IRS_REGULAR_TEMPLATE) RestTemplate irsRegularTemplate,
                     TraceabilityProperties traceabilityProperties) {
        this.irsAdminTemplate = irsAdminTemplate;
        this.irsRegularTemplate = irsRegularTemplate;
        this.traceabilityProperties = traceabilityProperties;
    }

    public List<PolicyResponse> getPolicies() {
        ResponseEntity<List<PolicyResponse>> responseEntity = irsAdminTemplate.exchange(
                "/irs/policies/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return responseEntity.getBody();
    }

    public void deletePolicy() {
        irsAdminTemplate.exchange("/irs/policies/" + traceabilityProperties.getRightOperand(), HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
        });
    }

    public void registerPolicy() {
        RegisterPolicyRequest registerPolicyRequest = RegisterPolicyRequest.from(traceabilityProperties.getLeftOperand(), OperatorType.fromValue(traceabilityProperties.getOperatorType()), traceabilityProperties.getRightOperand(), traceabilityProperties.getValidUntil());
        irsAdminTemplate.exchange("/irs/policies/", HttpMethod.POST, new HttpEntity<>(registerPolicyRequest), Void.class);
    }

    public void registerJob(RegisterJobRequest registerJobRequest) {
        irsRegularTemplate.exchange("/irs/jobs/", HttpMethod.POST, new HttpEntity<>(registerJobRequest), Void.class);
    }


    @Nullable
    public JobDetailResponse getJobDetailResponse(String jobId) {
        return irsRegularTemplate.exchange("/irs/jobs/" + jobId, HttpMethod.GET, null, new ParameterizedTypeReference<JobDetailResponse>() {
        }).getBody();
    }
}
