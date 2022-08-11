/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package net.catenax.traceability.common.docs;

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
