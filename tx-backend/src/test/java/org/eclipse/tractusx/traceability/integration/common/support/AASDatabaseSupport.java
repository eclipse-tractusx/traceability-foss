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
package org.eclipse.tractusx.traceability.integration.common.support;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.Actor;
import org.eclipse.tractusx.traceability.aas.domain.repository.AASRepository;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.eclipse.tractusx.traceability.aas.infrastructure.repository.JpaAASRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class AASDatabaseSupport {

    @Autowired
    AASRepository aasRepository;

    @Autowired
    JpaAASRepository jpaAASRepository;

    @Autowired
    OAuth2ApiSupport oAuth2ApiSupport;

    public List<AAS> findExistingAASIds(List<String> aasList) {
        return aasRepository.findExistingAasList(aasList);
    }

    public void createAASEntity() {
        AAS aas = AAS.builder()
                .aasId("AAS_ID")
                .created(LocalDateTime.now().minus(Duration.ofDays(5)))
                .updated(LocalDateTime.now())
                .ttl(1000)
                .bpn(BPN.of("BPNL00000001TEST"))
                .actor(Actor.SYSTEM)
                .digitalTwinType(DigitalTwinType.PART_TYPE)
                .build();
        aasRepository.save(List.of(aas));
    }

    public void createExpiredAASEntity() {
        AAS aas = AAS.builder()
                .aasId("AAS_ID2")
                .created(LocalDateTime.now().minus(Duration.ofDays(5)))
                .updated(LocalDateTime.now().minus(Duration.ofMinutes(5)))
                .ttl(1)
                .bpn(BPN.of("BPNL00000001TEST"))
                .actor(Actor.SYSTEM)
                .digitalTwinType(DigitalTwinType.PART_TYPE)
                .build();
        aasRepository.save(List.of(aas));
    }

    public Long aasElementsInDatabase(){
        return jpaAASRepository.count();
    }


}
