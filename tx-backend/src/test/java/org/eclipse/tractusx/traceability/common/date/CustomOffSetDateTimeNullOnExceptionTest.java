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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.OffsetDateTime;

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
        String validDateString = "2022-02-04T14:48:54";
        when(jsonParser.getCodec()).thenReturn(objectMapper);
        when(objectMapper.readTree(jsonParser)).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn(validDateString);

        customDeserializer.deserialize(jsonParser, deserializationContext);

        verify(log, never()).warn(anyString(), any(Throwable.class));
        verify(jsonNode, times(1)).asText();
        verify(jsonParser, times(1)).getCodec();
        verify(objectMapper, times(1)).readTree(jsonParser);
    }

    @Test
    public void testDeserializeInvalidDate() throws IOException {

        String invalidDateString = "invalid-date";

        when(jsonParser.getCodec()).thenReturn(objectMapper);
        when(objectMapper.readTree(jsonParser)).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn(invalidDateString);

        OffsetDateTime result = customDeserializer.deserialize(jsonParser, deserializationContext);

        verify(jsonNode, times(1)).asText();
        verify(jsonParser, times(1)).getCodec();
        verify(objectMapper, times(1)).readTree(jsonParser);
        Assertions.assertNull(result);
    }
}
