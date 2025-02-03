package org.eclipse.tractusx.traceability.common.properties; /********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
@EnableConfigurationProperties(value = RegistryProperties.class)
class RegistryPropertiesTest {

    @Autowired
    private RegistryProperties registryProperties;

    @Test
    void test_registryProperties() {
        //GIVEN
        //WHEN
        //THEN
        assertThat(registryProperties.getAllowedBpns()).isNotEmpty();
        assertThat(registryProperties.getShellDescriptorUrl()).isNotEmpty();
        assertThat(registryProperties.getUrlWithPathExternal()).isNotEmpty();
        assertThat(registryProperties.getUrlWithPathInternal()).isNotEmpty();
        assertThat(registryProperties.getShellDescriptorUrl()).isNotEmpty();
    }

}
