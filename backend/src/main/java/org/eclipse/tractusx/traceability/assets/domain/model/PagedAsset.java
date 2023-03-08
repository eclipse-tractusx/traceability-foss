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

package org.eclipse.tractusx.traceability.assets.domain.model;

import io.swagger.annotations.ApiParam;

import java.util.List;

/**
 * @implNote Used as schema description object for swagger ui
 */
public final class PagedAsset {
	@ApiParam(value = "assets")
	private List<Asset> assets;
	@ApiParam(value = "page", example = "0")
	private Integer page;
	@ApiParam(value = "pageCount", example = "20")
	private Integer pageCount;
	@ApiParam(value = "pageSize", example = "20")
	private Integer pageSize;
	@ApiParam(value = "totalItem", example = "1")
	private Long totalItem;

	public PagedAsset(List<Asset> assets, Integer page, Integer pageCount, Integer pageSize, Long totalItem) {
		this.assets = assets;
		this.page = page;
		this.pageCount = pageCount;
		this.pageSize = pageSize;
		this.totalItem = totalItem;
	}

	public List<Asset> getAssets() {
		return assets;
	}

	public Integer getPage() {
		return page;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Long getTotalItem() {
		return totalItem;
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setTotalItem(Long totalItem) {
		this.totalItem = totalItem;
	}
}
