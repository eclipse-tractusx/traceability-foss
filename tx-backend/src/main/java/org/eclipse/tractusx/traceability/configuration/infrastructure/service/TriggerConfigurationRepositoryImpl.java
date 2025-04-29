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
package org.eclipse.tractusx.traceability.configuration.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.TriggerConfigurationEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.TriggerConfigurationJPARepository;
import org.eclipse.tractusx.traceability.configuration.infrastructure.repository.TriggerConfigurationRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TriggerConfigurationRepositoryImpl implements TriggerConfigurationRepository {

    private final TriggerConfigurationJPARepository triggerConfigurationJPARepository;

    @Override
    public TriggerConfiguration findTopByCreatedAtDesc() {
        return TriggerConfigurationEntity.toDomain(triggerConfigurationJPARepository.findTopByCreatedAtDesc().orElse(TriggerConfigurationEntity.defaultTriggerConfigurationEntity()));
    }

    @Override
    public void save(TriggerConfiguration triggerConfiguration) {
        triggerConfigurationJPARepository.save(TriggerConfigurationEntity.toEntity(triggerConfiguration));
    }
}
