package net.catenax.traceability.assets;

import org.springframework.beans.support.PagedListHolder;

import java.util.List;

public record PageResult<T>(
	List<T> content,
	Integer page,
	Integer pageCount,
	Integer pageSize,
	Integer totalItems
) {
	PageResult(PagedListHolder<T> pagedListHolder) {
		this(pagedListHolder.getPageList(), pagedListHolder.getPage(), pagedListHolder.getPageSize(), pagedListHolder.getPageSize(), pagedListHolder.getNrOfElements());
	}
}
