package org.eclipse.tractusx.traceability.common.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
public class OwnPageable {
    private Integer page;
    private Integer size;
    @ArraySchema(arraySchema = @Schema(description = "Content of Assets PageResults"), maxItems = Integer.MAX_VALUE)
    private String sort;

    public static Pageable toPageable(OwnPageable ownPageable) {
        int usedPage = 0;
        int usedPageSize = 50;

        if (ownPageable.page != null) {
            usedPage = ownPageable.getPage();
        }

        if (ownPageable.size != null) {
            usedPageSize = ownPageable.getSize();
        }

        Sort usedSort = Sort.unsorted();

        if (!StringUtils.isBlank(ownPageable.getSort())) {
            usedSort = Sort.by(ownPageable.getSort());
        }

        return PageRequest.of(usedPage, usedPageSize, usedSort);
    }

    public static OwnPageable toOwnPageable(Pageable pageable) {
        return new OwnPageable(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().toString());
    }


}
