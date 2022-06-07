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

import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { Router } from '@angular/router';
import { realm } from '@core/api/api.service.properties';
import { PartsAssembler } from '@page/parts/core/parts.assembler';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { FormatDatePipe, State, View } from '@shared';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-part-detail',
  templateUrl: './part-detail.component.html',
  styleUrls: ['./part-detail.component.scss'],
})
export class PartDetailComponent implements AfterViewInit {
  @ViewChild('sidenav') sidenav: MatSidenav;
  public partDetails$: Observable<View<Part>>;
  public relationPartDetails$: Observable<View<Part>>;
  public manufacturerDetails$: Observable<View<Part>>;
  public customerDetails$: Observable<View<Part>>;

  private readonly _isOpen$: State<boolean> = new State<boolean>(false);

  constructor(private readonly partsFacade: PartsFacade, formatDate: FormatDatePipe, private readonly router: Router) {
    this.relationPartDetails$ = this.partsFacade.selectedPart$;
    this.partDetails$ = this.partsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForView(),
      map(partView => {
        if (!partView.data) {
          return partView;
        }

        partView.data.productionDate = formatDate.transform(partView.data.productionDate) as any;
        return partView;
      }),
    );

    this.manufacturerDetails$ = this.partsFacade.selectedPart$.pipe(PartsAssembler.mapPartForManufacturerView());
    this.customerDetails$ = this.partsFacade.selectedPart$.pipe(PartsAssembler.mapPartForCustomerView());
  }

  ngAfterViewInit(): void {
    this.partsFacade.selectedPart$.subscribe(detailView => {
      if (!detailView.data) {
        return;
      }

      setTimeout(() => void this.sidenav.open());
    });
  }

  get isOpen$(): Observable<boolean> {
    return this._isOpen$.observable;
  }

  set isOpen(openState: boolean) {
    this._isOpen$.update(openState);

    if (!openState) {
      this.partsFacade.selectedPart = null;
    }
  }

  public openRelationPage(part: Part): void {
    void this.router.navigate([`${realm}/parts/relations/${part.id}`]);
  }
}
