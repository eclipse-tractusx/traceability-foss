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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset.ChildDescriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.eclipse.tractusx.traceability.assets.infrastructure.adapters.jpa.asset.AssetEntity.ChildDescription;

@Component
public class PersistentAssetsRepository implements AssetRepository {

	private final JpaAssetsRepository assetsRepository;

	public PersistentAssetsRepository(JpaAssetsRepository assetsRepository) {
		this.assetsRepository = assetsRepository;
	}

	@Override
	@Transactional
	public Asset getAssetById(String assetId) {
		return assetsRepository.findById(assetId)
			.map(this::toAsset)
			.orElseThrow(() -> new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId)));
	}

	@Override
	public List<Asset> getAssetsById(List<String> assetIds) {
		return assetsRepository.findByIdIn(assetIds).stream()
			.map(this::toAsset)
			.toList();
	}

	@Override
	public Asset getAssetByChildId(String assetId, String childId) {
		return assetsRepository.findById(childId)
			.map(this::toAsset)
			.orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
	}

	@Override
	public PageResult<Asset> getAssets(Pageable pageable) {
		return new PageResult<>(assetsRepository.findAll(pageable), this::toAsset);
	}

	@Override
	public PageResult<Asset> getSupplierAssets(Pageable pageable) {
		return new PageResult<>(assetsRepository.findBySupplierPartIsTrue(pageable), this::toAsset);
	}

	@Override
	public PageResult<Asset> getOwnAssets(Pageable pageable) {
		return new PageResult<>(assetsRepository.findBySupplierPartIsFalse(pageable), this::toAsset);
	}

	@Override
	@Transactional
	public List<Asset> getAssets() {
		return toAssets(assetsRepository.findAll());
	}

	@Override
	public Asset save(Asset asset) {
		return toAsset(assetsRepository.save(toEntity(asset)));
	}

	@Override
	@Transactional
	public List<Asset> saveAll(List<Asset> assets) {
		return toAssets(assetsRepository.saveAll(toEntities(assets)));
	}

	@Override
	public long countAssets() {
		return assetsRepository.count();
	}

	@Override
	public long countMyAssets() {
		return assetsRepository.countBySupplierPartIsFalse();
	}

	private List<Asset> toAssets(List<AssetEntity> entities) {
		return entities.stream()
			.map(this::toAsset)
			.toList();
	}

	private List<AssetEntity> toEntities(List<Asset> assets) {
		return assets.stream()
			.map(this::toEntity)
			.toList();
	}

	private AssetEntity toEntity(Asset asset) {
		return new AssetEntity(
			asset.getId(), asset.getIdShort(),
			asset.getNameAtManufacturer(),
			asset.getManufacturerPartId(),
			asset.getPartInstanceId(),
			asset.getManufacturerId(),
			asset.getBatchId(),
			asset.getManufacturerName(),
			asset.getNameAtCustomer(),
			asset.getCustomerPartId(),
			asset.getManufacturingDate(),
			asset.getManufacturingCountry(),
			asset.isSupplierPart(),
			asset.getChildDescriptions().stream()
				.map(child -> new ChildDescription(child.id(), child.idShort()))
				.toList(),
			asset.getQualityType(),
			asset.getVan()
		);
	}

	private Asset toAsset(AssetEntity entity) {
		return new Asset(
			entity.getId(), entity.getIdShort(),
			entity.getNameAtManufacturer(),
			entity.getManufacturerPartId(),
			entity.getPartInstanceId(),
			entity.getManufacturerId(),
			entity.getBatchId(),
			entity.getManufacturerName(),
			entity.getNameAtCustomer(),
			entity.getCustomerPartId(),
			entity.getManufacturingDate(),
			entity.getManufacturingCountry(),
			entity.isSupplierPart(),
			entity.getChildDescriptors().stream()
					.map(child -> new ChildDescriptions(child.getId(), child.getIdShort()))
					.toList(),
			entity.isOnInvestigation(),
			entity.getQualityType(),
			entity.getVan()
		);
	}
}
