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

package org.eclipse.tractusx.traceability.qualitynotification.application.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import qualitynotification.base.request.QualityNotificationSeverityRequest;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class QualityNotificationSeverityRequestTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @MethodSource("validValues")
    void givenValidSeverity_whenDeserialize_thenCreateProperValue(
            final String input, final QualityNotificationSeverityRequest output
    ) throws IOException {
        // when
        final QualityNotificationSeverityRequest result = objectMapper.readValue(input, QualityNotificationSeverityRequest.class);

        // then
        assertThat(result).isEqualTo(output);
    }

    @Test
    void givenInvalidSeverity_whenDeserialize_thenThrowException() throws IOException {
        // given
        final String input = "\"NON_EXISTENT\"";

        // when/then
        try{
            objectMapper.readValue(input, QualityNotificationSeverityRequest.class);
        } catch(ValueInstantiationException exception){
            assertThat(exception.getCause()).isExactlyInstanceOf(NoSuchElementException.class);
            assertThat(exception.getCause().getMessage())
                    .isEqualTo("Unsupported QualityNotificationSeverityRequest: NON_EXISTENT. Must be one of: MINOR, MAJOR, CRITICAL, LIFE-THREATENING");
        }
    }

    private static Stream<Arguments> validValues() {
        return Stream.of(
                Arguments.of("\"MINOR\"", QualityNotificationSeverityRequest.MINOR),
                Arguments.of("\"CRITICAL\"", QualityNotificationSeverityRequest.CRITICAL),
                Arguments.of("\"MAJOR\"", QualityNotificationSeverityRequest.MAJOR),
                Arguments.of("\"LIFE-THREATENING\"", QualityNotificationSeverityRequest.LIFE_THREATENING)
        );
    }
}
