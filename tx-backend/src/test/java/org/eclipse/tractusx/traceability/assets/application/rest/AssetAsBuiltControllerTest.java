package org.eclipse.tractusx.traceability.assets.application.rest;

import assets.request.AssetRequest;
import assets.response.asbuilt.AssetAsBuiltResponse;
import org.eclipse.tractusx.traceability.assets.application.asbuilt.mapper.AssetAsBuiltFieldMapper;
import org.eclipse.tractusx.traceability.assets.application.asbuilt.mapper.AssetAsBuiltResponseMapper;
import org.eclipse.tractusx.traceability.assets.application.asbuilt.rest.AssetAsBuiltController;
import org.eclipse.tractusx.traceability.assets.application.base.service.AssetBaseService;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssetAsBuiltControllerTest {

    @Mock
    private AssetBaseService assetBaseService;

    @Mock
    private AssetAsBuiltFieldMapper fieldMapper;

    @InjectMocks
    private AssetAsBuiltController assetController;

    @Test
    void testQuery() {

        AssetRequest assetRequest = AssetRequest.builder().page(1).size(10).assetFilters(Collections.emptyList()).build();

        OwnPageable ownPageable = new OwnPageable(1, 10, Collections.emptyList());
        SearchCriteria searchCriteria = SearchCriteriaMapper
                .toSearchCriteria(fieldMapper, assetRequest.getAssetFilters());
        Pageable pageable = OwnPageable.toPageable(ownPageable, fieldMapper);

        PageResult<AssetBase> mockPageResult = new PageResult<>(
                new ArrayList<>(),
                1,
                10,
                10,
                100L
        );

        when(assetBaseService.getAssets(eq(pageable), eq(searchCriteria))).thenReturn(mockPageResult);

        PageResult<AssetAsBuiltResponse> result;
        try (var mockedStatic = Mockito.mockStatic(AssetAsBuiltResponseMapper.class)) {

            PageResult<AssetAsBuiltResponse> mockPageResultResponse = new PageResult<>(
                    new ArrayList<>(),
                    1,
                    10,
                    10,
                    100L
            );

            mockedStatic.when(() -> AssetAsBuiltResponseMapper.from(any(PageResult.class)))
                    .thenReturn(mockPageResultResponse);

            result = assetController.query(assetRequest);

            mockedStatic.verify(() -> AssetAsBuiltResponseMapper.from(any(PageResult.class)));
        }

        // Assert
        assertNotNull(result);
        Assertions.assertEquals(0, result.content().size());
        verify(assetBaseService).getAssets(eq(pageable), eq(searchCriteria));
    }
}
