/*********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.testdata;

import ess.request.EssRequest;
import ess.response.EssResponse;
import ess.response.EssStatus;
import java.util.List;
import java.util.UUID;
import org.eclipse.tractusx.traceability.bpdm.model.response.legal.LegalEntity;
import org.eclipse.tractusx.traceability.bpdm.model.response.site.SiteAddress;
import org.eclipse.tractusx.traceability.bpdm.model.response.site.SiteSearchContent;
import org.eclipse.tractusx.traceability.ess.domain.LogisticAddress;
import org.eclipse.tractusx.traceability.ess.domain.model.EssEntity;

public class EssTestDataFactory {

    public static final String BPNL_CATHODE = "BPNL00000003B6LU";
    public static final String BPNS_CATHODE = "BPNS00000003B6LU";
    public static final String BPNA_CATHODE = "BPNA00000003B6LU";

    public static final String PARTID_VECHICLE_MODEL_A = "0733946c-59c6-41ae-9570-cb43a6e4c79e";

    public static LogisticAddress createLogisticAddress() {
        return LogisticAddress.buildAddress("1", UUID.randomUUID().toString(), "11.11.2011 11:11:11",
                "11.11.2011 11:11:12", "BPNA00000003B6LU", "DE", "Testhausen",
                "Teststrasse", "1", "11111");
    }

    public static LegalEntity createLegalEntity() {
        return LegalEntity.buildEntity("Cathode Gmbh", "BPNL00000003B6LU");
    }

    public static SiteSearchContent createSiteSearchContent() {
        return SiteSearchContent.builderSiteSearchContent("BPNS00000003B6LU", "Cathodium", List.of(),
        "BPNL00000003B6LU", "11.11.2011 11:11:11", "11.11.2011 11:11:12",
                SiteAddress.buildAddress());
    }

    public static EssEntity createEssInvestigation() {
        return EssEntity.builder()
            .id(UUID.randomUUID().toString())
            .rowno("#1")
            .build();
    }

    public static EssRequest createEssRequest() {
        return EssRequest.builder()
            .partIds(List.of(EssTestDataFactory.PARTID_VECHICLE_MODEL_A))
            .bpns(EssTestDataFactory.BPNS_CATHODE)
            .build();
    }

    public static List<EssResponse> createEssResponse(EssEntity ess) {
        return List.of(EssResponse.builder()
                .ess_status(EssStatus.ON)
                .message("some message")
                .id(ess.getId())
                .partId(ess.getPartId())
                .company(ess.getCompanyName())
                .bpns(ess.getBpns())
                .jobId(ess.getJobId())
                .status(ess.getStatus())
                .impacted(ess.getImpacted())
                .response(ess.getResponse())
                .build());
    }

}
