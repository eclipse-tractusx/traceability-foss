/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.eclipse.tractusx.traceability.contracts.infrastructure.model.ContractAgreementAsBuiltEntity;
import org.eclipse.tractusx.traceability.contracts.infrastructure.repository.JpaContractAgreementAsBuiltRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class ContractsSupport {

    @Autowired
    JpaContractAgreementAsBuiltRepository jpaContractAgreementAsBuiltRepository;

    @Transactional
    public void defaultContractAgreementAsBuiltStored() {
        ContractAgreementAsBuiltEntity contractAgreement = new ContractAgreementAsBuiltEntity();
        contractAgreement.setContractAgreementId("abc1");
        contractAgreement.setGlobalAssetId("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb");
        contractAgreement.setCreated(LocalDateTime.now().toInstant(ZoneOffset.UTC));
        contractAgreement.setType(ContractType.ASSET_AS_BUILT);
        contractAgreement.setUpdated(LocalDateTime.now().toInstant(ZoneOffset.UTC));

        jpaContractAgreementAsBuiltRepository.save(contractAgreement);

    }


}
