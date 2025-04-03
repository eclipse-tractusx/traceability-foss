package org.eclipse.tractusx.traceability.digitaltwinpart.domain.repository;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPart;
import org.eclipse.tractusx.traceability.digitaltwinpart.domain.model.DigitalTwinPartDetail;
import org.springframework.data.domain.Pageable;

public interface DigitalTwinPartRepository {
    PageResult<DigitalTwinPart> getAllDigitalTwinParts(Pageable pageable, SearchCriteria searchCriteria);

    DigitalTwinPartDetail getDigitalTwinPartDetail(String id);

}
