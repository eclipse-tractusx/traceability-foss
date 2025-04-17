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

package org.eclipse.tractusx.traceability.assets.domain.service;

import assets.request.PartChainIdentificationKey;
import java.util.stream.Stream;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.base.OrderRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AssetAsBuiltServiceImplTest {

    @InjectMocks
    private AssetAsBuiltServiceImpl assetService;

    @Mock
    private OrderRepository orderRepository;

    @Test
    void synchronizeAssets_shouldSaveCombinedAssets_whenNoException() {
        // given
        List<PartChainIdentificationKey> keys = Stream.of("123")
                .map(id -> new PartChainIdentificationKey(null, id, "bpn"))
                .toList();

        // when
        assetService.syncAssetsUsingIRSOrderAPI(keys, null);

        // then
        verify(orderRepository).createOrderToResolveAssets(keys, Direction.DOWNWARD, Aspect.downwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT, null);
        verify(orderRepository).createOrderToResolveAssets(keys, Direction.UPWARD, Aspect.upwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT, null);
    }
}
