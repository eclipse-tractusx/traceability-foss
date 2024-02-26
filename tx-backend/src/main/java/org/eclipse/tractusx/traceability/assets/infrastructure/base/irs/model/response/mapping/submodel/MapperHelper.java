package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel;

import org.eclipse.tractusx.irs.component.Shell;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return getShortIds(shells).get(globalAssetId);
    }

    private static Map<String, String> getShortIds(List<Shell> shells) {
        return shells.stream()
                .map(shell -> Map.entry(shell.payload().getGlobalAssetId(), shell.payload().getIdShort()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existingValue, newValue) -> existingValue
                ));
    }
}
