package org.eclipse.tractusx.traceability.assets.application.rest.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class OwnAssetsPageable {
    private int page;
    private int size;
    @ArraySchema(arraySchema = @Schema(description = "Content of Assets PageResults"), maxItems = Integer.MAX_VALUE)
    private List<Sort> sort;

    public static Pageable toPageable(OwnAssetsPageable ownAssetsPageable) {
        return PageRequest.of(ownAssetsPageable.getPage(), ownAssetsPageable.getSize(), ownAssetsPageable.getSort().stream().findFirst().orElse(Sort.unsorted()));
    }



}
