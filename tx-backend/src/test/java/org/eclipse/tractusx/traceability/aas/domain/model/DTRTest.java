/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.aas.domain.model;

import org.eclipse.tractusx.irs.registryclient.decentral.LookupShellsResponseExtended;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

public class DTRTest {

    /**
     * Unit tests for the fromLookupShellsResponseExtended method in the DTR class.
     * This method converts a LookupShellsResponseExtended object into a DTR object.
     */

    @Test
    public void testFromLookupShellsResponseExtended_ValidInput() {
        // Arrange
        String bpn = "BPN123";
        String digitalTwinType = "PartType";
        String nextCursor = "cursor123";
        int limit = 5;
        var aasIds = Arrays.asList("aas1", "aas2");

        LookupShellsResponseExtended lookupShellsResponseExtended = Mockito.mock(LookupShellsResponseExtended.class);
        when(lookupShellsResponseExtended.getBpn()).thenReturn(bpn);
        when(lookupShellsResponseExtended.getDigitalTwinType()).thenReturn(digitalTwinType);
        when(lookupShellsResponseExtended.getCursor()).thenReturn(nextCursor);
        when(lookupShellsResponseExtended.getResult()).thenReturn(aasIds);
        when(lookupShellsResponseExtended.getLimit()).thenReturn(limit);

        // Act
        DTR result = DTR.fromLookupShellsResponseExtended(lookupShellsResponseExtended);

        // Assert
        assertEquals(bpn, result.getBpn());
        assertEquals(DigitalTwinType.digitalTwinTypeFromString(digitalTwinType), result.getDigitalTwinType());
        assertEquals(nextCursor, result.getNextCursor());
        assertEquals(aasIds, result.getAasIds());
        assertEquals(limit, result.getLimit());
    }

    @Test
    public void testFromLookupShellsResponseExtended_EmptyResult() {
        // Arrange
        String bpn = "BPN456";
        String digitalTwinType = "PartInstance";
        String nextCursor = "";
        int limit = 10;

        LookupShellsResponseExtended lookupShellsResponseExtended = Mockito.mock(LookupShellsResponseExtended.class);
        when(lookupShellsResponseExtended.getBpn()).thenReturn(bpn);
        when(lookupShellsResponseExtended.getDigitalTwinType()).thenReturn(digitalTwinType);
        when(lookupShellsResponseExtended.getCursor()).thenReturn(nextCursor);
        when(lookupShellsResponseExtended.getResult()).thenReturn(Arrays.asList());
        when(lookupShellsResponseExtended.getLimit()).thenReturn(limit);

        // Act
        DTR result = DTR.fromLookupShellsResponseExtended(lookupShellsResponseExtended);

        // Assert
        assertEquals(bpn, result.getBpn());
        assertEquals(DigitalTwinType.digitalTwinTypeFromString(digitalTwinType), result.getDigitalTwinType());
        assertEquals(nextCursor, result.getNextCursor());
        assertEquals(0, result.getAasIds().size());
        assertEquals(limit, result.getLimit());
    }

    @Test
    public void testFromLookupShellsResponseExtended_NullInputFields() {
        // Arrange
        LookupShellsResponseExtended lookupShellsResponseExtended = Mockito.mock(LookupShellsResponseExtended.class);
        when(lookupShellsResponseExtended.getBpn()).thenReturn(null);
        when(lookupShellsResponseExtended.getDigitalTwinType()).thenReturn(null);
        when(lookupShellsResponseExtended.getCursor()).thenReturn(null);
        when(lookupShellsResponseExtended.getResult()).thenReturn(null);
        when(lookupShellsResponseExtended.getLimit()).thenReturn(0);

        // Act
        DTR result = DTR.fromLookupShellsResponseExtended(lookupShellsResponseExtended);

        // Assert
        assertNull(result.getBpn());
        assertNull(result.getDigitalTwinType());
        assertNull(result.getNextCursor());
        assertNull(result.getAasIds());
        assertEquals(0, result.getLimit());
    }
}
