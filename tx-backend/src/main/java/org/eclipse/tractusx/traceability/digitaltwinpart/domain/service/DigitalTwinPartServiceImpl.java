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
package org.eclipse.tractusx.traceability.digitaltwinpart.domain.service;

import digitaltwinpart.DigitalTwinPartDetailRequest;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.configuration.application.service.ConfigurationService;
import org.eclipse.tractusx.traceability.digitaltwinpart.application.service.DigitalTwinPartService;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPart;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPartDetail;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.repository.DigitalTwinPartRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@RequiredArgsConstructor
@Service
public class DigitalTwinPartServiceImpl implements DigitalTwinPartService {

    private final DigitalTwinPartRepository digitalTwinPartRepository;
    private final ConfigurationService configurationService;

    @Override
    public PageResult<DigitalTwinPart> findAllBy(Pageable pageable, SearchCriteria searchCriteria) {
        return digitalTwinPartRepository.getAllDigitalTwinParts(pageable, searchCriteria);
    }

    @Override
    public DigitalTwinPartDetail findDetail(DigitalTwinPartDetailRequest digitalTwinPartDetailRequest) {
        String filterId = null;
        if (digitalTwinPartDetailRequest != null && digitalTwinPartDetailRequest.getAasId() != null) {
            filterId = digitalTwinPartDetailRequest.getAasId();
        }
        if (digitalTwinPartDetailRequest != null && digitalTwinPartDetailRequest.getGlobalAssetId() != null) {
            filterId = digitalTwinPartDetailRequest.getGlobalAssetId();
        }
        if (filterId == null) {
            throw new IllegalArgumentException("Either aasId or globalAssetId must not be null");
        } else {
            DigitalTwinPartDetail digitalTwinPartDetail = digitalTwinPartRepository.getDigitalTwinPartDetail(filterId);
            enrichNextLookupAndSync(digitalTwinPartDetail);
            return digitalTwinPartDetail;
        }
    }

    private void enrichNextLookupAndSync(DigitalTwinPartDetail digitalTwinPartDetail) {
        String cronAASLookup = configurationService.getLatestTriggerConfiguration().getCronExpressionAASLookup();
        String cronRegisterOrderTTL = configurationService.getLatestTriggerConfiguration().getCronExpressionRegisterOrderTTLReached();

        LocalDateTime now = LocalDateTime.now();

        if (digitalTwinPartDetail.getAasExpirationDate() != null && CronExpression.isValidExpression(cronAASLookup)) {
            CronExpression expression = CronExpression.parse(cronAASLookup);
            LocalDateTime nextLookup = expression.next(now);
            if (nextLookup != null) {
                digitalTwinPartDetail.setNextLookup(nextLookup);
            }
        }

        if (digitalTwinPartDetail.getAssetExpirationDate() != null && CronExpression.isValidExpression(cronRegisterOrderTTL)) {
            CronExpression expression = CronExpression.parse(cronRegisterOrderTTL);
            LocalDateTime nextSync = expression.next(now);
            if (nextSync != null) {
                digitalTwinPartDetail.setNextSync(nextSync);
            }
        }
    }
}
