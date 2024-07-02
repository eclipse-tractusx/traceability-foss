/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.submodel;

import org.eclipse.tractusx.irs.component.partsiteinformationasplanned.Site;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.model.aspect.DetailAspectDataPartSiteInformationAsPlanned;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectModel;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectType;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.mapping.asplanned.AsPlannedDetailMapper;
import org.eclipse.tractusx.traceability.generated.PartSiteInformationAsPlanned100Schema;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
public class PartSiteInformationAsPlannedMapper implements AsPlannedDetailMapper {
    @Override
    public List<DetailAspectModel> extractDetailAspectModel(IrsSubmodel irsSubmodel) {
        PartSiteInformationAsPlanned100Schema partSiteInformationAsPlanned = (PartSiteInformationAsPlanned100Schema) irsSubmodel.getPayload();
        List<Site> sites = partSiteInformationAsPlanned
                .getSites()
                .stream()
                .map(asPlannedSite -> new Site(ZonedDateTime.parse(asPlannedSite.getFunctionValidUntil()), asPlannedSite.getFunction().toString(), ZonedDateTime.parse(asPlannedSite.getFunctionValidFrom()), asPlannedSite.getCatenaXSiteId())).toList();

        return extractDetailAspectModelsPartSiteInformationAsPlanned(sites, partSiteInformationAsPlanned.getCatenaXId());
    }

    @Override
    public boolean validMapper(IrsSubmodel submodel) {
        return submodel.getPayload() instanceof PartSiteInformationAsPlanned100Schema;
    }

    private static List<DetailAspectModel> extractDetailAspectModelsPartSiteInformationAsPlanned(List<Site> sites, String globalAssetId) {
        List<DetailAspectModel> detailAspectModels = new ArrayList<>();
        emptyIfNull(sites).forEach(site -> {
            DetailAspectDataPartSiteInformationAsPlanned detailAspectDataPartSiteInformationAsPlanned = DetailAspectDataPartSiteInformationAsPlanned.builder()
                    .catenaXSiteId(site.catenaXsiteId())
                    .functionValidFrom(site.functionValidFrom().toOffsetDateTime())
                    .function(site.function())
                    .functionValidUntil(site.functionValidUntil().toOffsetDateTime())
                    .build();
            detailAspectModels.add(DetailAspectModel.builder().data(detailAspectDataPartSiteInformationAsPlanned).type(DetailAspectType.PART_SITE_INFORMATION_AS_PLANNED).globalAssetId(globalAssetId).build());
        });

        return detailAspectModels;
    }
}
