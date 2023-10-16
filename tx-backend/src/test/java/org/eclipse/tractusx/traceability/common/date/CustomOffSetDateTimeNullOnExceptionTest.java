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
package org.eclipse.tractusx.traceability.common.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.OffsetDateTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomOffSetDateTimeNullOnExceptionTest {

    @Mock
    private JsonParser jsonParser;
    @Mock
    private DeserializationContext deserializationContext;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private JsonNode jsonNode;
    @Mock
    private Logger log;

    @InjectMocks
    private CustomOffSetDateTimeNullOnException customDeserializer;

    @Test
    public void testDeserializeValidDate() throws IOException {
        // Prepare a valid date string
        //String validDateString = "2023-10-13T14:30:45+01:00";
        String validDateString = "2022-02-04T14:48:54";
        // Mock the necessary behavior for deserialization
        when(jsonParser.getCodec()).thenReturn(objectMapper);
        when(objectMapper.readTree(jsonParser)).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn(validDateString);

        // Call the deserialize method
        customDeserializer.deserialize(jsonParser, deserializationContext);

        // Verify that the result is not null and corresponds to the valid date string
        verify(log, never()).warn(anyString(), any(Throwable.class));
        verify(jsonNode, times(1)).asText();
        verify(jsonParser, times(1)).getCodec();
        verify(objectMapper, times(1)).readTree(jsonParser);
    }

    @Test
    public void testDeserializeInvalidDate() throws IOException {
        // Prepare an invalid date string
        String invalidDateString = "invalid-date";

        // Mock the necessary behavior for deserialization
        when(jsonParser.getCodec()).thenReturn(objectMapper);
        when(objectMapper.readTree(jsonParser)).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn(invalidDateString);

        // Call the deserialize method
        OffsetDateTime result = customDeserializer.deserialize(jsonParser, deserializationContext);

        // Verify that the result is null
        verify(jsonNode, times(1)).asText();
        verify(jsonParser, times(1)).getCodec();
        verify(objectMapper, times(1)).readTree(jsonParser);
        assertNull(result);
    }
}
