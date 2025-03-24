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
import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.eclipse.tractusx.traceability.common.model.BPN;

import java.time.LocalDateTime;

@Data
@Builder
public class AAS {

    private String aasId;
    private Integer ttl;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Actor actor;
    private DigitalTwinType digitalTwinType;
    private BPN bpn;
    private LocalDateTime expiryDate;


    public static Actor actorFromString(String actor) {
        if (actor == null) {
            return null;
        }
        return Actor.valueOf(actor);
    }

    public static DigitalTwinType digitalTwinTypeFromString(String digitalTwinType) {
        if (digitalTwinType == null) {
            return null;
        }
        return DigitalTwinType.valueOf(digitalTwinType);
    }

    public static BPN bpnFromString(String bpn) {
        if (bpn == null) {
            return null;
        }
        return BPN.of(bpn);
    }

}
