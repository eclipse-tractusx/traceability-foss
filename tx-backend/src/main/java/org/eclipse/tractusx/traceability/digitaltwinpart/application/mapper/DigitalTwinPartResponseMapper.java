package org.eclipse.tractusx.traceability.digitaltwinpart.application.mapper;

import digitaltwinpart.DigitalTwinPartResponse;
import lombok.experimental.UtilityClass;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@UtilityClass
public class DigitalTwinPartResponseMapper {
    public static DigitalTwinPartResponse from(DigitalTwinPart part) {
        return DigitalTwinPartResponse.builder()
                .aasId(part.getAasId())
                .aasExpirationDate(part.getAasExpirationDate())
                .globalAssetId(part.getGlobalAssetId())
                .assetExpirationDate(part.getAssetExpirationDate())
                .digitalTwinType(part.getDigitalTwinType())
                .bpn(part.getBpn().toString())
                .build();
    }

    public static PageResult<DigitalTwinPartResponse> fromAsPageResult(PageResult<DigitalTwinPart> notificationPageResult) {
        List<DigitalTwinPartResponse> investigationResponses = notificationPageResult.content().stream().map(DigitalTwinPartResponseMapper::from).toList();
        int pageNumber = notificationPageResult.page();
        int pageSize = notificationPageResult.pageSize();
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<DigitalTwinPartResponse> digitalTwinPartResponses = new PageImpl<>(investigationResponses, pageable, notificationPageResult.totalItems());
        return new PageResult<>(digitalTwinPartResponses);
    }


}
