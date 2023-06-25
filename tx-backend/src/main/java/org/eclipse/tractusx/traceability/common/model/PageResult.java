/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.model;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@ArraySchema(arraySchema = @Schema(description = "PageResults", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
public record PageResult<T>(
        @ArraySchema(arraySchema = @Schema(description = "Content of PageResults"), minItems = 0, maxItems = Integer.MAX_VALUE)
	List<T> content,
        @ApiModelProperty(example = "1")
        Integer page,
        @ApiModelProperty(example = "15")
        Integer pageCount,
        @ApiModelProperty(example = "10")
        Integer pageSize,
        @ApiModelProperty(example = "2")
        Long totalItems
) {

	public PageResult(List<T> data) {
		this(new PagedListHolder<>(data));
	}

	public PageResult(PagedListHolder<T> pagedListHolder) {
		this(pagedListHolder.getPageList(), pagedListHolder.getPage(), pagedListHolder.getPageSize(), pagedListHolder.getPageSize(), (long)pagedListHolder.getNrOfElements());
	}

	public PageResult(Page<T> page) {
		this(page, Function.identity());
	}

	public <R> PageResult(Page<R> page, Function<R, T> mapping) {
		this(page.getContent().stream().map(mapping).toList(), page.getPageable().getPageNumber(), page.getTotalPages(), page.getPageable().getPageSize(), page.getTotalElements());
	}
}
