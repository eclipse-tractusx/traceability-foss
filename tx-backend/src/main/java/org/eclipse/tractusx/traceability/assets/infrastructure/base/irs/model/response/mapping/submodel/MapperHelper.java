package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Shell;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.AssetBaseMapperProvider;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.asbuilt.AsBuiltDetailMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.asplanned.AsPlannedDetailMapper;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@UtilityClass
@Slf4j
public class MapperHelper {

    public static Owner getOwner(AssetBase assetBase, IRSResponse irsResponse) {
        boolean isOwn = assetBase.getId().equals(irsResponse.jobStatus().globalAssetId());
        if (isOwn) {
            return Owner.OWN;
        } else if (irsResponse.jobStatus().parameter().direction().equalsIgnoreCase(Direction.DOWNWARD.name())) {
            return Owner.SUPPLIER;
        } else {
            return Owner.CUSTOMER;
        }
    }

    public static String getShortId(List<Shell> shells, String globalAssetId) {
        return shells.stream()
                .filter(shell -> shell.payload().idShort() != null)
                .map(shell -> Map.entry(shell.payload().globalAssetId(), shell.payload().idShort()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existingValue, newValue) -> existingValue
                )).get(globalAssetId);
    }

    public static String getContractAgreementId(List<Shell> shells, String globalAssetId) {
        return shells.stream()
                .filter(shell -> globalAssetId.equals(shell.payload().globalAssetId()))
                .map(Shell::contractAgreementId)
                .findFirst()
                .orElse(null);
    }

    public static OffsetDateTime getOffsetDateTime(String date) {
        try {
            return OffsetDateTime.parse(date);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(date).atOffset(ZoneOffset.UTC);
            } catch (Exception ex) {
                log.warn("Neither OffsetDateTime nor LocalDateTime could be created from string: {} Fallback to null", date);
                return null;
            }
        }
    }

    public static void enrichAssetBase(List<DetailAspectModel> detailAspectModels, AssetBase assetBase) {
        detailAspectModels.stream()
                .filter(detailAspectModel -> detailAspectModel.getGlobalAssetId().equals(assetBase.getId()))
                .findFirst()
                .ifPresent(detailAspectModel -> emptyIfNull(new ArrayList<>(assetBase.getDetailAspectModels())).add(detailAspectModel));
    }

    public static void enrichUpwardAndDownwardDescriptions(Map<String, List<Descriptions>> descriptionsMap, AssetBase assetBase) {
        List<Descriptions> upwardDescriptions = new ArrayList<>();
        List<Descriptions> downwardDescriptions = new ArrayList<>();

        List<Descriptions> descriptions = descriptionsMap.get(assetBase.getId());
        for (Descriptions description : emptyIfNull(descriptions)) {
            if (description.direction() == org.eclipse.tractusx.irs.component.enums.Direction.UPWARD) {
                upwardDescriptions.add(description);
            } else if (description.direction() == org.eclipse.tractusx.irs.component.enums.Direction.DOWNWARD) {
                downwardDescriptions.add(description);
            }
        }

        assetBase.setChildRelations(downwardDescriptions);
        assetBase.setParentRelations(upwardDescriptions);
    }


    @NotNull
    public List<DetailAspectModel> extractTractionBatteryCode(List<IrsSubmodel> irsSubmodels, String globalAssetId, AssetBaseMapperProvider assetBaseMapperProvider) {
        return irsSubmodels
                .stream()
                .flatMap(irsSubmodel -> {
                    Optional<AsBuiltDetailMapper> mapper = assetBaseMapperProvider.getAsBuiltDetailMapper(irsSubmodel);
                    return mapper.map(asBuiltDetailMapper -> asBuiltDetailMapper.extractDetailAspectModel(irsSubmodel, globalAssetId).stream()).orElseGet(Stream::empty);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @NotNull
    public List<DetailAspectModel> extractPartSiteInformationAsPlanned(List<IrsSubmodel> irsSubmodels, AssetBaseMapperProvider assetBaseMapperProvider) {
        return irsSubmodels
                .stream()
                .flatMap(irsSubmodel -> {
                    Optional<AsPlannedDetailMapper> mapper = assetBaseMapperProvider.getAsPlannedDetailMapper(irsSubmodel);
                    return mapper.map(asPlannedDetailMapper -> asPlannedDetailMapper.extractDetailAspectModel(irsSubmodel).stream()).orElseGet(Stream::empty);
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
