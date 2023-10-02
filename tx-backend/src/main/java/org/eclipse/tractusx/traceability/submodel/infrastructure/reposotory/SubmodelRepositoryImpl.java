/********************************************************************************
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

package org.eclipse.tractusx.traceability.submodel.infrastructure.reposotory;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.submodel.domain.model.Submodel;
import org.eclipse.tractusx.traceability.submodel.domain.repository.SubmodelRepository;
import org.eclipse.tractusx.traceability.submodel.infrastructure.model.SubmodelEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SubmodelRepositoryImpl implements SubmodelRepository {

    private final JpaSubmodelRepository jpaSubmodelRepository;

    @Override
    public void save(Submodel submodel) {
        jpaSubmodelRepository.save(SubmodelEntity.from(submodel));
    }

    @Override
    public Optional<Submodel> findById(String id) {
        return jpaSubmodelRepository.findById(id).map(SubmodelEntity::toDomain);
    }

    @Override
    public void deleteAll() {
        jpaSubmodelRepository.deleteAll();
    }
}
