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

package org.eclipse.tractusx.traceability.digitaltwinpart.domain.service;

import digitaltwinpart.DigitalTwinPartDetailRequest;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.configuration.application.service.ConfigurationService;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPart;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPartDetail;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.repository.DigitalTwinPartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DigitalTwinPartServiceImplTest {
    private DigitalTwinPartRepository digitalTwinPartRepository;
    private ConfigurationService configurationService;
    private DigitalTwinPartServiceImpl digitalTwinPartService;

    @BeforeEach
    void setup() {
        digitalTwinPartRepository = mock(DigitalTwinPartRepository.class);
        configurationService = mock(ConfigurationService.class);
        digitalTwinPartService = new DigitalTwinPartServiceImpl(digitalTwinPartRepository, configurationService);
    }

    @Test
    void findAllBy_returnsPageResult() {
        Pageable pageable = PageRequest.of(0, 10);
        SearchCriteria searchCriteria = SearchCriteria.builder().build();

        PageResult<DigitalTwinPart> pageResult = new PageResult<>(
                List.of(), // content
                0,         // page
                1,         // pageCount
                10,        // pageSize
                0L         // totalItems
        );

        when(digitalTwinPartRepository.getAllDigitalTwinParts(pageable, searchCriteria)).thenReturn(pageResult);

        PageResult<DigitalTwinPart> result = digitalTwinPartService.findAllBy(pageable, searchCriteria);

        assertThat(result).isEqualTo(pageResult);
        verify(digitalTwinPartRepository).getAllDigitalTwinParts(pageable, searchCriteria);
    }


    @Test
    void findDetail_withValidAasId() {
        DigitalTwinPartDetailRequest request = DigitalTwinPartDetailRequest.builder()
                .aasId("aasId-123")
                .build();

        DigitalTwinPartDetail detail = DigitalTwinPartDetail.builder()
                .aasExpirationDate(LocalDateTime.now())
                .assetExpirationDate(LocalDateTime.now())
                .build();

        when(digitalTwinPartRepository.getDigitalTwinPartDetail("aasId-123")).thenReturn(detail);

        TriggerConfiguration triggerConfig = mock(TriggerConfiguration.class);
        when(configurationService.getLatestTriggerConfiguration()).thenReturn(triggerConfig);
        assert triggerConfig != null;
        when(triggerConfig.getCronExpressionAASLookup()).thenReturn("0 0 12 * * *"); // valid Spring cron
        when(triggerConfig.getCronExpressionRegisterOrderTTLReached()).thenReturn("0 0 12 * * *");

        DigitalTwinPartDetail result = digitalTwinPartService.findDetail(request);

        assertThat(result).isNotNull();
        assertThat(result.getNextLookup()).isNotNull();
        assertThat(result.getNextSync()).isNotNull();
    }


    @Test
    void findDetail_withGlobalAssetIdOverridesAasId() {
        DigitalTwinPartDetailRequest request = DigitalTwinPartDetailRequest.builder()
                .aasId("aasId-123")
                .globalAssetId("global-456")
                .build();
        DigitalTwinPartDetail detail = DigitalTwinPartDetail.builder().build();

        when(digitalTwinPartRepository.getDigitalTwinPartDetail("global-456")).thenReturn(detail);

        TriggerConfiguration triggerConfig = mock(TriggerConfiguration.class);
        when(configurationService.getLatestTriggerConfiguration()).thenReturn(triggerConfig);
        when(triggerConfig.getCronExpressionAASLookup()).thenReturn("0 0 12 * * *");
        when(triggerConfig.getCronExpressionRegisterOrderTTLReached()).thenReturn("0 0 12 * * *");

        DigitalTwinPartDetail result = digitalTwinPartService.findDetail(request);

        assertThat(result).isNotNull();
        verify(digitalTwinPartRepository).getDigitalTwinPartDetail("global-456");
    }


    @Test
    void findDetail_withNullRequest_throwsException() {
        assertThatThrownBy(() -> digitalTwinPartService.findDetail(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Either aasId or globalAssetId must not be null");
    }

    @Test
    void findDetail_withEmptyRequest_throwsException() {
        DigitalTwinPartDetailRequest request = DigitalTwinPartDetailRequest.builder()
                .build();

        assertThatThrownBy(() -> digitalTwinPartService.findDetail(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Either aasId or globalAssetId must not be null");
    }
}
