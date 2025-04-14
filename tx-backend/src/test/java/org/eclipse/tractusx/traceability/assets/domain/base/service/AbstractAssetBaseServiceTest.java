package org.eclipse.tractusx.traceability.assets.domain.base.service;

import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.OrderRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractAssetBaseServiceTest {

    TestService service;

    @BeforeEach
    void setUp() {
        service = new TestService();
    }

    @ParameterizedTest
    @MethodSource("enumFieldNamesProvider")
    void givenEnumFieldName(String fieldName, List<String> startsWith, List<String> expectedValues) {
        // given params

        // when
        List<String> result = service.getSearchableValues(fieldName, startsWith, 10, null, List.of());

        // then
        assertThat(result).containsAll(expectedValues);
    }

    private static Stream<Arguments> enumFieldNamesProvider() {
        return Stream.of(
                Arguments.of("owner", List.of("SU", "CU"), List.of("SUPPLIER", "CUSTOMER")),
                Arguments.of("qualityType", List.of("M"), List.of("MINOR", "MAJOR")),
                Arguments.of("semanticDataModel", null, List.of("BATCH", "SERIALPART", "UNKNOWN", "PARTASPLANNED", "JUSTINSEQUENCE")),
                Arguments.of("importState", List.of("T"), List.of("TRANSIENT"))
        );
    }

    static class TestService extends AbstractAssetBaseService {

        @Override
        protected AssetRepository getAssetRepository() {
            return null;
        }

        @Override
        protected List<String> getDownwardAspects() {
            return null;
        }

        @Override
        protected List<String> getUpwardAspects() {
            return null;
        }

        @Override
        protected BomLifecycle getBomLifecycle() {
            return null;
        }

        @Override
        protected OrderRepository getOrderRepository() {
            return null;
        }

        @Override
        public PageResult<AssetBase> getAssets(Pageable pageable, SearchCriteria searchCriteria) {
            return null;
        }
    }

}
