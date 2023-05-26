package org.eclipse.tractusx.traceability.common.request;

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
public class OwnPageable {
    private int page;
    private int size;
    @ArraySchema(arraySchema = @Schema(description = "Content of Assets PageResults"), maxItems = Integer.MAX_VALUE)
    private List<Sort> sort;

    public static Pageable toPageable(OwnPageable ownPageable) {
        Object sort = ownPageable.getSort() == null ? Sort.unsorted() : ownPageable.getSort().stream().findFirst() ;
        assert sort instanceof Sort;
        return PageRequest.of(ownPageable.getPage(), ownPageable.getSize(), (Sort) sort);
    }

    public static OwnPageable toOwnPageable(Pageable pageable) {
        return new OwnPageable(pageable.getPageNumber(), pageable.getPageSize(), List.of(pageable.getSort()));
    }



}
