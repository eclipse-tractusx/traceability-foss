package org.eclipse.tractusx.traceability.assets.application.rest;

import assets.request.AssetRequest;
import assets.response.asplanned.AssetAsPlannedResponse;
import org.eclipse.tractusx.traceability.assets.application.asplanned.mapper.AssetAsPlannedFieldMapper;
import org.eclipse.tractusx.traceability.assets.application.asplanned.mapper.AssetAsPlannedResponseMapper;
import org.eclipse.tractusx.traceability.assets.application.asplanned.rest.AssetAsPlannedController;
import org.eclipse.tractusx.traceability.assets.application.base.service.AssetBaseService;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetAsPlannedControllerTest {

    @Mock
    private AssetBaseService assetService;

    @Mock
    private AssetAsPlannedFieldMapper fieldMapper;

    @InjectMocks
    private AssetAsPlannedController assetController;

    @Test
    void testQueryForPlannedAssets() {
        // Arrange
        AssetRequest assetRequest = AssetRequest.builder().page(1).size(10).assetFilters(Collections.emptyList()).build();

        OwnPageable ownPageable = new OwnPageable(1, 10, Collections.emptyList());
        SearchCriteria searchCriteria = SearchCriteriaMapper.toSearchCriteria(fieldMapper, assetRequest.getAssetFilters());

        Pageable pageable = OwnPageable.toPageable(ownPageable, fieldMapper);

        PageResult<AssetBase> mockPageResult = new PageResult<>(
                new ArrayList<>(),
                1,
                10,
                10,
                100L
        );

        when(assetService.getAssets(eq(pageable), eq(searchCriteria))).thenReturn(mockPageResult);

        PageResult<AssetAsPlannedResponse> result;
        try (var mockedStatic = Mockito.mockStatic(AssetAsPlannedResponseMapper.class)) {

            PageResult<AssetAsPlannedResponse> mockPageResultResponse = new PageResult<>(
                    new ArrayList<>(),
                    1,
                    10,
                    10,
                    100L
            );

            mockedStatic.when(() -> AssetAsPlannedResponseMapper.from(any(PageResult.class)))
                    .thenReturn(mockPageResultResponse);
            result = assetController.query(assetRequest);
            mockedStatic.verify(() -> AssetAsPlannedResponseMapper.from(any(PageResult.class)));
        }

        assertNotNull(result);
        assertEquals(0, result.content().size());
        verify(assetService).getAssets(eq(pageable), eq(searchCriteria));
    }
}
