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

package net.catenax.traceability.assets.domain.model;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
	List<T> content,
	Integer page,
	Integer pageCount,
	Integer pageSize,
	Long totalItems
) {
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
