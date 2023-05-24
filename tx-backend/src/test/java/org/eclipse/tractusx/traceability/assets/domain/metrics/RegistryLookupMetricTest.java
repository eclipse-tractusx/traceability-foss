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
package org.eclipse.tractusx.traceability.assets.domain.metrics;

import org.eclipse.tractusx.traceability.assets.infrastructure.repository.jpa.registrylookup.RegistryLookupMetricEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RegistryLookupMetricTest {

    private Clock clock;

    @BeforeEach
    void setUp() {
        clock = Clock.systemUTC();
    }


    @Test
    void testEnd() {
        // Test case 1: End date is null, should set the end date and update status
        Instant startDate = Instant.now();
        RegistryLookupMetric metric = new RegistryLookupMetric(startDate);
        Clock clock = Clock.fixed(startDate.plusSeconds(10), ZoneId.systemDefault());
        metric.end(clock);
        assertEquals(startDate.plusSeconds(10), metric.getEndDate());
        assertNotNull(metric.getRegistryLookupStatus());
    }

    @Test
    void testToEntity() {
        Instant startDate = Instant.parse("2023-05-24T10:00:00Z");
        RegistryLookupMetric metric = new RegistryLookupMetric(startDate,
                RegistryLookupStatus.SUCCESSFUL,
                10L,
                5L,
                Instant.parse("2023-05-24T11:00:00Z"));

        RegistryLookupMetricEntity entity = metric.toEntity();

        assertEquals(startDate, entity.getStartDate());
        assertEquals(RegistryLookupStatus.SUCCESSFUL, entity.getStatus());
        assertEquals(10L, entity.getSuccessShellDescriptorsFetchCount());
        assertEquals(5L, entity.getFailedShellDescriptorsFetchCount());
        assertEquals(Instant.parse("2023-05-24T11:00:00Z"), entity.getEndDate());
    }

    @Test
    void testSetStatus() {
        // Test case 1: All counts are zero
        RegistryLookupMetric metric1 = new RegistryLookupMetric(null);
        metric1.setStatus();
        assertEquals(RegistryLookupStatus.SUCCESSFUL, metric1.getRegistryLookupStatus());

        // Test case 2: Only success count is zero
        RegistryLookupMetric metric2 = new RegistryLookupMetric(null);
        metric2.incrementFailedShellDescriptorsFetchCount();
        metric2.setStatus();
        assertEquals(RegistryLookupStatus.ERROR, metric2.getRegistryLookupStatus());

        // Test case 3: Both success and failure counts are non-zero
        RegistryLookupMetric metric3 = new RegistryLookupMetric(null);
        metric3.incrementSuccessShellDescriptorsFetchCount();
        metric3.incrementFailedShellDescriptorsFetchCount();
        metric3.setStatus();
        assertEquals(RegistryLookupStatus.PARTIALLY_SUCCESS, metric3.getRegistryLookupStatus());

        // Test case 4: Only failure count is zero
        RegistryLookupMetric metric4 = new RegistryLookupMetric(null);
        metric4.incrementSuccessShellDescriptorsFetchCount();
        metric4.setStatus();
        assertEquals(RegistryLookupStatus.SUCCESSFUL, metric4.getRegistryLookupStatus());
    }

    @Test
    void testStart() {
        RegistryLookupMetric metric = RegistryLookupMetric.start(clock);
        assertNotNull(metric.getStartDate());
        assertNull(metric.getRegistryLookupStatus());
        assertEquals(0L, metric.getSuccessShellDescriptorsFetchCount());
        assertEquals(0L, metric.getFailedShellDescriptorsFetchCount());
        assertNull(metric.getEndDate());
    }

    @Test
    void testIncrementSuccessShellDescriptorsFetchCount() {
        Instant startDate = Instant.now();
        RegistryLookupMetric metric = new RegistryLookupMetric(startDate);

        metric.incrementSuccessShellDescriptorsFetchCount();
        assertEquals(1L, metric.getSuccessShellDescriptorsFetchCount());

        metric.incrementSuccessShellDescriptorsFetchCount();
        assertEquals(2L, metric.getSuccessShellDescriptorsFetchCount());
    }
}
