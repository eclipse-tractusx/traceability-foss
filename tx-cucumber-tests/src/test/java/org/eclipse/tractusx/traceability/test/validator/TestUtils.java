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

package org.eclipse.tractusx.traceability.test.validator;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestUtils {

    private static final String UNIQUE_SEPARATOR = ":-:";

    public static String wrapStringWithUUID(String string) {
        return UNIQUE_SEPARATOR + string + UNIQUE_SEPARATOR + UUID.randomUUID();
    }

    public static String unWrapStringWithTimestamp(String string) {
        String[] array = string.split(UNIQUE_SEPARATOR);
        return array[1];
    }

    public static Map<String, String> normalize(Map<String, String> input) {
        return input.entrySet().stream().map(entry -> Map.entry(normalizeString(entry.getKey()), normalizeString(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String normalizeString(String input) {
        Pattern r = Pattern.compile("\"(.+)\"");

        Matcher m = r.matcher(input);

        if (m.matches()) {
            return m.group(1);
        } else {
            return "";
        }

    }
}
