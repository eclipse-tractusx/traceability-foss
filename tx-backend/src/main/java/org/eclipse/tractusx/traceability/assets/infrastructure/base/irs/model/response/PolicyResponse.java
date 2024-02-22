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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;

import java.time.OffsetDateTime;

/**
 * Policy representation for get all policies response
 */
@Builder
@Schema(example = PolicyResponse.EXAMPLE_PAYLOAD)
public record PolicyResponse(OffsetDateTime validUntil, Payload payload) {

    @SuppressWarnings({"FileTabCharacter", "java:S2479"})
    // required to show correctly example payload in open-api
    public static final String EXAMPLE_PAYLOAD = """
            [
               {
                	"validUntil": "2025-12-12T23:59:59.999Z",
                	"payload": {
                		"@context": {
                			"odrl": "http://www.w3.org/ns/odrl/2/"
                		},
                		"@id": "policy-id",
                		"policy": {
                			"odrl:permission": [
                				{
                					"odrl:action": "USE",
                					"odrl:constraint": {
                						"odrl:and": [
                							{
                								"odrl:leftOperand": "Membership",
                								"odrl:operator": {
                									"@id": "odrl:eq"
                								},
                								"odrl:rightOperand": "active"
                							},
                							{
                								"odrl:leftOperand": "PURPOSE",
                								"odrl:operator": {
                									"@id": "odrl:eq"
                								},
                								"odrl:rightOperand": "ID 3.1 Trace"
                							}
                						]
                					}
                				}
                			]
                		}
                	}
                }
             ]
               """;

    public static PolicyResponse fromPolicy(final Policy policy) {
        return PolicyResponse.builder()
                .validUntil(policy.getValidUntil())
                .payload(Payload.builder()
                        .policyId(policy.getPolicyId())
                        .context(Context.getDefault())
                        .policy(policy)
                        .build())
                .build();
    }
}
