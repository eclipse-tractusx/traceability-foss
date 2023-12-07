/********************************************************************************
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

import { PartResponse } from '@page/parts/model/parts.model';

export const mockPartResponses4Essx = [
  {
    "id": "48de0eae-3088-4b2f-882e-ee8ec537c41b",
    // "catenaxSiteId": "86f69643-3b90-4e34-90bf-789edcf40e7e",
    "manufacturerPartId": "7A047C7-01",
    "nameAtManufacturer": "N Tier A CathodeMaterial",
    "classification": "product",
    // "validityPeriodFrom": "2013-04-11 05:30:04+00",
    // "validityPeriodTo": "2025-04-23 19:59:03+00"
  },
  {
    "id": "a3f68c7f-a82d-4d71-913b-fdecf4d284b0",
    // "catenaxSiteId": "2c57b0e9-a653-411d-bdcd-64787e9fd3a7",
    "manufacturerPartId": "32494586-73",
    "nameAtManufacturer": "Tier A Gearbox",
    "classification": "product",
    // "validityPeriodFrom": "2013-04-01 00:18:40+00",
    // "validityPeriodTo": "2025-07-06 08:58:34+00"
  },
  {
    "id": "3acf87c5-59e2-4bf2-b636-6df2e9237c6d",
    // "catenaxSiteId": "bee5614f-9e46-4c98-9209-61a6f2b2a7fc",
    "manufacturerPartId": "6740244-02",
    "nameAtManufacturer": "Sub Tier A Sensor",
    "classification": "product",
    // "validityPeriodFrom": "2015-03-15 17:25:20+00",
    // "validityPeriodTo": "2027-12-12 17:07:52+00"
  }
] as PartResponse[];

export const mockPartResponses4Ess = [
  {
    "id": "0733946c-59c6-41ae-9570-cb43a6e4c79e",
    "idShort": "c79e",
    "semanticModelId": "NO-341449848714937445621543",
    "businessPartner": "BPNL00000003AYRE",
    "manufacturerName": "BMW AG",
    "nameAtManufacturer": "Vehicle Model A",
    "manufacturerPartId": "ZX-55",
    "owner": "OWN",
    "childRelations": [],
    "parentRelations": [],
    "activeAlert": false,
    "underInvestigation": false,
    "qualityType": "Ok",
    "van": "--",
    "semanticDataModel": "SERIALPART",
    "classification": "product",
    "detailAspectModels": [
      {
        "type": "AS_PLANNED",
        "data": {
          "validityPeriodFrom": "2019-04-04T03:19:03.000Z",
          "functionValidUntil": "2024-12-29T10:25:12.000Z"
        }
      },
      {
        "type": "PART_SITE_INFORMATION_AS_PLANNED",
        "data": {
          "functionValidUntil": "2025-02-08T04:30:48.000Z",
          "function": "production",
          "functionValidFrom": "2019-08-21T02:10:36.000Z",
          "catenaXSiteId": "0733946c-59c6-41ae-9570-cb43a6e4c79e"
        }
      }
    ]
  }
] as PartResponse[];

export const mockPartResponses4Ess_orig = [
  {
    "id": "48de0eae-3088-4b2f-882e-ee8ec537c41b",
    "idShort": "c41b",
    "semanticModelId": "NO-341449848714937445621543",
    "businessPartner": "BPNL00000003B6LU",
    "manufacturerName": "CathodeGmbH",
    "nameAtManufacturer": "N Tier A CathodeMaterial",
    "manufacturerPartId": "7A047C7-01",
    "owner": "OWN",
    "childRelations": [],
    "parentRelations": [],
    "activeAlert": false,
    "underInvestigation": false,
    "qualityType": "Ok",
    "van": "--",
    "semanticDataModel": "SERIALPART",
    "classification": "product",
    "detailAspectModels": [
      {
        "type": "AS_PLANNED",
        "data": {
          "validityPeriodFrom": "2013-04-11 05:30:04+00",
          "functionValidUntil": "2025-04-23 19:59:03+00"
        }
      },
      {
        "type": "PART_SITE_INFORMATION_AS_PLANNED",
        "data": {
          "functionValidUntil": "Sat Feb 08 03:30:48 GMT 2025",
          "function": "production",
          "functionValidFrom": "Wed Aug 21 00:10:36 GMT 2019",
          "catenaXSiteId": "86f69643-3b90-4e34-90bf-789edcf40e7e"
        }
      }
    ]
  },
  {
    "id": "a3f68c7f-a82d-4d71-913b-fdecf4d284b0",
    "idShort": "84b0",
    "semanticModelId": "NO-341449848714937445621543",
    "businessPartner": "BPNL00000003B6LU",
    "manufacturerName": "CathodeGmbH",
    "nameAtManufacturer": "Tier A Gearbox",
    "manufacturerPartId": "32494586-73",
    "owner": "OWN",
    "childRelations": [],
    "parentRelations": [],
    "activeAlert": false,
    "underInvestigation": false,
    "qualityType": "Ok",
    "van": "--",
    "semanticDataModel": "SERIALPART",
    "classification": "product",
    "detailAspectModels": [
      {
        "type": "AS_PLANNED",
        "data": {
          "validityPeriodFrom": "2013-04-01 00:18:40+00",
          "functionValidUntil": "2025-07-06 08:58:34+00"
        }
      },
      {
        "type": "PART_SITE_INFORMATION_AS_PLANNED",
        "data": {
          "functionValidUntil": "Sat Feb 08 03:30:48 GMT 2025",
          "function": "production",
          "functionValidFrom": "Wed Aug 21 00:10:36 GMT 2019",
          "catenaXSiteId": "2c57b0e9-a653-411d-bdcd-64787e9fd3a7"
        }
      }
    ]
  },
  {
    "id": "3acf87c5-59e2-4bf2-b636-6df2e9237c6d",
    "idShort": "7c6d",
    "semanticModelId": "NO-341449848714937445621543",
    "businessPartner": "BPNL00000003B6LU",
    "manufacturerName": "CathodeGmbH",
    "nameAtManufacturer": "Sub Tier A Sensor",
    "manufacturerPartId": "6740244-02",
    "owner": "OWN",
    "childRelations": [],
    "parentRelations": [],
    "activeAlert": false,
    "underInvestigation": false,
    "qualityType": "Ok",
    "van": "--",
    "semanticDataModel": "SERIALPART",
    "classification": "product",
    "detailAspectModels": [
      {
        "type": "AS_PLANNED",
        "data": {
          "validityPeriodFrom": "2015-03-15 17:25:20+00",
          "functionValidUntil": "2027-12-12 17:07:52+00"
        }
      },
      {
        "type": "PART_SITE_INFORMATION_AS_PLANNED",
        "data": {
          "functionValidUntil": "Sat Feb 08 03:30:48 GMT 2025",
          "function": "production",
          "functionValidFrom": "Wed Aug 21 00:10:36 GMT 2019",
          "catenaXSiteId": "bee5614f-9e46-4c98-9209-61a6f2b2a7fc"
        }
      }
    ]
  }
] as PartResponse[];
