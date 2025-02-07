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
package org.eclipse.tractusx.traceability.policies.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;
import policies.response.PolicyResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.IRS_ADMIN_TEMPLATE;

@Slf4j
@Component
public class PolicyClient {
    private static final String DEFAULT_POLICY_EMPTY_PLACEHOLDER = "";
    private static final String DEFAULT_POLICY_PLACEHOLDER = "default";
    private final RestTemplate irsAdminTemplate;
    private final TraceabilityProperties traceabilityProperties;

    @Value("${traceability.irsPoliciesPath}")
    String policiesPath = null;

    public PolicyClient(@Qualifier(IRS_ADMIN_TEMPLATE) RestTemplate irsAdminTemplate,
                        TraceabilityProperties traceabilityProperties) {
        this.irsAdminTemplate = irsAdminTemplate;
        this.traceabilityProperties = traceabilityProperties;
    }

    public List<PolicyResponse> getPoliciesByApplicationBPNOrDefault() {
        Map<String, List<IrsPolicyResponse>> policies = getPolicies();
        String applicationBPN = traceabilityProperties.getBpn().value();

        List<IrsPolicyResponse> irsPolicyResponsesByBpn = policies.get(applicationBPN);

        if (CollectionUtils.isEmpty(irsPolicyResponsesByBpn)) {
            Stream<IrsPolicyResponse> defaultPolicies = Stream.concat(
                    CollectionUtils.emptyIfNull(policies.get(DEFAULT_POLICY_PLACEHOLDER)).stream(),
                    CollectionUtils.emptyIfNull(policies.get(DEFAULT_POLICY_EMPTY_PLACEHOLDER)).stream());

            return defaultPolicies
                    .max(Comparator.comparing(p -> p.payload().policy().getCreatedOn()))
                    .map(irsPolicyResponse -> IrsPolicyResponse.toResponse(irsPolicyResponse, applicationBPN)).stream().toList();
        } else {
            return irsPolicyResponsesByBpn
                    .stream()
                    .map(irsPolicyResponse -> IrsPolicyResponse.toResponse(irsPolicyResponse, applicationBPN)).toList();
        }

    }

    public Map<String, List<IrsPolicyResponse>> getPolicies() {
        Map<String, List<IrsPolicyResponse>> body;

        try {
            // Attempt to fetch the response body
            body = irsAdminTemplate.exchange(
                    policiesPath,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, List<IrsPolicyResponse>>>() {}
            ).getBody();
        } catch (HttpClientErrorException.NotFound e) {
            // Handle 404 (Not Found) specifically
            log.warn("No policies found at the provided path: {}", policiesPath);
            return Collections.emptyMap(); // Return an empty map for 404 responses
        } catch (RestClientException e) {
            // Handle other client-related errors
            log.error("Failed to retrieve policies from IRS Policy Store: {}", e.getMessage(), e);
            throw e; // Re-throw the exception if desired, or handle it based on application requirements
        }

        // Null-safe handling of the body
        if (body == null || body.isEmpty()) {
            log.info("No policies retrieved from IRS Policy Store");
            return Collections.emptyMap();
        }

        // Process and log the retrieved policies
        body.forEach((key, valueList) -> {
            String normalizedKey = StringUtils.normalizeSpace(key);
            log.info("Key: {}", normalizedKey);

            if (valueList != null) {
                valueList.forEach(value -> log.info("Policy: {}", StringUtils.normalizeSpace(value.toString())));
            } else {
                log.info("No policies for key: {}", normalizedKey);
            }
        });

        return body;
    }

    public void deletePolicy(String policyId) {
        irsAdminTemplate.exchange(policiesPath + "/" + policyId, HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
        });
    }

    public void updatePolicy(UpdatePolicyRequest updatePolicyRequest) {
        irsAdminTemplate.exchange(policiesPath, HttpMethod.PUT, new HttpEntity<>(updatePolicyRequest), new ParameterizedTypeReference<>() {
        });
    }

    public CreatePolicyResponse createPolicy(RegisterPolicyRequest registerPolicyRequest) {
        return irsAdminTemplate.exchange(policiesPath, HttpMethod.POST, new HttpEntity<>(registerPolicyRequest), CreatePolicyResponse.class).getBody();
    }

}
