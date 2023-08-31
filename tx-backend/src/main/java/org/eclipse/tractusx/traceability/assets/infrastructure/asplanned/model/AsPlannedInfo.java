package org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model;

import lombok.Builder;
import lombok.Getter;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;

import java.util.List;
import java.util.Optional;

@Builder
@Getter
public class AsPlannedInfo {
    private String functionValidUntil;
    private String functionValidFrom;
    private String function;
    private String validityPeriodFrom;
    private String validityPeriodTo;

    public static AsPlannedInfo from(List<DetailAspectModel> detailAspectModels) {
        Optional<DetailAspectModel> asPlannedInfo = detailAspectModels
                .stream()
                .filter(detailAspectModel -> detailAspectModel.getType().equals(DetailAspectType.AS_PLANNED))
                .findFirst();

        String validityPeriodFrom = asPlannedInfo.map(detailAspectModel -> (DetailAspectDataAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned::getValidityPeriodFrom)
                .orElse("");

        String validityPeriodTo = asPlannedInfo.map(detailAspectModel -> (DetailAspectDataAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataAsPlanned::getValidityPeriodTo)
                .orElse("");

        Optional<DetailAspectModel> partSiteInfo = detailAspectModels
                .stream()
                .filter(detailAspectModel -> detailAspectModel.getType().equals(DetailAspectType.AS_PLANNED))
                .findFirst();

        String function = partSiteInfo.map(detailAspectModel -> (DetailAspectDataPartSiteInformationAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned::getFunction)
                .orElse("");

        String functionValidUntil = partSiteInfo.map(detailAspectModel -> (DetailAspectDataPartSiteInformationAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned::getFunctionValidUntil)
                .orElse("");

        String functionValidFrom = partSiteInfo.map(detailAspectModel -> (DetailAspectDataPartSiteInformationAsPlanned) detailAspectModel.getData())
                .map(org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned::getFunctionValidFrom)
                .orElse("");

        return AsPlannedInfo.builder()
                .functionValidUntil(functionValidUntil)
                .functionValidFrom(functionValidFrom)
                .function(function)
                .validityPeriodFrom(validityPeriodFrom)
                .validityPeriodTo(validityPeriodTo)
                .build();
    }
}
