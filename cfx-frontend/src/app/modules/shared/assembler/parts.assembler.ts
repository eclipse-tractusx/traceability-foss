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

import { Pagination, PaginationResponse } from '@core/model/pagination.model';
import { PaginationAssembler } from '@core/pagination/pagination.assembler';
import {
  AsBuiltAspectModel,
  AsPlannedAspectModel,
  PartSiteInformationAsPlanned,
  SemanticModel,
  TractionBatteryCode,
} from '@page/parts/model/aspectModels.model';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part, PartResponse, QualityType } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { OperatorFunction } from 'rxjs';
import { map } from 'rxjs/operators';

export class PartsAssembler {

  public static createSemanticModelFromPartResponse(partResponse: PartResponse): SemanticModel {
    let proplist = {};
    partResponse.detailAspectModels.forEach((detailAspectModel) => {
      proplist = { ...proplist, ...detailAspectModel.data };
    });

    return proplist;
  }

  public static assemblePart(partResponse: PartResponse, mainAspectType: MainAspectType): Part {
    if (!partResponse) {
      return null;
    }

    const createdSemanticModel = PartsAssembler.createSemanticModelFromPartResponse(partResponse);

    // Access the partId property

    const partId = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.partId;
    const customerPartId = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.customerPartId;
    const nameAtCustomer = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.nameAtCustomer;
    const manufacturingDate = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.manufacturingDate;
    const manufacturingCountry = (partResponse.detailAspectModels[0].data as AsBuiltAspectModel)?.manufacturingCountry;

    const validityPeriodFrom = (partResponse.detailAspectModels[0].data as AsPlannedAspectModel)?.validityPeriodFrom;
    const validityPeriodTo = (partResponse.detailAspectModels[0].data as AsPlannedAspectModel)?.validityPeriodTo;
    const catenaXSiteId = (partResponse.detailAspectModels[1]?.data as PartSiteInformationAsPlanned)?.catenaXSiteId;
    const psFunction = (partResponse.detailAspectModels[1]?.data as PartSiteInformationAsPlanned)?.function;
    const functionValidFrom = (partResponse.detailAspectModels[1]?.data as PartSiteInformationAsPlanned)?.functionValidFrom;
    const functionValidUntil = (partResponse.detailAspectModels[1]?.data as PartSiteInformationAsPlanned)?.functionValidUntil;

    // traction battery code
    const productType = (partResponse.detailAspectModels[1]?.data as TractionBatteryCode)?.productType;
    const tractionBatteryCode = (partResponse.detailAspectModels[1]?.data as TractionBatteryCode)?.tractionBatteryCode;
    const subcomponents = (partResponse.detailAspectModels[1]?.data as TractionBatteryCode)?.subcomponents;

    return {
      id: partResponse.id,
      idShort: partResponse.idShort,
      semanticModelId: partResponse.semanticModelId,
      manufacturerName: partResponse.manufacturerName,
      manufacturerPartId: partResponse.manufacturerPartId,
      nameAtManufacturer: partResponse.nameAtManufacturer,
      owner: partResponse.owner,
      businessPartner: partResponse.businessPartner,
      children: partResponse.childRelations.map(child => child.id) || [],
      parents: partResponse.parentRelations?.map(parent => parent.id) || [],
      qualityType: partResponse.qualityType || QualityType.Ok,
      van: partResponse.van || '--',
      semanticDataModel: partResponse.semanticDataModel,
      classification: partResponse.classification,
      semanticModel: createdSemanticModel,

      mainAspectType: mainAspectType,

      // as built
      partId: partId, // is partInstance, BatchId, jisNumber
      customerPartId: customerPartId,
      nameAtCustomer: nameAtCustomer,
      manufacturingDate: manufacturingDate === 'null' ? null : manufacturingDate,
      manufacturingCountry: manufacturingCountry,

      // tractionBatteryCode
      productType: productType,
      tractionBatteryCode: tractionBatteryCode,
      subcomponents: subcomponents,

      // as planned
      validityPeriodFrom: validityPeriodFrom === 'null' ? null : validityPeriodFrom,
      validityPeriodTo: validityPeriodTo === 'null' ? null : validityPeriodTo,

      //partSiteInformationAsPlanned
      catenaXSiteId: catenaXSiteId,
      psFunction: psFunction,
      functionValidFrom: functionValidFrom === 'null' ? null : functionValidFrom,
      functionValidUntil: functionValidUntil === 'null' ? null : functionValidUntil,

      // count of notifications
      sentActiveAlerts: partResponse.sentQualityAlertIdsInStatusActive,
      receivedActiveAlerts: partResponse.receivedQualityAlertIdsInStatusActive,
      sentActiveInvestigations: partResponse.sentQualityInvestigationIdsInStatusActive,
      receivedActiveInvestigations: partResponse.receivedQualityInvestigationIdsInStatusActive,

      importNote: partResponse.importNote,
      importState: partResponse.importState

    };
  }

  public static assembleOtherPart(partResponse: PartResponse, mainAspectType: MainAspectType): Part {
    if (!partResponse) {
      return null;
    }

    return { ...PartsAssembler.assemblePart(partResponse, mainAspectType), qualityType: partResponse.qualityType };
  }

  public static assembleParts(parts: PaginationResponse<PartResponse>, mainAspectType: MainAspectType): Pagination<Part> {
    return PaginationAssembler.assemblePagination(PartsAssembler.assemblePart, parts, mainAspectType);
  }

  public static assemblePartList(parts: PartResponse[], mainAspectType: MainAspectType): Part[] {
    const partCopy = [...parts];
    return partCopy.map(part => PartsAssembler.assemblePart(part, mainAspectType));
  }

  public static assembleOtherParts(parts: PaginationResponse<PartResponse>, mainAspectType: MainAspectType): Pagination<Part> {
    return PaginationAssembler.assemblePagination(PartsAssembler.assembleOtherPart, parts, mainAspectType);
  }

  public static filterPartForView(viewData: View<Part>): View<Part> {
    if (!viewData?.data) {
      return viewData;
    }

    const {
      semanticDataModel,
      semanticModelId,
      manufacturingDate,
      manufacturingCountry,
      classification,

    } = viewData.data;
    return {
      data: {
        semanticDataModel,
        semanticModelId,
        manufacturingDate,
        manufacturingCountry,
        classification,
      } as Part,
    };
  }

  public static mapPartForView(): OperatorFunction<View<Part>, View<Part>> {
    return map(PartsAssembler.filterPartForView);
  }

  public static mapPartForManufacturerView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData.data) {
        return viewData;
      }

      // exclude 'van' if is a partAsPlanned
      if (viewData.data?.mainAspectType === MainAspectType.AS_BUILT) {
        const {
          manufacturerName,
          manufacturerPartId,
          nameAtManufacturer,
          van,
        } = viewData.data;
        return { data: { manufacturerName, manufacturerPartId, nameAtManufacturer, van } as Part };
      } else {
        const {
          manufacturerName,
          manufacturerPartId,
          nameAtManufacturer,
        } = viewData.data;
        return { data: { manufacturerName, manufacturerPartId, nameAtManufacturer } as Part };
      }
    });
  }

  public static mapPartForCustomerOrPartSiteView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData.data) {
        return;
      }
      // if no customer data is available then return partSiteInformation
      if (!viewData.data?.nameAtCustomer && !viewData.data?.customerPartId && viewData.data?.functionValidFrom) {
        const { catenaXSiteId, psFunction, functionValidFrom, functionValidUntil } = viewData.data;
        return { data: { catenaXSiteId, psFunction, functionValidFrom, functionValidUntil } as Part };
      }

      const { nameAtCustomer, customerPartId } = viewData.data;
      return { data: { nameAtCustomer, customerPartId } as Part };
    });
  }

  public static mapPartForTractionBatteryCodeDetailsView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData?.data?.tractionBatteryCode) {
        return;
      }

      const { productType, tractionBatteryCode } = viewData.data;
      return { data: { productType, tractionBatteryCode } as Part };
    });
  }

  public static mapPartForAssetStateDetailsView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData?.data?.importState) {
        return;
      }

      const { importNote, importState } = viewData.data;
      return { data: { importNote, importState } as Part };
    });
  }

  public static mapPartForTractionBatteryCodeSubComponentsView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData?.data?.tractionBatteryCode || !viewData?.data?.subcomponents?.length) {
        return;
      }

      const { productType, tractionBatteryCode, subcomponents } = viewData.data;
      return { data: { productType, tractionBatteryCode, subcomponents } as Part };
    });
  }

  public static mapFieldNameToApi(fieldName: string) {
    if (!fieldName) {
      return;
    }

    if (this.localToApiMapping.has(fieldName)) {
      return this.localToApiMapping.get(fieldName);
    } else {
      return fieldName;
    }

  }

  public static mapSortToApiSort(sorting: TableHeaderSort): string {
    if (!sorting) {
      return '';
    }

    return `${this.localToApiMapping.get(sorting[0]) || sorting},${sorting[1]}`;
  }

  public static readonly localToApiMapping = new Map<string, string>([
    ['id', 'id'],
    ['idShort', 'idShort'],
    ['semanticModelId', 'semanticModelId'],
    ['manufacturer', 'manufacturerName'],
    ['manufacturerPartId', 'manufacturerPartId'],
    ['partId', 'manufacturerPartId'],
    ['nameAtManufacturer', 'nameAtManufacturer'],
    ['businessPartner', 'businessPartner'],
    ['name', 'nameAtManufacturer'],
    ['qualityType', 'qualityType'],
    ['van', 'van'],
    ['semanticDataModel', 'semanticDataModel'],
    ['classification', 'classification'],
    ['customerPartId', 'customerPartId'],
    ['nameAtCustomer', 'nameAtCustomer'],
    ['manufacturingDate', 'manufacturingDate'],
    ['manufacturingCountry', 'manufacturingCountry'],
    ['validityPeriodFrom', 'validityPeriodFrom'],
    ['validityPeriodTo', 'validityPeriodTo'],
    ['catenaXSiteId', 'catenaxSiteId'],
    ['psFunction', 'function'],
    ['functionValidFrom', 'functionValidFrom'],
    ['functionValidUntil', 'functionValidUntil'],
    ['sentActiveAlerts', 'sentQualityAlertIdsInStatusActive'],
    ['receivedActiveAlerts', 'receivedQualityAlertIdsInStatusActive'],
    ['sentActiveInvestigations', 'sentQualityInvestigationIdsInStatusActive'],
    ['receivedActiveInvestigations', 'receivedQualityInvestigationIdsInStatusActive'],
    ['importState', 'importState'],
    ['importNote', 'importNote']
  ]);
}
