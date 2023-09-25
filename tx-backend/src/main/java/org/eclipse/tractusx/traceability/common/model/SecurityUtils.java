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

package org.eclipse.tractusx.traceability.common.model;


import java.util.ArrayList;
import java.util.List;

public class SecurityUtils {

    public static String sanitize(String unSanitizedInput) {
        return unSanitizedInput.replaceAll("\r\n|\r|\n", " ");
    }

    public static List<String> sanitizeList(List<String> unSanitizedList) {
        List<String> cleanListOfAffectedItems = new ArrayList<>();
        for (String affectedItems : unSanitizedList) {
            String cleanAffectedItem = sanitize(affectedItems);
            cleanListOfAffectedItems.add(cleanAffectedItem);
        }
        return cleanListOfAffectedItems;
    }
}
