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

package org.eclipse.tractusx.traceability.integration.common.support;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ISO8601DateTimeMatcher extends TypeSafeMatcher<String> {


    @Override
    public boolean matchesSafely(String item) {
        try {
            DateTimeFormatter.ISO_INSTANT.parse(item);
        } catch (DateTimeParseException ignored) {
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("ISO 8601 date");
    }

    public static Matcher<String> isIso8601DateTime() {
        return new ISO8601DateTimeMatcher();
    }
}
