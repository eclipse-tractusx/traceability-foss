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

public enum SearchCriteriaStrategy {
    EQUAL("EQUAL"),
    STARTS_WITH("STARTS_WITH"),
    AT_LOCAL_DATE("AT_LOCAL_DATE"),
    AFTER_LOCAL_DATE("AFTER_LOCAL_DATE"),
    BEFORE_LOCAL_DATE("BEFORE_LOCAL_DATE"),
    NOTIFICATION_COUNT_EQUAL("NOTIFICATION_COUNT_EQUAL"),
    EXCLUDE("EXCLUDE"),
    IS_NOT_NULL("IS_NOT_NULL"),
    GLOBAL("GLOBAL");

    private final String value;

    SearchCriteriaStrategy(String value) {
        this.value = value;
    }

    public static SearchCriteriaStrategy fromValue(String value) {
        for (SearchCriteriaStrategy strategy : values()) {
            if (strategy.value.equalsIgnoreCase(value)) {
                return strategy;
            }
        }
        throw new IllegalArgumentException("Invalid SearchCriteriaOperator value: " + value);
    }
}
