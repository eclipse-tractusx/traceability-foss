/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

package assets.request;

import common.FilterAttribute;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AssetFilter {
    private final FilterAttribute id;
    private final FilterAttribute idShort;
    private final FilterAttribute name;
    private final FilterAttribute manufacturerName;
    private final FilterAttribute businessPartner;
    private final FilterAttribute partId;
    private final FilterAttribute manufacturerPartId;
    private final FilterAttribute customerPartId;
    private final FilterAttribute contractAgreementId;
    private final FilterAttribute classification;
    private final FilterAttribute nameAtCustomer;
    private final FilterAttribute semanticModelId;
    private final FilterAttribute semanticDataModel;
    private final FilterAttribute manufacturingDate;
    private final FilterAttribute manufacturingCountry;
    private final FilterAttribute manufacturerId;
    private final FilterAttribute productType;
    private final FilterAttribute tractionBatteryCode;
    private final FilterAttribute owner;
    private final FilterAttribute qualityType;
    private final FilterAttribute nameAtManufacturer;
    private final FilterAttribute van;
    private final FilterAttribute sentQualityAlertIdsInStatusActive;
    private final FilterAttribute sentQualityInvestigationIdsInStatusActive;
    private final FilterAttribute receivedQualityAlertIdsInStatusActive;
    private final FilterAttribute receivedQualityInvestigationIdsInStatusActive;
    private final FilterAttribute importState;
    private final FilterAttribute importNote;
    private final FilterAttribute validityPeriodFrom;
    private final FilterAttribute validityPeriodTo;
    private final FilterAttribute functionValidUntil;
    private final FilterAttribute functionValidFrom;
    private final FilterAttribute function;
    private final FilterAttribute catenaxSiteId;
}
