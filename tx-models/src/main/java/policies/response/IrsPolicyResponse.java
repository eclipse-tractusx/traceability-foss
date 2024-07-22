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
package policies.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.eclipse.tractusx.irs.edc.client.policy.Constraint;
import org.eclipse.tractusx.irs.edc.client.policy.Permission;
import org.eclipse.tractusx.irs.edc.client.policy.Policy;
import policies.request.Payload;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.apache.commons.lang3.StringUtils;


import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

/**
 * Policy representation for get all policies response
 */
@Builder
@Schema(example = IrsPolicyResponse.EXAMPLE_PAYLOAD)
public record IrsPolicyResponse(@JsonFormat(shape = JsonFormat.Shape.STRING) OffsetDateTime validUntil, Payload payload) {
    // https://github.com/eclipse-tractusx/traceability-foss/issues/978
    // "odrl:action" USE will be use (lowercase) make sure to migrate

    @SuppressWarnings({"FileTabCharacter", "java:S2479"})
    // required to show correctly example payload in open-api
    public static final String EXAMPLE_PAYLOAD = """
            [
               {
                	"validUntil": "2025-12-12T23:59:59.999Z",
                	"payload": {
                		"@context": {
                            "@vocab" : "https://w3id.org/edc/v0.0.1/ns/",
                            "edc": "https://w3id.org/edc/v0.0.1/ns/",
                            "cx-policy": "https://w3id.org/catenax/policy/",
                			"odrl": "http://www.w3.org/ns/odrl/2/"
                		},
                		"@id": "policy-id",
                		"policy": {
                			"odrl:permission": [
                				{
                					"odrl:action": "use",
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
}
