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
package org.eclipse.tractusx.traceability.digitaltwinpart.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.DigitalTwinPartNotFoundException;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPart;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPartDetail;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.repository.DigitalTwinPartRepository;
import org.eclipse.tractusx.traceability.digitaltwinpart.infrastructure.model.DigitalTwinPartEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@RequiredArgsConstructor
public class DigitalTwinPartRepositoryImpl implements DigitalTwinPartRepository {
    private final DigitalTwinPartJPARepository repository;

    @Override
    public PageResult<DigitalTwinPart> getAllDigitalTwinParts(Pageable pageable, SearchCriteria searchCriteria) {

        List<DigitalTwinPartSpecification> digitalTwinPartSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList())
                .stream()
                .map(DigitalTwinPartSpecification::new)
                .toList();
        Specification<DigitalTwinPartEntity> specification = BaseSpecification.toSpecification(digitalTwinPartSpecifications);
        return new PageResult<>(repository.findAll(specification, pageable), DigitalTwinPartEntity::toDomain);
    }

    @Override
    public DigitalTwinPartDetail getDigitalTwinPartDetail(String filterId) {
        DigitalTwinPartEntity entity = repository.findByAasIdOrGlobalAssetId(filterId)
                .orElseThrow(() -> new DigitalTwinPartNotFoundException("DigitalTwinPart with ID " + filterId + " not found"));

        return DigitalTwinPartEntity.toDomainDetail(entity);
    }
}
