package org.eclipse.tractusx.traceability.aas.domain.model;

// Importiert die Testbibliothek JUnit 5
import org.junit.jupiter.api.Test;

// Importiert statische Methoden für Assertions (z. B. assertEquals)
import static org.junit.jupiter.api.Assertions.*;

class TwinTypeTest {

    // Testet die Methode getValue() für beide Enum-Werte
    @Test
    void testGetValue() {
        assertEquals("PartType", TwinType.PART_TYPE.getValue());       // Erwartet den String "PartType" vom Enum-Wert PART_TYPE
        assertEquals("PartInstance", TwinType.PART_INSTANCE.getValue()); // Erwartet den String "PartInstance" vom Enum-Wert PART_INSTANCE
    }

    // Testet die Namen der Enum-Werte
    @Test
    void testEnumNames() {
        assertEquals("PART_TYPE", TwinType.PART_TYPE.name());         // Überprüft, ob der Name des Enum-Werts PART_TYPE korrekt ist
        assertEquals("PART_INSTANCE", TwinType.PART_INSTANCE.name()); // Überprüft, ob der Name des Enum-Werts PART_INSTANCE korrekt ist
    }

    // Testet die valueOf-Methode des Enums
    @Test
    void testEnumValueOf() {
        assertEquals(TwinType.PART_TYPE, TwinType.valueOf("PART_TYPE"));         // Erwartet den Enum-Wert PART_TYPE bei Angabe des Strings "PART_TYPE"
        assertEquals(TwinType.PART_INSTANCE, TwinType.valueOf("PART_INSTANCE")); // Erwartet den Enum-Wert PART_INSTANCE bei Angabe des Strings "PART_INSTANCE"
    }

    // Testet, dass getValue() bei keinem Enum-Wert null zurückgibt
    @Test
    void testNotNullValues() {
        for (TwinType type : TwinType.values()) {            // Iteriert über alle Enum-Werte von TwinType
            assertNotNull(type.getValue());                  // Stellt sicher, dass getValue() nicht null ist
        }
    }
}
