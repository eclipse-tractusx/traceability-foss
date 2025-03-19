package org.eclipse.tractusx.traceability.common.request;

import org.eclipse.tractusx.traceability.common.model.BaseRequestFieldMapper;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaFilter;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetRequestTest {

    private AssetRequest assetRequest;

    @Mock
    private BaseRequestFieldMapper fieldMapper;

    @BeforeEach
    void setUp() {
        assetRequest = new AssetRequest();
        assetRequest.setOperator("OR");
    }

    @Test
    void toSearchCriteria_WithNullFilter_ShouldReturnEmptyCriteria() {
        SearchCriteria criteria = assetRequest.toSearchCriteria(fieldMapper);
        assertNotNull(criteria);
        assertTrue(criteria.getSearchCriteriaFilterList().isEmpty());
    }

    @Test
    void toSearchCriteria_WithValidFilter_ShouldMapCorrectly() {
        AssetRequest.FilterAttribute filterAttribute = new AssetRequest.FilterAttribute();
        AssetRequest.FilterValue filterValue = new AssetRequest.FilterValue();
        filterValue.setValue("testValue");
        filterValue.setStrategy("EQUAL");
        filterAttribute.setValue(List.of(filterValue));

        AssetRequest.AssetFilter filter = new AssetRequest.AssetFilter();
        filter.setId(filterAttribute);
        assetRequest.setFilter(filter);

        when(fieldMapper.mapRequestFieldName("id")).thenReturn("mappedId");

        SearchCriteria criteria = assetRequest.toSearchCriteria(fieldMapper);
        assertNotNull(criteria);
        assertEquals(1, criteria.getSearchCriteriaFilterList().size());
        SearchCriteriaFilter searchCriteriaFilter = criteria.getSearchCriteriaFilterList().get(0);

        assertEquals("mappedId", searchCriteriaFilter.getKey());
        assertEquals("testValue", searchCriteriaFilter.getValue());
        assertEquals(SearchCriteriaOperator.OR, searchCriteriaFilter.getOperator());
    }
}
