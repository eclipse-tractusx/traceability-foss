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

import feign.Param;
import feign.RequestLine;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Constraints;
import org.eclipse.tractusx.irs.edc.client.policy.Operator;
import org.eclipse.tractusx.irs.edc.client.policy.OperatorType;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import org.eclipse.tractusx.irs.edc.client.policy.PolicyType;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterJobRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.RegisterPolicyRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Context;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsPolicyResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Payload;
import org.eclipse.tractusx.traceability.bpdm.model.request.RegisterEssInvestigationJobRequest;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.IRS_ADMIN_TEMPLATE;
import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.IRS_REGULAR_TEMPLATE;

@Slf4j
@Component
public class IrsClient {

    private final RestTemplate irsAdminTemplate;

    private final RestTemplate irsRegularTemplate;

    private final TraceabilityProperties traceabilityProperties;

    @Value("${traceability.irsPoliciesPath}")
    String policiesPath = null;

    public IrsClient(@Qualifier(IRS_ADMIN_TEMPLATE) RestTemplate irsAdminTemplate,
                     @Qualifier(IRS_REGULAR_TEMPLATE) RestTemplate irsRegularTemplate,
                     TraceabilityProperties traceabilityProperties) {
        this.irsAdminTemplate = irsAdminTemplate;
        this.irsRegularTemplate = irsRegularTemplate;
        this.traceabilityProperties = traceabilityProperties;
    }

    public List<IrsPolicyResponse> getPolicies() {
        return irsAdminTemplate.exchange(policiesPath, HttpMethod.GET, null, new ParameterizedTypeReference<List<IrsPolicyResponse>>() {
        }).getBody();
    }

    public void deletePolicy() {
        irsAdminTemplate.exchange(policiesPath + "/" + traceabilityProperties.getRightOperand(), HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
        });
    }

    public void registerPolicy() {
        OffsetDateTime validUntil = traceabilityProperties.getValidUntil();
        Context context = Context.getDefault();
        String policyId = UUID.randomUUID().toString();

        Constraint constraint = new Constraint(traceabilityProperties.getLeftOperand(), new Operator(OperatorType.EQ), traceabilityProperties.getRightOperand());

        Constraints constraints = Constraints.builder()
                .and(List.of(constraint))
                .or(List.of(constraint))
                .build();

        Permission permission = Permission.builder()
                .action(PolicyType.USE)
                .constraint(constraints)
                .build();

        Policy policy = new Policy(policyId, Instant.now().atOffset(ZoneOffset.UTC), validUntil, List.of(permission));

        Payload payload = new Payload(context, policyId, policy);
        RegisterPolicyRequest registerPolicyRequest = new RegisterPolicyRequest(validUntil.toInstant(), payload);
        irsAdminTemplate.exchange(policiesPath, HttpMethod.POST, new HttpEntity<>(registerPolicyRequest), Void.class);
    }

    public void registerJob(RegisterJobRequest registerJobRequest) {
        irsRegularTemplate.exchange("/irs/jobs", HttpMethod.POST, new HttpEntity<>(registerJobRequest), Void.class);
    }

    @Nullable
    public IRSResponse getIrsJobDetailResponse(String jobId) {
        return irsRegularTemplate.exchange("/irs/jobs/" + jobId, HttpMethod.GET, null, new ParameterizedTypeReference<IRSResponse>() {
        }).getBody();
    }

    public RegisterJobResponse registerEssInvestigationJob(RegisterEssInvestigationJobRequest registerJobRequest) {
        return irsRegularTemplate.exchange("/ess/bpn/investigations", HttpMethod.POST, new HttpEntity<>(registerJobRequest),  new ParameterizedTypeReference<RegisterJobResponse>() {
        }).getBody();
    }

    public JobDetailResponse getEssInvestigationJobDetails(String jobId) {
        return irsRegularTemplate.exchange("/ess/bpn/investigations/" + jobId, HttpMethod.GET, null, new ParameterizedTypeReference<JobDetailResponse>() {
        }).getBody();
    }
}
