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
package org.eclipse.tractusx.traceability.common.date;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateUtilTest {

    @Test
    public void testToInstantNotNull() {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse("2023-10-13T14:30:45+01:00");
        Instant expectedInstant = offsetDateTime.toInstant();
        Instant resultInstant = DateUtil.toInstant(offsetDateTime);
        assertEquals(expectedInstant, resultInstant);
    }

    @Test
    public void testToInstantNull() {
        Instant resultInstant = DateUtil.toInstant(null);
        assertEquals(null, resultInstant);
    }

    @Test
    public void testToOffsetDateTimeNotNull() {
        Instant instant = Instant.parse("2023-10-13T14:30:45Z");
        OffsetDateTime expectedOffsetDateTime = OffsetDateTime.from(instant.atZone(ZoneId.of("UTC")));
        OffsetDateTime resultOffsetDateTime = DateUtil.toOffsetDateTime(instant);
        assertEquals(expectedOffsetDateTime, resultOffsetDateTime);
    }

    @Test
    public void testToOffsetDateTimeNull() {
        OffsetDateTime resultOffsetDateTime = DateUtil.toOffsetDateTime(null);
        assertEquals(null, resultOffsetDateTime);
    }
}
