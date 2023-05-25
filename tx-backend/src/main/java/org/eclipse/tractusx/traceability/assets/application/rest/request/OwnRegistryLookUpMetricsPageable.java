package org.eclipse.tractusx.traceability.assets.application.rest.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@AllArgsConstructor
public class OwnRegistryLookUpMetricsPageable {
    private int page;
    private int size;
    @ArraySchema(arraySchema = @Schema(description = "Content of Registry Look Up Metrics PageResults"), maxItems = Integer.MAX_VALUE)
    private List<Sort> sort;


    public static Pageable toPageable(OwnRegistryLookUpMetricsPageable ownRegistryLookUpMetricsPageable) {
        return PageRequest.of(ownRegistryLookUpMetricsPageable.getPage(), ownRegistryLookUpMetricsPageable.getSize(), ownRegistryLookUpMetricsPageable.getSort().stream().findFirst().orElse(Sort.unsorted()));
    }

    public static OwnRegistryLookUpMetricsPageable toOwnPageable(Pageable pageable) {
        return new OwnRegistryLookUpMetricsPageable(pageable.getPageNumber(), pageable.getPageSize(), List.of(pageable.getSort()));
    }
}
