/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.bpdm.model.request;

import java.util.List;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.PartChainIdentificationKey;

public record RegisterEssInvestigationJobRequest(
        BomLifecycle bomLifecycle,
        String callbackUrl,
        List<String> incidentBPNSs,
        PartChainIdentificationKey key
) {
    public static RegisterEssInvestigationJobRequest buildRequest(
            BomLifecycle bomLifecycle, String callback, List<String> incidentBPNSs, String globalAssetId, String bpn) {
        return new RegisterEssInvestigationJobRequest(
            bomLifecycle, callback, incidentBPNSs, new PartChainIdentificationKey(globalAssetId, bpn));
    }
}
