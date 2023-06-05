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

package org.eclipse.tractusx.traceability.shelldescriptor.domain.model.metrics;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.jpa.registrylookup.RegistryLookupMetricEntity;

import java.time.Clock;
import java.time.Instant;

@Data
@ArraySchema(arraySchema = @Schema(description = "RegistryLookupMetric", implementation = RegistryLookupMetric.class), maxItems = Integer.MAX_VALUE)
public class RegistryLookupMetric {

    private final Instant startDate;

    private RegistryLookupStatus registryLookupStatus;

    private Long successShellDescriptorsFetchCount;

    private Long failedShellDescriptorsFetchCount;

    private Long shellDescriptorsFetchDelta;

    private Instant endDate;

    @JsonCreator
    public RegistryLookupMetric(@JsonFormat(shape = JsonFormat.Shape.STRING) @JsonProperty("startDate") Instant startDate,
                                @JsonProperty("registryLookupStatus") RegistryLookupStatus registryLookupStatus,
                                @JsonProperty("successShellDescriptorsFetchCount") Long successShellDescriptorsFetchCount,
                                @JsonProperty("failedShellDescriptorsFetchCount") Long failedShellDescriptorsFetchCount,
                                @JsonProperty("shellDescriptorsFetchDelta") Long shellDescriptorsFetchDelta,
                                @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonProperty("endDate") Instant endDate) {
        this.startDate = startDate;
        this.registryLookupStatus = registryLookupStatus;
        this.successShellDescriptorsFetchCount = successShellDescriptorsFetchCount;
        this.failedShellDescriptorsFetchCount = failedShellDescriptorsFetchCount;
        this.shellDescriptorsFetchDelta = shellDescriptorsFetchDelta;
        this.endDate = endDate;
    }

    public RegistryLookupMetric(Instant startDate) {
        this.startDate = startDate;
        this.successShellDescriptorsFetchCount = 0L;
        this.failedShellDescriptorsFetchCount = 0L;
    }

    public static RegistryLookupMetric start(Clock clock) {
        return new RegistryLookupMetric(clock.instant());
    }

    public RegistryLookupMetric(Instant startDate,
                                RegistryLookupStatus registryLookupStatus,
                                Long successShellDescriptorsFetchCount,
                                Long failedShellDescriptorsFetchCount,
                                Instant endDate) {
        this.startDate = startDate;
        this.registryLookupStatus = registryLookupStatus;
        this.successShellDescriptorsFetchCount = successShellDescriptorsFetchCount;
        this.failedShellDescriptorsFetchCount = failedShellDescriptorsFetchCount;
        this.endDate = endDate;
    }

    public void incrementSuccessShellDescriptorsFetchCount() {
        this.successShellDescriptorsFetchCount += 1;
    }

    public void incrementFailedShellDescriptorsFetchCount() {
        this.failedShellDescriptorsFetchCount += 1;
    }

    public void end(Clock clock) {
        if (this.endDate != null) {
            throw new IllegalStateException("RegistryLookupMetric already finished");
        }

        this.endDate = clock.instant();
        setStatus();
    }

    public void setStatus() {
        if (failedShellDescriptorsFetchCount == 0) {
            this.registryLookupStatus = RegistryLookupStatus.SUCCESSFUL;

            return;
        }

        if (failedShellDescriptorsFetchCount > 0 && successShellDescriptorsFetchCount == 0) {
            this.registryLookupStatus = RegistryLookupStatus.ERROR;

            return;
        }

        if (failedShellDescriptorsFetchCount > 0 && successShellDescriptorsFetchCount > 0) {
            this.registryLookupStatus = RegistryLookupStatus.PARTIALLY_SUCCESS;

            return;
        }

        this.registryLookupStatus = RegistryLookupStatus.SUCCESSFUL;
    }

    public RegistryLookupMetricEntity toEntity() {
        return RegistryLookupMetricEntity.builder()
                .startDate(startDate)
                .status(registryLookupStatus)
                .successShellDescriptorsFetchCount(successShellDescriptorsFetchCount)
                .failedShellDescriptorsFetchCount(failedShellDescriptorsFetchCount)
                .endDate(endDate)
                .build();
    }

    public void firstElementDelta() {
        this.shellDescriptorsFetchDelta = successShellDescriptorsFetchCount;
    }

    public void addDelta(RegistryLookupMetric previousMetric) {
        this.shellDescriptorsFetchDelta = previousMetric.successShellDescriptorsFetchCount;
    }

}
