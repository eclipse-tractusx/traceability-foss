package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Descriptions;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class AssetTestDataFactory {

    public static Asset createAssetTestData() {

        List<Descriptions> childDescriptions = new ArrayList<>();
        childDescriptions.add(new Descriptions("child1", "desc1"));
        childDescriptions.add(new Descriptions("child2", "desc2"));

        List<Descriptions> parentDescriptions = new ArrayList<>();
        parentDescriptions.add(new Descriptions("parent1", "desc1"));
        parentDescriptions.add(new Descriptions("parent2", "desc2"));

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
                false,
                QualityType.CRITICAL,
                "van123"
        );
    }
}
