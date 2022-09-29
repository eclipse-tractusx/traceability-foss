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

package net.catenax.traceability.assets.infrastructure.adapters.jpa.asset;

import net.catenax.traceability.assets.domain.model.Asset;
import net.catenax.traceability.assets.domain.model.Asset.ChildDescriptions;
import net.catenax.traceability.assets.domain.model.AssetNotFoundException;
import net.catenax.traceability.assets.domain.model.InvestigationStatus;
import net.catenax.traceability.assets.domain.model.PageResult;
import net.catenax.traceability.assets.domain.ports.AssetRepository;
import net.catenax.traceability.assets.infrastructure.adapters.jpa.asset.AssetEntity.PendingInvestigation;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

import static net.catenax.traceability.assets.infrastructure.adapters.jpa.asset.AssetEntity.ChildDescription;

@Component
public class PersistentAssetsRepository implements AssetRepository {

	private final JpaAssetsRepository assetsRepository;

	public PersistentAssetsRepository(JpaAssetsRepository assetsRepository) {
		this.assetsRepository = assetsRepository;
	}

	@Override
	public Asset getAssetById(String assetId) {
		return assetsRepository.findById(assetId)
			.map(this::toAsset)
			.orElse(null);
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
	public List<Asset> getAssets() {
		return toAssets(assetsRepository.findAll());
	}

	@Override
	public Asset save(Asset asset) {
		return toAsset(assetsRepository.save(toEntity(asset)));
	}

	@Override
	public void startInvestigation(List<String> assetIds, String description) {
		assetsRepository.findByIdIn(assetIds).forEach(entity -> {
			entity.setPendingInvestigation(PendingInvestigation.newInvestigation(entity.getId(), description));
			assetsRepository.save(entity);
		});
	}

	@Override
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

	@Override
	public void clean() {
		assetsRepository.deleteAll();
	}

	@Override
	public long countPendingInvestigations() {
		return assetsRepository.countByPendingInvestigationStatus(InvestigationStatus.PENDING);
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
			asset.getQualityType()
		);
	}

	private Asset toAsset(AssetEntity entity) {
		return new Asset(
			entity.getId(), entity.getIdShort(),
			entity.getNameAtManufacturer(),
			entity.getManufacturerPartId(),
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
			entity.getQualityType()
		);
	}
}
