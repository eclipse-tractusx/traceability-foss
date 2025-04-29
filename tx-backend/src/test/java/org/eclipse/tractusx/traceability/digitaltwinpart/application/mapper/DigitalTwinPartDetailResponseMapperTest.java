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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.digitaltwinpart.application.mapper;

import digitaltwinpart.ActorResponse;
import digitaltwinpart.DigitalTwinPartDetailResponse;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPartDetail;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DigitalTwinPartDetailResponseMapperTest {

    @Test
    void testFrom_AllFieldsMappedCorrectly() {
        DigitalTwinPartDetail partDetail = DigitalTwinPartDetail.builder()
                .aasId("testAasId")
                .aasTTL(3600)
                .aasExpirationDate(LocalDateTime.parse("2025-12-31T23:59:59"))
                .globalAssetId("testGlobalAssetId")
                .assetTTL(7200)
                .assetExpirationDate(LocalDateTime.parse("2026-12-31T23:59:59"))
                .actor("SYSTEM")
                .bpn(BPN.of("BPN123456"))
                .digitalTwinType("TestType")
                .build();

        DigitalTwinPartDetailResponse response = DigitalTwinPartDetailResponseMapper.from(partDetail);

        assertEquals("testAasId", response.getAasId());
        assertEquals(3600, response.getAasTTL());
        assertEquals("2025-12-31T23:59:59", response.getAasExpirationDate().toString());
        assertEquals("testGlobalAssetId", response.getGlobalAssetId());
        assertEquals(7200, response.getAssetTTL());
        assertEquals("2026-12-31T23:59:59", response.getAssetExpirationDate().toString());
        assertEquals(ActorResponse.SYSTEM, response.getActor());
        assertEquals("BPN123456", response.getBpn());
        assertEquals("TestType", response.getDigitalTwinType());
    }

    @Test
    void testFrom_NullFieldsHandledGracefully() {
        DigitalTwinPartDetail partDetail = DigitalTwinPartDetail.builder().build();

        DigitalTwinPartDetailResponse response = DigitalTwinPartDetailResponseMapper.from(partDetail);

        assertNull(response.getAasId());
        assertNull(response.getAasTTL());
        assertNull(response.getAasExpirationDate());
        assertNull(response.getGlobalAssetId());
        assertNull(response.getAssetTTL());
        assertNull(response.getAssetExpirationDate());
        assertNull(response.getActor());
        assertNull(response.getBpn());
        assertNull(response.getDigitalTwinType());
    }

    @Test
    void testFrom_EmptyFieldsHandledAsExpected() {
        DigitalTwinPartDetail partDetail = DigitalTwinPartDetail.builder()
                .aasId("")
                .aasTTL(0)
                .aasExpirationDate(null)
                .globalAssetId("")
                .assetTTL(0)
                .assetExpirationDate(null)
                .digitalTwinType("")
                .build();

        DigitalTwinPartDetailResponse response = DigitalTwinPartDetailResponseMapper.from(partDetail);

        assertEquals("", response.getAasId());
        assertEquals(0, response.getAasTTL());
        assertNull(response.getAasExpirationDate());
        assertEquals("", response.getGlobalAssetId());
        assertEquals(0, response.getAssetTTL());
        assertNull(response.getAssetExpirationDate());
        assertNull(response.getActor()); // gracefully handled
        assertEquals("", response.getDigitalTwinType());
    }
}
