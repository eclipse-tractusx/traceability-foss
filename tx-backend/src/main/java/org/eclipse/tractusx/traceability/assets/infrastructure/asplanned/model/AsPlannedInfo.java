package org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model;

import lombok.Builder;
import lombok.Getter;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
public class AsPlannedInfo {
    private OffsetDateTime functionValidUntil;
    private OffsetDateTime functionValidFrom;
    private String function;
    private OffsetDateTime validityPeriodFrom;
    private OffsetDateTime validityPeriodTo;
    private String catenaxSiteId;

    public static AsPlannedInfo from(List<DetailAspectModel> detailAspectModels) {
        Optional<DetailAspectModel> asPlannedInfo = detailAspectModels
                .stream()
                .filter(detailAspectModel -> detailAspectModel.getType().equals(DetailAspectType.AS_PLANNED))
                .findFirst();

        OffsetDateTime validityPeriodFrom = asPlannedInfo.map(detailAspectModel -> (DetailAspectDataAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned::getValidityPeriodFrom)
                .orElse(null);

        OffsetDateTime validityPeriodTo = asPlannedInfo.map(detailAspectModel -> (DetailAspectDataAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned::getValidityPeriodTo)
                .orElse(null);

        Optional<DetailAspectModel> partSiteInfo = detailAspectModels
                .stream()
                .filter(detailAspectModel -> detailAspectModel.getType().equals(DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED))
                .findFirst();

        String function = partSiteInfo.map(detailAspectModel -> (DetailAspectDataPartSiteInformationAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned::getFunction)
                .orElse("");

        OffsetDateTime functionValidUntil = partSiteInfo.map(detailAspectModel -> (DetailAspectDataPartSiteInformationAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned::getFunctionValidUntil)
                .orElse(null);

        OffsetDateTime functionValidFrom = partSiteInfo.map(detailAspectModel -> (DetailAspectDataPartSiteInformationAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned::getFunctionValidFrom)
                .orElse(null);

        String catenaxSiteId = partSiteInfo.map(detailAspectModel -> (DetailAspectDataPartSiteInformationAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned::getCatenaXSiteId)
                .orElse("");

        return AsPlannedInfo.builder()
                .functionValidUntil(functionValidUntil)
                .functionValidFrom(functionValidFrom)
                .function(function)
                .validityPeriodFrom(validityPeriodFrom)
                .validityPeriodTo(validityPeriodTo)
                .catenaxSiteId(catenaxSiteId)
                .build();
    }
}
