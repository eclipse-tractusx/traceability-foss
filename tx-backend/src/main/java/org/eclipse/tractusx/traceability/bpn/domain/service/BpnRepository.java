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

package org.eclipse.tractusx.traceability.bpn.domain.service;


import bpn.request.BpnMappingRequest;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnEdcMapping;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BpnRepository {

    BpnEdcMapping findByIdOrThrowNotFoundException(String bpn);

    List<BpnEdcMapping> findAllWhereUrlNotNull();

    boolean existsWhereUrlNotNull(String bpn);

    List<BpnEdcMapping> saveAll(List<BpnMappingRequest> bpnEdcMappings);

    void deleteById(String bpn);

    Optional<String> findManufacturerName(String manufacturerId);

    void updateManufacturers(Map<String, String> bpns);

}
