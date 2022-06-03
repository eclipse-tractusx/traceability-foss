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

import { Part, PartResponse } from '@page/parts/model/parts.model';
import { View } from '@shared';
import { OperatorFunction } from 'rxjs';
import { map } from 'rxjs/operators';

export class PartsAssembler {
  public static assembleParts(parts: PartResponse[]): Part[] {
    if (!parts || !parts.length) {
      return null;
    }

    return parts.map(part => {
      const transformedPart = {} as Part;
      transformedPart.id = part.id;
      transformedPart.name = part.nameAtManufacturer;
      transformedPart.manufacturer = part.manufacturerName;
      transformedPart.serialNumber = part.manufacturerPartId;
      transformedPart.partNumber = part.customerPartId;
      transformedPart.productionCountry = part.manufacturingCountry;
      transformedPart.nameAtCustomer = part.nameAtCustomer;
      transformedPart.customerPartId = part.customerPartId;
      transformedPart.qualityType = 'high';
      transformedPart.productionDate = new Date(part.manufacturingDate);
      transformedPart.children = part.childDescriptions.map(child => child.id);

      return transformedPart;
    });
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
}
