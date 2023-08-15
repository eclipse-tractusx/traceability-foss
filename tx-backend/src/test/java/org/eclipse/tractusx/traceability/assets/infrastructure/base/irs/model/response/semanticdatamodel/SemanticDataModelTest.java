package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SemanticDataModelTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned",
            "urn:bamm:io.catenax.part_as_planned:1.0.0#PartAsPlanned",
            "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned"
    })
    void test_IsAsPlanned(String aspectType) {
        //GIVEN
        SemanticDataModel semanticDataModel = new SemanticDataModel();
        semanticDataModel.setAspectType(aspectType);
        //WHEN
        boolean asPlanned = semanticDataModel.isAsPlanned();
        //THEN
        assertTrue(asPlanned);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "urn:bamm:io.catenax.just_in_sequence_part:1.0.0#JustInSequencePart",
            "urn:bamm:com.catenax.batch:1.0.0#Batch",
            "urn:bamm:io.catenax.batch:1.0.0#Batch",
            "urn:bamm:io.catenax.batch:1.0.2#Batch",
            "urn:samm:io.catenax.serial_part:1.0.0#SerialPart",
            "urn:bamm:io.catenax.serial_part:1.0.0#SerialPart",
            "urn:bamm:io.catenax.serial_part:1.1.0#SerialPart",
            "urn:bamm:io.catenax.serial_part:1.0.1#SerialPart"})
    void test_IsAsBuilt(String aspectType) {
        //GIVEN
        SemanticDataModel semanticDataModel = new SemanticDataModel();
        semanticDataModel.setAspectType(aspectType);
        //WHEN
        boolean asBuilt = semanticDataModel.isAsBuilt();
        //THEN
        assertTrue(asBuilt);
    }
}
