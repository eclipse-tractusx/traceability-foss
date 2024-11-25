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

package org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.AssetCallbackRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.common.repository.CriteriaUtility;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractAgreement;
import org.eclipse.tractusx.traceability.contracts.domain.model.ContractType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Transactional
public class AssetAsBuiltRepositoryImpl implements AssetAsBuiltRepository, AssetCallbackRepository {

    private final JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AssetBase getAssetById(String assetId) {
        return jpaAssetAsBuiltRepository.findById(assetId)
                .map(AssetAsBuiltEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId)));
    }

    @Override
    public void deleteAssetById(String assetId) {
            jpaAssetAsBuiltRepository.deleteById(assetId);
    }

    @Override
    public boolean existsById(String assetId) {
        return jpaAssetAsBuiltRepository.existsById(assetId);
    }

    @Override
    public List<AssetBase> getAssetsById(List<String> assetIds) {
        return jpaAssetAsBuiltRepository.findByIdIn(assetIds).stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    @Override
    public AssetBase getAssetByChildId(String childId) {
        return jpaAssetAsBuiltRepository.findById(childId)
                .map(AssetAsBuiltEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
    }

    @Override
    public List<String> getFieldValues(String fieldName, String startWith, Integer resultLimit, Owner owner, List<String> inAssetIds) {
        return CriteriaUtility.getDistinctAssetFieldValues(fieldName, startWith, resultLimit, owner, inAssetIds, AssetAsBuiltEntity.class, entityManager);
    }

    @Override
    public List<AssetBase> getAssets() {
        return jpaAssetAsBuiltRepository.findAll().stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    @Override
    public AssetBase save(AssetBase asset) {
        enrichContractAgreementsAsBuilt(List.of(asset));
        return jpaAssetAsBuiltRepository.save(AssetAsBuiltEntity.from(asset)).toDomain();
    }

    private void enrichContractAgreementsAsBuilt(List<AssetBase> assets) {


        assets.forEach(assetBase -> {
            List<ContractAgreement> contractAgreements = new ArrayList<>();
            contractAgreements.add(ContractAgreement.toDomain(assetBase.getLatestContractAgreementId(), assetBase.getId(), ContractType.ASSET_AS_BUILT));
            Optional<AssetAsBuiltEntity> byId = jpaAssetAsBuiltRepository.findById(assetBase.getId());
            byId.ifPresent(assetAsBuiltEntity -> contractAgreements.addAll(ContractAgreement.fromAsBuiltEntityToContractAgreements(assetAsBuiltEntity.getContractAgreements())));
            assetBase.setContractAgreements(contractAgreements);
        });
    }

    @Override
    public List<AssetBase> saveAll(List<AssetBase> assets) {
        enrichContractAgreementsAsBuilt(assets);
        return jpaAssetAsBuiltRepository.saveAll(AssetAsBuiltEntity.fromList(assets)).stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    @Override
    public List<AssetBase> saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(List<AssetBase> assets) {
        if (Objects.isNull(assets)) {
            return List.of();
        }
        List<AssetAsBuiltEntity> toPersist = assets.stream().map(assetToPersist -> new AbstractMap.SimpleEntry<AssetBase, AssetBaseEntity>(assetToPersist, jpaAssetAsBuiltRepository.findById(assetToPersist.getId()).orElse(null)))
                .filter(this::entityIsTransientOrNotExistent)
                .map(entry -> {
                    if (entry.getValue() != null) {
                        entry.getKey().setImportNote(ImportNote.TRANSIENT_UPDATED);
                    }
                    return entry.getKey();
                })
                .map(AssetAsBuiltEntity::from).toList();

        return jpaAssetAsBuiltRepository.saveAll(toPersist).stream().map(AssetAsBuiltEntity::toDomain).toList();
    }

    private boolean entityIsTransientOrNotExistent(AbstractMap.SimpleEntry<AssetBase, AssetBaseEntity> assetBaseAssetBaseEntitySimpleEntry) {
        if (Objects.isNull(assetBaseAssetBaseEntitySimpleEntry.getValue())) {
            return true;
        }
        return assetBaseAssetBaseEntitySimpleEntry.getValue().getImportState() == ImportState.TRANSIENT;
    }

    @Override
    public List<AssetBase> findByImportStateIn(ImportState... importStates) {
        return jpaAssetAsBuiltRepository.findByImportStateIn(importStates).stream()
                .map(AssetAsBuiltEntity::toDomain).toList();
    }

    @Override
    public void updateImportStateAndNoteForAssets(ImportState importState, String importNote, List<String> assetIds) {
        List<AssetAsBuiltEntity> assets = jpaAssetAsBuiltRepository.findByIdIn(assetIds);
        assets.forEach(assetAsBuilt -> {
            assetAsBuilt.setImportState(importState);
            assetAsBuilt.setImportNote(importNote);
        });
        jpaAssetAsBuiltRepository.saveAll(assets);
    }

    @Override
    public List<AssetBase> findAll() {
        return jpaAssetAsBuiltRepository.findAll().stream()
                .map(AssetAsBuiltEntity::toDomain).toList();
    }

    @Override
    public Optional<AssetBase> findById(String assetId) {
        return jpaAssetAsBuiltRepository.findById(assetId)
                .map(AssetAsBuiltEntity::toDomain);
    }

    @Override
    public long countAssets() {
        return jpaAssetAsBuiltRepository.count();
    }

    @Override
    public long countAssetsByOwner(Owner owner) {
        return jpaAssetAsBuiltRepository.countAssetsByOwner(owner);
    }
}
