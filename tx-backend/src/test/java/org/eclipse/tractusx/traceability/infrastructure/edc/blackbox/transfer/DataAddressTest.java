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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transfer;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DataAddressTest {

    public static final String type = "type";
    public static final String property = "property";
    public static final String keyName = "keyName";
    private static final Map<String, String> properties = new HashMap<>();

    private DataAddress dataAddress;

    @BeforeEach
    void setUp() {
        properties.put(property, property);
        properties.put(keyName, keyName);
        dataAddress = DataAddress.Builder.newInstance()
                .type(type)
                .property(property, property)
                .properties(properties)
                .build();
    }

    @Test
    void getType() {
        assertEquals(type, dataAddress.getType());
    }

    @Test
    void getProperty() {
        assertEquals(property, dataAddress.getProperty(property));
    }

    @Test
    void getKeyName() {
        assertEquals(keyName, dataAddress.getKeyName());
    }

}
