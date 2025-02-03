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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Owner } from '@page/parts/model/owner.enum';
import { Part } from '@page/parts/model/parts.model';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { RequestStepperComponent } from '@shared/components/request-notification/request-stepper/request-stepper.component';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { Observable, Subject, Subscription } from 'rxjs';

@Component({
  selector: 'app-start-investigation',
  templateUrl: './start-investigation.component.html',
  styleUrls: ['./start-investigation.component.scss'],
})
export class StartInvestigationComponent {
  @Input()
  get part(): View<Part> {
    return this._part;
  }

  set part(part: View<Part>) {
    this._part = part;
    if (!this._part?.data) return;

    this.selectedChildPartsState.reset();
    this.childPartsState.reset();

    this.childPartListSubscription?.unsubscribe();
    this.childPartListSubscription = this.partDetailsFacade.getChildPartDetails(this._part.data).subscribe({
      next: data => {
        const otherChildren = data.filter((part) => part.owner !== Owner.OWN);
        this.childPartsState.update({ data: otherChildren });
      },
      error: error => this.childPartsState.update({ error }),
    });
  }

  @Output() submitted = new EventEmitter<void>();

  public readonly selectedChildParts$: Observable<Part[]>;
  public readonly childParts$: Observable<View<Part[]>>;

  public readonly tableConfig: TableConfig;
  public readonly childPartsLabel = this.staticIdService.generateId('StartInvestigationComponent');
  public readonly deselectPartTrigger$: Subject<Part[]> = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();

  private readonly selectedChildPartsState = new State<Part[]>([]);
  private readonly childPartsState = new State<View<Part[]>>({ loader: true });
  private childPartListSubscription: Subscription;

  private _part: View<Part>;

  constructor(
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
    public dialog: MatDialog,
  ) {
    this.childParts$ = this.childPartsState.observable;
    this.selectedChildParts$ = this.selectedChildPartsState.observable;

    const displayedColumns: string[] = ['select', 'nameAtManufacturer', 'semanticModelId'];
    const sortableColumns: Record<string, boolean> = {
      nameAtManufacturer: true,
      semanticModelId: true,
    };

    this.tableConfig = {
      displayedColumns,
      header: CreateHeaderFromColumns(displayedColumns, 'table.column'),
      sortableColumns: sortableColumns,
      hasPagination: false,
    };
  }

  public removeChildPartFromSelection(part: Part): void {
    this.selectedChildPartsState.update([...this.selectedChildPartsState.snapshot.filter(c => c.id !== part.id)]);
    this.deselectPartTrigger$.next([part]);
  }

  public addChildPartToSelection(part: Part): void {
    this.selectedChildPartsState.update([...this.selectedChildPartsState.snapshot, part]);
    this.addPartTrigger$.next(part);
  }

  public clearSelectedChildParts(): void {
    this.selectedChildPartsState.reset();
  }

  public onMultiSelect(parts: unknown[]): void {
    this.selectedChildPartsState.update(parts as Part[]);
  }

  public onChildPartsSort({ sorting }: TableEventConfig) {
    const [name, direction] = sorting || ['', ''];

    const data = this.partDetailsFacade.sortChildParts(this.childPartsState.snapshot, name, direction);
    this.childPartsState.update({ data });
  }

  public openInvestigationDialog(): void {
    const dialogRef = this.dialog.open(RequestStepperComponent, {
      autoFocus: false,
      data: {
        selectedItems: this.selectedChildPartsState.snapshot,
        context: RequestContext.REQUEST_INVESTIGATION,
        tabIndex: 1,
        fromExternal: true,
      },
    });

    dialogRef?.componentInstance.deselectPart.subscribe(this.removeChildPartFromSelection);
    if (dialogRef?.afterClosed) {
      dialogRef.afterClosed().subscribe((_part: Part) => {
        dialogRef.componentInstance.deselectPart.unsubscribe();
      });
    }
  }
}
