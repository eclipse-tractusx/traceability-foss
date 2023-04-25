package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Owner;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AssetTestDataFactory {

    public static Asset createAssetTestData() {

        List<Asset.Descriptions> childDescriptions = new ArrayList<>();
        childDescriptions.add(new Asset.Descriptions("child1", "desc1"));
        childDescriptions.add(new Asset.Descriptions("child2", "desc2"));

        List<Asset.Descriptions> parentDescriptions = new ArrayList<>();
        parentDescriptions.add(new Asset.Descriptions("parent1", "desc1"));
        parentDescriptions.add(new Asset.Descriptions("parent2", "desc2"));

        Instant manufacturingDate = Instant.now();

        return new Asset(
                "1",
                "1234",
                "Asset Name",
                "part123",
                "instance123",
                "manu456",
                "batch1",
                "Manufacturer Name",
                "Customer Name",
                "customer123",
                manufacturingDate,
                "US",
                Owner.OWN,
                childDescriptions,
                parentDescriptions,
                false,
                QualityType.CRITICAL,
                "van123"
        );
    }
}
