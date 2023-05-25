package org.eclipse.tractusx.traceability.assets.application.rest.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class OwnInvestigationsPageable {
    private int page;
    private int size;
    @ArraySchema(arraySchema = @Schema(description = "Content of Investigations PageResults"), maxItems = Integer.MAX_VALUE)
    private List<Sort> sort;

    public static Pageable toPageable(OwnInvestigationsPageable ownInvestigationsPageable) {
        return PageRequest.of(ownInvestigationsPageable.getPage(), ownInvestigationsPageable.getSize(), ownInvestigationsPageable.getSort().stream().findFirst().orElse(Sort.unsorted()));
    }



}
