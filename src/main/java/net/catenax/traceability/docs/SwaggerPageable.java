package net.catenax.traceability.docs;

import io.swagger.annotations.ApiParam;
import org.springframework.lang.Nullable;

public class SwaggerPageable {

	@ApiParam(value = "Number of records per page", example = "0")
	private Integer size;

	@ApiParam(value = "Results page you want to retrieve (0..N)", example = "0")
	private Integer page;

	@ApiParam(value = "Sorting criteria in the format: [property(,asc|desc)]. Default sort order is ascending. Multiple sort criteria are supported.")
	private String sort;

	@Nullable
	public Integer getSize() {
		return size;
	}

	public void setSize(@Nullable Integer size) {
		this.size = size;
	}

	@Nullable
	public Integer getPage() {
		return page;
	}

	public void setPage(@Nullable Integer page) {
		this.page = page;
	}

	@Nullable
	public String getSort() {
		return sort;
	}

	public void setSort(@Nullable String sort) {
		this.sort = sort;
	}
}
