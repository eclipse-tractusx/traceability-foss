package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel;


import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Shell;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                .ifPresent(detailAspectModel -> assetBase.setDetailAspectModels(List.of(detailAspectModel)));
    }

    public static void enrichManufacturingInformation(IRSResponse irsResponse, Map<String, String> bpnMap, AssetBase assetBase) {
        if (assetBase.getManufacturerId() == null && assetBase.getId().equals(irsResponse.jobStatus().globalAssetId())) {
            String bpn = irsResponse.jobStatus().parameter().bpn();
            assetBase.setManufacturerId(bpn);
            assetBase.setManufacturerName(bpnMap.get(bpn));
        } else {
            String bpnName = bpnMap.get(assetBase.getManufacturerId());
            if (bpnName != null) {
                assetBase.setManufacturerName(bpnName);
            }
        }
    }
}
