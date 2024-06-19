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

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static org.eclipse.tractusx.traceability.common.config.TimeConfig.SYSTEM_TIMEZONE;

public class DateUtil {

    public static Instant toInstant(OffsetDateTime offsetDateTime) {
        if (offsetDateTime != null) {
            return offsetDateTime.toInstant();
        } else {
            return null;
        }
    }

    public static OffsetDateTime toOffsetDateTime(Instant instant) {
        if (instant != null) {
            return OffsetDateTime.from(instant.atZone(ZoneId.of(SYSTEM_TIMEZONE)));
        } else {
            return null;
        }
    }
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;

    public static String convertInstantToString(Instant instant) {
        if (instant == null) {
            return null;
        }
        return formatter.format(instant);
    }

}
