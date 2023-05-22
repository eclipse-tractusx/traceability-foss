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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.PolicyType.CONTRACT;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.PolicyType.OFFER;
import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.PolicyType.SET;
import static org.junit.Assert.assertThrows;

class PolicyTypeTest {

    @ParameterizedTest
    @MethodSource("provideValues")
    void getType(PolicyType input, String expectedOutput) {
        // when
        final String result = input.getType();

        // then
        assertThat(result).isEqualTo(expectedOutput);
    }

    @ParameterizedTest
    @MethodSource("provideObjects")
    void fromObject(Map<String, Object> input, PolicyType expectedOutput) {
        // when
        final PolicyType result = PolicyType.fromObject(input);

        // then
        assertThat(result).isEqualTo(expectedOutput);
    }

    @Test
    void givenWrongPolicyObject_whenFromObject_thenThrowIllegalArgumentException() {
        // given
        Map<String, Object> object =Map.of("@policytype", "unknown");

        // when/then
        assertThrows(IllegalArgumentException.class, () -> PolicyType.fromObject(object));
    }

    private static Stream<Arguments> provideValues() {
        return Stream.of(
                Arguments.of(SET, "set"),
                Arguments.of(OFFER, "offer"),
                Arguments.of(CONTRACT, "contract")
        );
    }

    private static Stream<Arguments> provideObjects() {
        return Stream.of(
                Arguments.of(Map.of("@policytype", "set"),SET),
                Arguments.of(Map.of("@policytype", "offer"),OFFER),
                Arguments.of(Map.of("@policytype", "contract"),CONTRACT)
        );
    }
}
