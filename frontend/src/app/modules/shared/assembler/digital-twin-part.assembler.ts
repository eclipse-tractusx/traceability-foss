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

import { Pagination } from '@core/model/pagination.model';
import { DigitalTwinPartDetailResponse, DigitalTwinPartResponse, DigitalTwinPartsResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';
import { TableHeaderSort } from '@shared/components/table/table.model';

export class DigitalTwinPartAssembler {
  public static readonly localToApiMapping = new Map<string, string>([
    ['aasId', 'aasId'],
    ['globalAssetId', 'globalAssetId'],
    ['bpn', 'bpn'],
    ['digitalTwinType', 'digitalTwinType'],
    ['aasExpirationDate', 'aasExpirationDate'],
    ['assetExpirationDate', 'assetExpirationDate'],
  ]);

  public static mapSortToApiSort(sort: TableHeaderSort): string {
    if (!sort) return null;

    const [localKey, direction] = sort;
    const apiKey = this.localToApiMapping.get(localKey) ?? localKey;

    if (!apiKey || !direction) return null;

    return `${apiKey},${direction}`;
  }

  public static assembleParts(response: DigitalTwinPartsResponse): Pagination<DigitalTwinPartResponse> {
    return {
      page: response.page,
      pageCount: response.pageCount,
      pageSize: response.pageSize,
      totalItems: response.totalItems,
      content: response.content.map(part => ({
        ...part,
        aasExpirationDate: this.formatDate(part.aasExpirationDate as any),
        assetExpirationDate: this.formatDate(part.assetExpirationDate as any),
      }))
    };
  }


  private static formatDate(value: number[]): Date | null {
    if (!value || value.length < 6) return null;
    return new Date(value[0], value[1] - 1, value[2], value[3], value[4], value[5]);
  }

  private static formatDateString(date: any): string {
    if (!date) return 'N/A';
    let parsed: Date;
    if (Array.isArray(date)) {
      parsed = new Date(...(date as [number, number, number, number, number, number]));
    } else {
      parsed = new Date(date);
    }

    if (isNaN(parsed.getTime())) return 'Invalid Date';

    const year = parsed.getFullYear();
    const month = String(parsed.getMonth() + 1).padStart(2, '0');
    const day = String(parsed.getDate()).padStart(2, '0');
    const hour = String(parsed.getHours()).padStart(2, '0');
    const minute = String(parsed.getMinutes()).padStart(2, '0');
    const second = String(parsed.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
  }


  public static assembleDetailView(detail: DigitalTwinPartDetailResponse): {
    registryData: Record<string, string>,
    irsData: Record<string, string>
  } {
    return {
      registryData: {
        'aasId': detail.aasId,
        'ttl': detail.aasTTL?.toString() ?? 'N/A',
        'nextLookup': this.formatDateString(detail.nextLookup),
        'lastUpdatedAt': this.formatDateString(detail.aasExpirationDate),
        'createdAt': this.formatDateString(detail.aasExpirationDate),
        'actor': detail.actor,
      },
      irsData: {
        'globalAssetId': detail.globalAssetId,
        'assetTTl': detail.assetTTL?.toString() ?? 'N/A',
        'nextSync': this.formatDateString(detail.nextSync),
        'assetExpirationDate': this.formatDateString(detail.assetExpirationDate),
        'actor': detail.actor,
      }
    };
  }
}
