package org.eclipse.tractusx.traceability.assets.domain.base.service;

import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
    void givenOwnerFieldName(String fieldName, List<String> expectedValues) {
        // given params

        // when
        List<String> result = service.getDistinctFilterValues(fieldName, 10L);

        // then
        assertThat(result).containsAll(expectedValues);
    }

    private static Stream<Arguments> enumFieldNamesProvider() {
        return Stream.of(
                Arguments.of("owner", List.of("SUPPLIER", "CUSTOMER", "OWN", "UNKNOWN")),
                Arguments.of("qualityType", List.of("OK", "MINOR", "MAJOR", "CRITICAL", "LIFE_THREATENING")),
                Arguments.of("semanticDataModel", List.of("BATCH", "SERIALPART", "UNKNOWN", "PARTASPLANNED", "JUSTINSEQUENCE"))
        );
    }

    static class TestService extends AbstractAssetBaseService {

        @Override
        protected AssetRepository getAssetRepository() {
            return null;
        }

        @Override
        protected IrsRepository getIrsRepository() {
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
    }

}
