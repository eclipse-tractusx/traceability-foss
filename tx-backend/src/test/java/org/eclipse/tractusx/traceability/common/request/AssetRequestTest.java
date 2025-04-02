package org.eclipse.tractusx.traceability.common.request;

import assets.request.AssetFilter;
import assets.request.AssetRequest;
import common.FilterAttribute;
import common.FilterValue;
import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetRequestTest {

    @Mock
    private BaseRequestFieldMapper fieldMapper;

    @Test
    void toSearchCriteria_WithNullFilter_ShouldReturnEmptyCriteria() {
        AssetRequest assetRequest = AssetRequest.builder().assetFilters(Collections.emptyList()).build();
        SearchCriteria criteria = SearchCriteriaMapper.toSearchCriteria(fieldMapper, assetRequest.getAssetFilters());
        assertNotNull(criteria);
        assertTrue(criteria.getSearchCriteriaFilterList().isEmpty());
    }

    @Test
    void toSearchCriteria_WithValidFilter_ShouldMapCorrectly() {

        FilterAttribute filterAttribute = FilterAttribute.builder()
                .value(List.of(FilterValue.builder()
                        .value("testValue")
                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                        .build()))
                .operator(SearchCriteriaOperator.OR.name())
                .build();

        AssetFilter filter = AssetFilter.builder().id(filterAttribute).build();

        AssetRequest assetRequest = AssetRequest.builder().assetFilters(List.of(filter)).build();


        when(fieldMapper.mapRequestFieldName("id")).thenReturn("mappedId");

        SearchCriteria criteria = SearchCriteriaMapper.toSearchCriteria(fieldMapper, assetRequest.getAssetFilters());
        assertNotNull(criteria);
        assertEquals(1, criteria.getSearchCriteriaFilterList().size());
        SearchCriteriaFilter searchCriteriaFilter = criteria.getSearchCriteriaFilterList().get(0);

        assertEquals("mappedId", searchCriteriaFilter.getKey());
        assertEquals("testValue", searchCriteriaFilter.getValue());
        assertEquals(SearchCriteriaOperator.OR, searchCriteriaFilter.getOperator());
    }
}
