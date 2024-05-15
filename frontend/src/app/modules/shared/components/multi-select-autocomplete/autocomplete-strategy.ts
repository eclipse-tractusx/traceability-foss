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
import { Injectable } from '@angular/core';
import { AdminService } from '@page/admin/core/admin.service';
import { NotificationChannel, TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { NotificationService } from '@shared/service/notification.service';
import { PartsService } from '@shared/service/parts.service';
import { of } from 'rxjs';

export abstract class AutocompleteStrategy {
  abstract retrieveSuggestionValues(tableType: TableType, filterColumns: string, searchElement: string, inAssetIds?: string[]): any;
}

@Injectable({
  providedIn: 'any',
})
export class PartsStrategy extends AutocompleteStrategy {
  partsService: PartsService;

  constructor(partsService: PartsService) {
    super();
    this.partsService = partsService;
  }

  retrieveSuggestionValues(tableType: TableType, filterColumns: string, searchElement: string, inAssetIds?: string[]): any {
    const asBuilt = isAsBuilt(tableType);

    if(inAssetIds?.length < 1) {
      return of([]);
    }

    return this.partsService.getDistinctFilterValues(
      asBuilt,
      filterColumns,
      searchElement,
      inAssetIds,
    );
  }
}

@Injectable({
  providedIn: 'any',
})
export class NotificationStrategy extends AutocompleteStrategy {
  notificationService: NotificationService;

  constructor(notificationService: NotificationService) {
    super();
    this.notificationService = notificationService;
  }

  retrieveSuggestionValues(tableType: TableType, filterColumns: string, searchElement: string): any {
    const notificationChannel = channelOfNotification(tableType);
    return this.notificationService.getDistinctFilterValues(
      notificationChannel,
      filterColumns,
      searchElement,
    );
  }
}

@Injectable({
  providedIn: 'any',
})
export class ContractsStrategy extends AutocompleteStrategy {
  adminService: AdminService;

  constructor(adminService: AdminService) {
    super();
    this.adminService = adminService;
  }

  retrieveSuggestionValues(tableType: TableType, filterColumns: string, searchElement: string): any {
    return this.adminService.getDistinctFilterValues(
      filterColumns,
      searchElement,
    );
  }
}

export const AutocompleteStrategyMap = new Map<TableType, any>([
  [ TableType.AS_BUILT_OWN, PartsStrategy ],
  [ TableType.AS_PLANNED_OWN, PartsStrategy ],
  [ TableType.RECEIVED_NOTIFICATION, NotificationStrategy ],
  [ TableType.SENT_NOTIFICATION, NotificationStrategy ],
  [ TableType.CONTRACTS, ContractsStrategy ],
]);


export function isAsBuilt(tableType: TableType): boolean {
  const isAsBuiltElement = [ TableType.AS_BUILT_OWN ];
  return isAsBuiltElement.includes(tableType);
}

export function channelOfNotification(tableType: TableType): NotificationChannel {
  if (tableType === TableType.SENT_NOTIFICATION) {
    return NotificationChannel.SENDER;
  } else {
    return NotificationChannel.RECEIVER;
  }

}
