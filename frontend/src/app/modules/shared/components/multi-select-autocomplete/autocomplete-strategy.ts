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
import { Injectable } from '@angular/core';
import { Owner } from '@page/parts/model/owner.enum';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { AlertsService } from '@shared/service/alerts.service';
import { InvestigationsService } from '@shared/service/investigations.service';
import { PartsService } from '@shared/service/parts.service';

export abstract class AutocompleteStrategy {
  abstract retrieveSuggestionValues(tableType: TableType, filterColumns: string, searchElement: string): any;
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

  retrieveSuggestionValues(tableType: TableType, filterColumns: string, searchElement: string): any {
    const tableOwner = getOwnerOfTable(tableType);
    const asBuilt = isAsBuilt(tableType);
    return this.partsService.getDistinctFilterValues(
      asBuilt,
      tableOwner,
      filterColumns,
      searchElement,
    );
  }
}

@Injectable({
  providedIn: 'any',
})
export class InvestigationStrategy extends AutocompleteStrategy {
  investigationsService: InvestigationsService;

  constructor(investigationsService: InvestigationsService) {
    super();
    this.investigationsService = investigationsService;
  }

  retrieveSuggestionValues(tableType: TableType, filterColumns: string, searchElement: string): any {
    const notificationChannel = channelOfNotification(tableType);
    return this.investigationsService.getDistinctFilterValues(
      notificationChannel,
      filterColumns,
      searchElement,
    );
  }
}

@Injectable({
  providedIn: 'any',
})
export class AlertStrategy extends AutocompleteStrategy {
  alertService: AlertsService;

  constructor(alertService: AlertsService) {
    super();
    this.alertService = alertService;
  }

  retrieveSuggestionValues(tableType: TableType, filterColumns: string, searchElement: string): any {
    const notificationChannel = channelOfNotification(tableType);
    return this.alertService.getDistinctFilterValues(
      notificationChannel,
      filterColumns,
      searchElement,
    );
  }
}

export const AutocompleteStrategyMap = new Map<TableType, any>([
  [ TableType.AS_BUILT_OWN, PartsStrategy ],
  [ TableType.AS_BUILT_SUPPLIER, PartsStrategy ],
  [ TableType.AS_BUILT_CUSTOMER, PartsStrategy ],
  [ TableType.AS_PLANNED_OWN, PartsStrategy ],
  [ TableType.AS_PLANNED_CUSTOMER, PartsStrategy ],
  [ TableType.AS_PLANNED_SUPPLIER, PartsStrategy ],
  [ TableType.RECEIVED_INVESTIGATION, InvestigationStrategy ],
  [ TableType.CREATED_INVESTIGATION, InvestigationStrategy ],
  [ TableType.RECEIVED_ALERT, AlertStrategy ],
  [ TableType.CREATED_ALERT, AlertStrategy ],

]);

export function getOwnerOfTable(tableType: TableType): Owner {
  if (tableType === TableType.AS_BUILT_OWN || tableType === TableType.AS_PLANNED_OWN) {
    return Owner.OWN;
  } else if (tableType === TableType.AS_BUILT_CUSTOMER || tableType === TableType.AS_PLANNED_CUSTOMER) {
    return Owner.CUSTOMER;
  } else if (tableType === TableType.AS_BUILT_SUPPLIER || tableType === TableType.AS_PLANNED_SUPPLIER) {
    return Owner.SUPPLIER;
  } else {
    return Owner.UNKNOWN;
  }
}

export function isAsBuilt(tableType: TableType): boolean {
  const isAsBuiltElement = [ TableType.AS_BUILT_SUPPLIER, TableType.AS_BUILT_OWN, TableType.AS_BUILT_CUSTOMER ];
  return isAsBuiltElement.includes(tableType);
}

export function channelOfNotification(tableType: TableType): string {
  if (tableType === TableType.CREATED_ALERT || tableType === TableType.CREATED_INVESTIGATION) {
    return 'SENDER';
  } else {
    return 'RECEIVER';
  }

}
