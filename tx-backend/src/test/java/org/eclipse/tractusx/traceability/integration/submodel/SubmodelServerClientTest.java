package org.eclipse.tractusx.traceability.integration.submodel;

import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.SubmodelPayloadRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.submodel.infrastructure.model.SubmodelEntity;
import org.eclipse.tractusx.traceability.submodel.infrastructure.reposotory.JpaSubmodelRepository;
import org.eclipse.tractusx.traceability.submodel.infrastructure.reposotory.SubmodelServerClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SubmodelServerClientTest extends IntegrationTestSpecification {

    @Autowired
    SubmodelServerClient submodelServerClient;

    @Autowired
    JpaSubmodelRepository repository;

    @Test
    void givenSubmodel_whenSaveSubmodel_thenIsPersistedToServer() {
        // given
        String submodelId = "ID_OF_SUBMODEL";
        String submodel = "SUBMODEL PAYLOAD";

        // when
        submodelServerClient.saveSubmodel(submodelId, submodel);

        // then
        Optional<SubmodelEntity> result = repository.findById(submodelId);
        assertThat(result.get().getSubmodel()).isEqualTo(submodel);

    }

}
