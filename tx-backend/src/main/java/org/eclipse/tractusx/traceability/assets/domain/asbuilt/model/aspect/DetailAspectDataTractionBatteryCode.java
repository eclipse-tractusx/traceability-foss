/********************************************************************************
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
package org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.aspect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.tractusx.traceability.assets.domain.base.model.aspect.DetailAspectData;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.semanticdatamodel.SemanticDataModel;

import java.util.List;
import java.util.Objects;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DetailAspectDataTractionBatteryCode extends SemanticDataModel implements DetailAspectData {
    String productType;
    String tractionBatteryCode;
    List<DetailAspectDataTractionBatteryCodeSubcomponent> subcomponents;

    @Builder
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailAspectDataTractionBatteryCodeSubcomponent {
        String productType;
        String tractionBatteryCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DetailAspectDataTractionBatteryCode that = (DetailAspectDataTractionBatteryCode) o;
        return Objects.equals(productType, that.productType) && Objects.equals(tractionBatteryCode, that.tractionBatteryCode) && Objects.equals(subcomponents, that.subcomponents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), productType, tractionBatteryCode, subcomponents);
    }
}
