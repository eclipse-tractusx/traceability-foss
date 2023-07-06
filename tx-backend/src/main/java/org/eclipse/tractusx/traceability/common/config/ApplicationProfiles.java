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

package org.eclipse.tractusx.traceability.common.config;

import org.springframework.core.env.Environment;

import java.util.Arrays;

public class ApplicationProfiles {

    public static final String LOCAL = "local";
    public static final String TESTS = "integration";
    public static final String NOT_TESTS = "!" + TESTS;
    public static final String DEV = "dev";
    public static final String TEST = "test";
    public static final String INT = "int";
    public static final String INT_A = "int-a";
    public static final String INT_B = "int-b";
    public static final String E2E_A = "e2e-a";
    public static final String E2E_B = "e2e-b";

    private ApplicationProfiles() {
    }

    public static boolean doesNotContainTestProfile(Environment environment) {
        return Arrays.stream(environment.getActiveProfiles())
                .noneMatch(profile -> profile.equals(TESTS));
    }
}
