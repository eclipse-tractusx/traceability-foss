/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Pagination, PaginationResponse } from '@core/model/pagination.model';
import { PaginationAssembler } from '@core/pagination/pagination.assembler';
import { Part, PartResponse, QualityType, SortableHeaders } from '@page/parts/model/parts.model';
import { View } from '@shared';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { OperatorFunction } from 'rxjs';
import { map } from 'rxjs/operators';

export class PartsAssembler {
  public static assemblePart(part: PartResponse): Part {
    if (!part) {
      return null;
    }

    return {
      id: part.id,
      name: part.nameAtManufacturer,
      manufacturer: part.manufacturerName,
      serialNumber: part.manufacturerPartId,
      partNumber: part.customerPartId,
      productionCountry: part.manufacturingCountry,
      nameAtCustomer: part.nameAtCustomer,
      customerPartId: part.customerPartId,
      qualityType: part.qualityType || QualityType.Ok,
      productionDate: new CalendarDateModel(part.manufacturingDate),
      children: part.childDescriptions.map(child => child.id),
    };
  }

  public static assembleParts(parts: PaginationResponse<PartResponse>): Pagination<Part> {
    if (!parts || !parts.content.length) {
      return null;
    }
    return PaginationAssembler.assemblePagination(parts, PartsAssembler.assemblePart);
  }

  public static filterPartForView(viewData: View<Part>): View<Part> {
    if (!viewData || !viewData.data) {
      return viewData;
    }
    const { name, productionDate, qualityType, serialNumber } = viewData.data;
    return { data: { name, productionDate, qualityType, serialNumber } as Part };
  }

  public static mapPartForView(): OperatorFunction<View<Part>, View<Part>> {
    return map(PartsAssembler.filterPartForView);
  }

  public static mapPartForManufacturerView(): OperatorFunction<View<Part>, View<Part>> {
    return map(viewData => {
      if (!viewData.data) {
        return viewData;
      }

      const { manufacturer, partNumber, serialNumber } = viewData.data;
      return { data: { manufacturer, partNumber, serialNumber } as Part };
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

    const localToApiMapping = new Map<SortableHeaders, string>([
      ['id', 'id'],
      ['name', 'nameAtManufacturer'],
      ['manufacturer', 'manufacturerName'],
      ['serialNumber', 'manufacturerPartId'],
      ['partNumber', 'customerPartId'],
      ['productionCountry', 'manufacturingCountry'],
      ['nameAtCustomer', 'nameAtCustomer'],
      ['customerPartId', 'customerPartId'],
      ['qualityType', 'qualityType'],
      ['productionDate', 'manufacturingDate'],
    ]);

    return `${localToApiMapping.get(sorting[0])},${sorting[1]}`;
  }
}
