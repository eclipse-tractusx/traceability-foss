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
package org.eclipse.tractusx.traceability.aas.domain.model;

import lombok.Builder;
import lombok.Data;
import org.eclipse.tractusx.irs.registryclient.decentral.LookupShellsResponseExtended;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;


import java.util.List;
import java.util.Optional;

@Data
@Builder
public class DTR {
    private String bpn;
    private DigitalTwinType digitalTwinType;
    private List<String> aasIds;
    private String nextCursor;
    private int limit;

    public static DTR fromLookupShellsResponseExtended(LookupShellsResponseExtended lookupShellsResponseExtended) {

        return DTR.
                builder()
                .digitalTwinType(Optional.ofNullable(lookupShellsResponseExtended.getDigitalTwinType())
                        .map(DigitalTwinType::digitalTwinTypeFromString)
                        .orElse(null))
                .bpn(lookupShellsResponseExtended.getBpn())
                .nextCursor(lookupShellsResponseExtended.getCursor())
                .aasIds(lookupShellsResponseExtended.getResult())
                .limit(lookupShellsResponseExtended.getLimit())
                .build();
    }

}
