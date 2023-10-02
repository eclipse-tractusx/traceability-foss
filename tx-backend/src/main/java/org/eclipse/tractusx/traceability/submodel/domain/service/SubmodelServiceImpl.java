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

package org.eclipse.tractusx.traceability.submodel.domain.service;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.submodel.application.service.SubmodelService;
import org.eclipse.tractusx.traceability.submodel.domain.model.Submodel;
import org.eclipse.tractusx.traceability.submodel.domain.model.SubmodelNotFoundException;
import org.eclipse.tractusx.traceability.submodel.domain.repository.SubmodelRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubmodelServiceImpl implements SubmodelService {
    private final SubmodelRepository submodelRepository;

    void findById(String id) {
        submodelRepository.findById(id);
    }

    @Override
    public Submodel getById(String submodelId) {
        return submodelRepository.findById(submodelId)
                .orElseThrow(() -> new SubmodelNotFoundException(submodelId));
    }

    @Override
    public void save(Submodel submodel) {
        submodelRepository.save(submodel);
    }

    @Override
    public void deleteAll() {
        submodelRepository.deleteAll();
    }
}
