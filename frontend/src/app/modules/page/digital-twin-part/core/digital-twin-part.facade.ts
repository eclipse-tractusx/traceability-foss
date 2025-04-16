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

import { Injectable } from '@angular/core';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { Pagination } from '@core/model/pagination.model';
import { DigitalTwinPartState } from './digital-twin-part.state';
import { provideDataObject } from '@page/parts/core/parts.helper';
import { Observable, Subscription } from 'rxjs';
import { DigitalTwinPartService } from '@shared/service/digitalTwinPart.service';
import {  DigitalTwinPartResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';
import { DigitalTwinPartFilter } from '@shared/model/filter.model';
import { MatDialog } from '@angular/material/dialog';
import { ConfigurationDialogComponent } from '../presentation/configuration-dialog/configuration-dialog.component';
import { ConfigurationService } from '@shared/service/configuration.service';

@Injectable()
export class DigitalTwinPartFacade {
  private subscription: Subscription;

  constructor(
    private readonly dialog: MatDialog,
    private readonly digitalTwinPartService: DigitalTwinPartService,
    private readonly configurationService: ConfigurationService,
    private readonly state: DigitalTwinPartState
  ) {}

  public get digitalTwinParts$(): Observable<View<Pagination<DigitalTwinPartResponse>>> {
    return this.state.digitalTwinParts$;
  }

  public setDigitalTwinParts(
    page = 0,
    pageSize = 50,
    sorting: TableHeaderSort[] = [],
    filters?: DigitalTwinPartFilter[]
  ) {
    this.subscription?.unsubscribe();

    const mappedSort = this.mapSortToParams(sorting);

    this.subscription = this.digitalTwinPartService.getDigitalTwinParts(
      page,
      pageSize,
      mappedSort,
      filters
    ).subscribe({
      next: data => {
        this.state.digitalTwinParts = { data: provideDataObject(data) };
      },
      error: error => {
        this.state.digitalTwinParts = { error };
      },
    });
  }

  getDigitalTwinPartDetail(aasId: string) {
    return this.digitalTwinPartService.getDigitalTwinPartDetail(aasId);
  }


private mapSortToParams(sort: TableHeaderSort[]): TableHeaderSort[] {
  return sort.filter(([key, direction]) => key && (direction === 'asc' || direction === 'desc'));
}

  public unsubscribe() {
    this.subscription?.unsubscribe();
  }
  openConfigurationDialog(): void {
    this.configurationService.getLatestOrderConfiguration().subscribe(currentConfig => {
      this.dialog.open(ConfigurationDialogComponent, {
        panelClass: 'custom-dialog-container',
        width: '700px',
        height: '500px',
        data: {
          order: currentConfig
        }
      });
    });
  }


}
