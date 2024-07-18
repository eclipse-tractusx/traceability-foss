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
package org.eclipse.tractusx.traceability.policies.domain;

import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;
import policies.response.CreatePolicyResponse;
import policies.response.IrsPolicyResponse;
import policies.response.PolicyResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PolicyRepository {
    Map<String, List<IrsPolicyResponse>> getPolicies();

    Optional<PolicyResponse> getNewestPolicyByOwnBpn();

    Map<String, Optional<IrsPolicyResponse>> getPolicy(String policyId);

    void createPolicyBasedOnAppConfig();

    void deletePolicy(String policyId);

    void updatePolicy(UpdatePolicyRequest updatePolicyRequest);

    CreatePolicyResponse createPolicy(RegisterPolicyRequest registerPolicyRequest);
}
