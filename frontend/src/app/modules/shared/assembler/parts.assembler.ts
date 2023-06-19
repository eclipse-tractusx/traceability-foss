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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Pagination, PaginationResponse } from '@core/model/pagination.model';
import { PaginationAssembler } from '@core/pagination/pagination.assembler';
import { Part, PartResponse, QualityType } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { OperatorFunction } from 'rxjs';
import { map } from 'rxjs/operators';

export class PartsAssembler {
  public static assemblePart(part: PartResponse): Part {
    if (!part) {
      return null;
    }

    return {
      id: part.id,
      name: part.semanticModel.nameAtManufacturer,
      manufacturer: part.manufacturerName,
      semanticModelId: part.semanticModelId,
      partNumber: part.semanticModel.manufacturerPartId,
      productionCountry: part.semanticModel.manufacturingCountry,
      nameAtCustomer: part.semanticModel.nameAtCustomer,
      customerPartId: part.semanticModel.customerPartId,
      qualityType: part.qualityType || QualityType.Ok,
      productionDate: new CalendarDateModel(part.semanticModel.manufacturingDate) ,
      children: part.childRelations.map(child => child.id) || [],
      parents: part.parentRelations?.map(parent => parent.id) || [],
      activeInvestigation: part.underInvestigation || false,
      activeAlert: part.activeAlert || false,
      van: part.van || '--',
      semanticDataModel: part.semanticDataModel
    };
  }

  public static assembleOtherPart(part: PartResponse): Part {
    if (!part) {
      return null;
    }

    return { ...PartsAssembler.assemblePart(part), qualityType: part.qualityType };
  }

  public static assembleParts(parts: PaginationResponse<PartResponse>): Pagination<Part> {
    return PaginationAssembler.assemblePagination(PartsAssembler.assemblePart, parts);
  }

  public static assemblePartList(parts: PartResponse[]): Part[] {
    const partCopy = [...parts];
    return partCopy.map(part => PartsAssembler.assemblePart(part));
  }

  public static assembleOtherParts(parts: PaginationResponse<PartResponse>): Pagination<Part> {
    return PaginationAssembler.assemblePagination(PartsAssembler.assembleOtherPart, parts);
  }

  public static filterPartForView(viewData: View<Part>): View<Part> {
    if (!viewData?.data) {
      return viewData;
    }
    const { name, semanticDataModel, productionDate, semanticModelId } = viewData.data;
    return { data: { name, semanticDataModel, productionDate, semanticModelId } as Part };
  }

  public static mapPartForView(): OperatorFunction<View<Part>, View<Part>> {
    return map(PartsAssembler.filterPartForView);
  }

  public static mapPartForManufacturerView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData.data) {
        return viewData;
      }

      const { manufacturer, partNumber, semanticModelId, van } = viewData.data;
      return { data: { manufacturer, partNumber, semanticModelId, van } as Part };
    });
  }

  public static mapPartForCustomerView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData.data) {
        return viewData;
      }

      const { nameAtCustomer, customerPartId } = viewData.data;
      return { data: { nameAtCustomer, customerPartId } as Part };
    });
  }

  public static mapSortToApiSort(sorting: TableHeaderSort): string {
    if (!sorting) {
      return '';
    }



    const localToApiMapping = new Map<string, string>([
      ['id', 'id'],
      ['semanticDataModel', 'semanticDataModel'],
      ['name', 'nameAtManufacturer'],
      ['manufacturer', 'manufacturerName'],
      ['semanticModelId', 'manufacturerPartId'],
      ['partNumber', 'customerPartId'],
      ['productionCountry', 'manufacturingCountry'],
      ['nameAtCustomer', 'nameAtCustomer'],
      ['customerPartId', 'customerPartId'],
      ['qualityType', 'qualityType'],
      ['productionDate', 'manufacturingDate'],
    ]);

    return `${localToApiMapping.get(sorting[0]) || sorting},${sorting[1]}`;
  }
}
