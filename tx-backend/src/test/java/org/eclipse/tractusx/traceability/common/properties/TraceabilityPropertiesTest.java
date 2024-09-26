package org.eclipse.tractusx.traceability.common.properties; /********************************************************************************
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(value = TraceabilityProperties.class)
class TraceabilityPropertiesTest {

    @Autowired
    private TraceabilityProperties traceabilityProperties;

    static {
        System.setProperty("TRACEABILITY_VALID_UNTIL", "2023-07-04T16:01:05.309Z");
    }

    @Test
    void test_traceabilityProperties() {
        //GIVEN
        //WHEN
        //THEN
        assertThat(traceabilityProperties.getUrl()).isNotEmpty();
        assertThat(traceabilityProperties.getBpn().toString()).isNotEmpty();
        assertThat(traceabilityProperties.getLeftOperand()).isNotEmpty();
        assertThat(traceabilityProperties.getOperatorType()).isNotEmpty();
        assertThat(traceabilityProperties.getRightOperand()).isNotEmpty();
        assertThat(traceabilityProperties.getLeftOperandSecond()).isNotEmpty();
        assertThat(traceabilityProperties.getOperatorTypeSecond()).isNotEmpty();
        assertThat(traceabilityProperties.getRightOperandSecond()).isNotEmpty();
        assertThat(traceabilityProperties.getDiscoveryType()).isNotEmpty();
    }

}
