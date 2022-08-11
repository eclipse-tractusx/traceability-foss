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

package net.catenax.traceability.assets.infrastructure.adapters.mockdata;

import net.catenax.traceability.assets.domain.Asset;
import net.catenax.traceability.assets.domain.AssetRepository;
import net.catenax.traceability.assets.domain.BpnRepository;
import net.catenax.traceability.assets.domain.PageResult;
import org.springframework.beans.support.MutableSortDefinition;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class InMemoryAssetsRepository implements AssetRepository {

	private final Map<String, Asset> assets = new HashMap<>();

	private final BpnRepository bpnRepository;

	public InMemoryAssetsRepository(BpnRepository bpnRepository) {
		this.bpnRepository = bpnRepository;
	}

	@Override
	public Asset getAssetById(String assetId) {
		return Optional.ofNullable(assets.get(assetId))
			.map(this::addManufacturerName)
			.orElse(null);
	}

	@Override
	public Asset getAssetByChildId(String assetId, String childId) {
		return assets.get(assetId).childDescriptions().stream()
			.filter(childDescription -> childDescription.id().equals(childId))
			.map(childDescription -> assets.get(childDescription.id()))
			.findFirst()
			.map(this::addManufacturerName)
			.orElse(null);
	}

	@Override
	public PageResult<Asset> getAssets(Pageable pageable) {
		PagedListHolder<Asset> pageListHolder = new PagedListHolder<>(new ArrayList<>(assets.values()));
		Sort sort = pageable.getSortOr(Sort.unsorted());

		if (sort.isSorted()) {
			sort.stream().findFirst().ifPresent(order -> {
				pageListHolder.setSort(new MutableSortDefinition(order.getProperty(), true, order.isAscending()));
				pageListHolder.resort();
			});
		}

		pageListHolder.setPage(pageable.getPageNumber());
		pageListHolder.setPageSize(pageable.getPageSize());

		List<Asset> updatedPageListHolder = getAssetsWithUpdatedNamesForPage(pageListHolder);
		pageListHolder.setSource(updatedPageListHolder);

		return new PageResult<>(pageListHolder);
	}

	@Override
	public List<Asset> getAssets() {
		return new ArrayList<>(assets.values());
	}

	@Override
	public Asset save(Asset asset) {
		assets.put(asset.id(), asset);

		return asset;
	}

	@Override
	public List<Asset> saveAll(List<Asset> assets) {
		this.assets.clear();
		this.assets.putAll(assets.stream()
			.collect(Collectors.toMap(Asset::id, Function.identity()))
		);
		return assets;
	}

	@Override
	public long countAssets() {
		return assets.size();
	}

	@Override
	public void clean() {
		assets.clear();
	}

	private List<Asset> getAssetsWithUpdatedNamesForPage(PagedListHolder<Asset> originPageListHolder) {
		List<Asset> updatedAssets = originPageListHolder.getPageList().stream()
			.map(this::addManufacturerName)
			.toList();

		List<Asset> sourceAssets = originPageListHolder.getSource();

		for (int i = originPageListHolder.getFirstElementOnPage(), j = 0; i < originPageListHolder.getLastElementOnPage() + 1; i++, j++) {
			sourceAssets.set(i, updatedAssets.get(j));
		}

		return sourceAssets;
	}

	private Asset addManufacturerName(Asset asset) {
		return bpnRepository.findManufacturerName(asset.manufacturerId())
			.map(asset::withManufacturerName)
			.orElse(asset);
	}
}
