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

package org.eclipse.tractusx.traceability.submodel.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.submodel.domain.model.SubmodelCreateRequest;
import org.eclipse.tractusx.traceability.submodel.domain.repository.SubmodelServerRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "provisioning.submodel.use-custom-implementation", havingValue = "false")
public class SubmodelServerClientImpl implements SubmodelServerRepository {


    private final SubmodelClient submodelClient;

    @Override
    public String saveSubmodel(SubmodelCreateRequest submodelCreateRequest) {
        submodelClient.createSubmodel(submodelCreateRequest.getSubmodelId(), submodelCreateRequest.getSubmodel());
        return "";
    }

    @Override
    public String getSubmodel(String submodelId) {
        return submodelClient.getSubmodel(submodelId);
    }

}
