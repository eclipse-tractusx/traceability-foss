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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect.DetailAspectDataTractionBatteryCode;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Relationship;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public record JobDetailResponse(
        JobStatus jobStatus,
        List<Shell> shells,
        List<SemanticDataModel> semanticDataModels,
        List<Relationship> relationships,
        Map<String, String> bpns
) {

    private static final String UNKNOWN_MANUFACTURER_NAME = "UNKNOWN_MANUFACTURER";
    private static final String JOB_STATUS_COMPLETED = "COMPLETED";
    private static final String JOB_STATUS_RUNNING = "RUNNING";

    @JsonCreator
    static JobDetailResponse of(
            @JsonProperty("job") JobStatus jobStatus,
            @JsonProperty("relationships") List<Relationship> relationships,
            @JsonProperty("shells") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Shell> shells,
            @JsonProperty("submodels") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Submodel> submodels,
            @JsonProperty("bpns") @JsonSetter(nulls = Nulls.AS_EMPTY) List<Bpn> bpns
    ) {
        Map<String, String> bpnsMap = bpns.stream()
                .map(bpn -> new Bpn(
                        bpn.manufacturerId(),
                        bpn.manufacturerName() == null ? UNKNOWN_MANUFACTURER_NAME : bpn.manufacturerName()
                ))
                .collect(Collectors.toMap(Bpn::manufacturerId, Bpn::manufacturerName));

        List<SemanticDataModel> semanticDataModels = submodels.stream()
                .filter(submodel -> submodel.getPayload() instanceof SemanticDataModel || submodel.getPayload() instanceof DetailAspectDataTractionBatteryCode)
                .map(submodel -> {
                    if (submodel.getPayload() instanceof DetailAspectDataTractionBatteryCode detailAspectDataTractionBatteryCode) {
                        detailAspectDataTractionBatteryCode.setAspectType(submodel.getAspectType());
                        return detailAspectDataTractionBatteryCode;
                    } else {
                        SemanticDataModel payload = (SemanticDataModel) submodel.getPayload();
                        payload.setAspectType(submodel.getAspectType());
                        payload.setIdentification(submodel.getIdentification());
                        return payload;
                    }
                }).toList();

        return new JobDetailResponse(
                jobStatus,
                shells,
                semanticDataModels,
                relationships,
                bpnsMap
        );
    }

    public boolean isRunning() {
        return JOB_STATUS_RUNNING.equals(jobStatus.state());
    }

    public boolean isCompleted() {
        return JOB_STATUS_COMPLETED.equals(jobStatus.state());
    }

}

record Bpn(
        String manufacturerId,
        String manufacturerName
) {
}
