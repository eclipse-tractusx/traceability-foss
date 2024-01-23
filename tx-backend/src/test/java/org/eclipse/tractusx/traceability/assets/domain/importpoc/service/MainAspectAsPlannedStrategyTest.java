package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.AssetMetaInfoRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MainAspectAsPlannedStrategyTest {

    MainAspectAsPlannedStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new MainAspectAsPlannedStrategy();
    }


    @Test
    void testMappingWithNoSubmodels() {
        // given
        ImportRequest.AssetImportRequest assetImportRequestV2 = new ImportRequest.AssetImportRequest(
                new AssetMetaInfoRequest("catenaXId"),
                List.of()
        );

        // when
        AssetBase assetBase = strategy.mapToAssetBase(assetImportRequestV2, new TraceabilityProperties());

        // then
        assertThat(assetBase.getDetailAspectModels()).isNull();
    }
}
