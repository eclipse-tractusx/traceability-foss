/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset;

import org.eclipse.tractusx.traceability.investigations.adapters.jpa.InvestigationEntity;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AssetEntityTest {

    @Test
    void testAssetIsNotUnderInvestigationWhenThereIsNoInvestigations() {
        // given
        AssetEntity assetEntity = new AssetEntity();
        assetEntity.setInvestigations(null);

        // expect
        assertThat(assetEntity.isOnInvestigation()).isFalse();
    }

    @Test
    void testAssetIsNotUnderInvestigationWhenInvestigationIsClosed() {
        // given
        AssetEntity assetEntity = new AssetEntity();
        InvestigationEntity investigationEntity = new InvestigationEntity();
        investigationEntity.setStatus(InvestigationStatus.CLOSED);
        assetEntity.setInvestigations(List.of(investigationEntity));

        // expect
        assertThat(assetEntity.isOnInvestigation()).isFalse();
    }

    @Test
    void testAssetIsOnInvestigation() {
        // given
        AssetEntity assetEntity = new AssetEntity();
        InvestigationEntity investigationEntity = new InvestigationEntity();
        investigationEntity.setStatus(InvestigationStatus.ACCEPTED);
        assetEntity.setInvestigations(List.of(investigationEntity));

        // expect
        assertThat(assetEntity.isOnInvestigation()).isTrue();
    }
}
