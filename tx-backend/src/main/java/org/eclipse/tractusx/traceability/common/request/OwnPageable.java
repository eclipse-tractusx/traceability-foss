package org.eclipse.tractusx.traceability.common.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Data
@AllArgsConstructor
public class OwnPageable {
    private Integer page;
    private Integer size;
    @ArraySchema(arraySchema = @Schema(description = "Content of Assets PageResults"), maxItems = Integer.MAX_VALUE)
    private List<Sort> sort;

    public static Pageable toPageable(OwnPageable ownPageable) {
        int usedPage = 1;
        int usedPageSize = 50;

        if(ownPageable.page != null) {
            usedPage = ownPageable.getPage();
        }

        if(ownPageable.size != null) {
            usedPageSize = ownPageable.getSize();
        }

        Sort sort = emptyIfNull(ownPageable.getSort()).stream().findFirst().orElse(Sort.unsorted());
        return PageRequest.of(usedPage, usedPageSize, sort);
    }

    public static OwnPageable toOwnPageable(Pageable pageable) {
        return new OwnPageable(pageable.getPageNumber(), pageable.getPageSize(), List.of(pageable.getSort()));
    }



}
