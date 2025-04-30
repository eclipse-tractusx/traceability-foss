package org.eclipse.tractusx.traceability.aas.domain.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TwinTypeTest {

    @Test
    void testGetValue() {
        assertEquals("PartType", TwinType.PART_TYPE.getValue());
        assertEquals("PartInstance", TwinType.PART_INSTANCE.getValue());
    }

    @Test
    void testEnumNames() {
        assertEquals("PART_TYPE", TwinType.PART_TYPE.name());
        assertEquals("PART_INSTANCE", TwinType.PART_INSTANCE.name());
    }

    @Test
    void testEnumValueOf() {
        assertEquals(TwinType.PART_TYPE, TwinType.valueOf("PART_TYPE"));
        assertEquals(TwinType.PART_INSTANCE, TwinType.valueOf("PART_INSTANCE"));
    }

    @Test
    void testNotNullValues() {
        for (TwinType type : TwinType.values()) {
            assertNotNull(type.getValue());
        }
    }
}

