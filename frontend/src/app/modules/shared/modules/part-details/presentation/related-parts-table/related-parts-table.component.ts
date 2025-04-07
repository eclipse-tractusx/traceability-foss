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
import { Router } from '@angular/router';
import { Pagination } from '@core/model/pagination.model';
import { SharedPartService } from '@page/notifications/detail/edit/shared-part.service';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part } from '@page/parts/model/parts.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import {
  CreateHeaderFromColumns,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { NotificationType } from '@shared/model/notification.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { Observable, Subject, Subscription } from 'rxjs';
import { RoleService } from '@core/user/role.service';
import { FormControl, FormGroup } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';

@Component({
  selector: 'app-related-parts-table',
  templateUrl: './related-parts-table.component.html',
  styleUrls: ['./related-parts-table.component.scss'],
})
export class RelatedPartsTableComponent {
  
  @Input() public showDetailTable = true;
  @Output() submitted = new EventEmitter<void>();
  @Output() tableConfigChanged = new EventEmitter<TableEventConfig>();

  public readonly selectedParts$: Observable<Part[]>;
  public readonly childParts$: Observable<View<Part[]>>;
  public readonly parentParts$: Observable<View<Part[]>>;
  public tableConfig: TableConfig;
  public filterFormGroup = new FormGroup({});
  public readonly relatedPartsLabel = this.staticIdService.generateId('RelatedPartsTableComponent');
  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();
  public multiSortList: TableHeaderSort[] = [];

  private _part: View<Part>;
  private readonly selectedPartsState = new State<Part[]>([]);
  private readonly childPartsState = new State<View<Part[]>>({ loader: true });
  private readonly parentPartsState = new State<View<Part[]>>({ loader: true });
  private childPartListSubscription: Subscription;
  private parentPartListSubscription: Subscription;
  public childPaginationObject: Pagination<Part>;
  public parentPaginationObject: Pagination<Part>;
  protected readonly NotificationType = NotificationType;
  protected readonly MainAspectType = MainAspectType;
  
  protected readonly TableType = TableType;
  component: { page: number; pageSize: number };

  constructor(
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
    private readonly sharedPartService: SharedPartService,
    private readonly router: Router,
    public roleService: RoleService,
  ) {
    this.childParts$ = this.childPartsState.observable;
    this.parentParts$ = this.parentPartsState.observable;
    this.selectedParts$ = this.selectedPartsState.observable;
  }

  @Input()
  get part(): View<Part> {
    return this._part;
  }

  set part(part: View<Part>) {
    this._part = part;
    if (!this._part?.data) return;

    this.selectedPartsState.reset();
    this.childPartsState.reset();
    this.parentPartsState.reset();

    this.childPartListSubscription?.unsubscribe();
    this.childPartListSubscription = this.partDetailsFacade.getChildPartDetails(this._part.data).subscribe({
      next: data => {
        this.childPartsState.update({ data });
        this.childPaginationObject = this.updateSortedPagination(data, 0, 10);
        this.setupTableConfig(data);
      },
      error: error => this.childPartsState.update({ error }),
    });

    this.parentPartListSubscription?.unsubscribe();
    this.parentPartListSubscription = this.partDetailsFacade.getParentPartDetails(this._part.data).subscribe({
      next: data => {
        this.parentPartsState.update({ data });
        this.parentPaginationObject = this.updateSortedPagination(data, 0, 10);
      },
      error: error => this.parentPartsState.update({ error }),
    });
  }

  public onMultiSelect(parts: Part[]): void {
    this.selectedPartsState.update(parts);
  }

  public onChildPartsSort(event: TableEventConfig): void {
    const [field, direction] = event.sorting || ['', ''];
    const sorted = this.partDetailsFacade.sortChildParts(this.childPartsState.snapshot, field, direction);
    this.childPartsState.update({ data: sorted });
    this.childPaginationObject = this.updateSortedPagination(sorted, 0, this.childPaginationObject.pageSize);
  }

  public onParentPartsSort(event: TableEventConfig): void {
    const [field, direction] = event.sorting || ['', ''];
    const sorted = this.partDetailsFacade.sortParentParts(this.parentPartsState.snapshot, field, direction);
    this.parentPartsState.update({ data: sorted });
    this.parentPaginationObject = this.updateSortedPagination(sorted, 0, this.parentPaginationObject.pageSize);
  }

  private updateSortedPagination(data: Part[], page: number, pageSize: number): Pagination<Part> {
    const totalItems = data.length;
    return {
      page,
      pageSize,
      totalItems,
      pageCount: Math.ceil(totalItems / pageSize),
      content: data.slice(page * pageSize, (page + 1) * pageSize),
    };
  }

  private setupTableConfig(data: Part[]): void {
    const displayedColumns = [
      'select',
      'manufacturerPartId',
      'nameAtManufacturer',
      'manufacturerName',
      'semanticModelId',
      'customerPartId',
    ];

    const sortableColumns = Object.fromEntries(displayedColumns.map(col => [col, true]));

    const header = CreateHeaderFromColumns(displayedColumns, 'table.column');

    const filterOptions = this.generateFilterOptions(data);
    const formGroup = new FormGroup({});
    for (const key of Object.keys(filterOptions)) {
      formGroup.addControl(key, new FormControl());
    }

    this.tableConfig = {
      displayedColumns,
      sortableColumns,
      header,
      hasPagination: false,
    };
  }

  private generateFilterOptions(data: Part[]): Record<string, string[]> {
    const filterOptions: Record<string, Set<string>> = {};

    data.forEach(part => {
      Object.entries(part).forEach(([key, value]) => {
        if (typeof value === 'string') {
          if (!filterOptions[key]) filterOptions[key] = new Set();
          filterOptions[key].add(value);
        }
      });
    });

    return Object.fromEntries(
      Object.entries(filterOptions).map(([key, set]) => [key, Array.from(set).sort((a, b) => a.localeCompare(b))]),
    );
  }

  public isPartsSelectionEmpty(parts: Part[] | null | undefined): boolean {
    return !parts || parts.length === 0;
  }

  public showIncidentButton(): boolean {
    const hasValidRole = this.roleService.isSupervisor() || this.roleService.isUser();
    const selectedParts = this.selectedPartsState.snapshot;
    const hasSelection = !this.isPartsSelectionEmpty(selectedParts);

    return hasValidRole && hasSelection;
  }

  navigateToNotificationCreationView() {
    this.sharedPartService.affectedParts = this.selectedPartsState.snapshot;
    this.router.navigate(['inbox/create'], { queryParams: { initialType: NotificationType.INVESTIGATION } });
  }

  public childPageChange(event: PageEvent): void {
    const { pageIndex, pageSize } = event;
    const allData = this.childPartsState.snapshot.data || [];
    this.childPaginationObject = this.updateSortedPagination(allData, pageIndex, pageSize);
  }
  
  public parentPageChange(event: PageEvent): void {
    const { pageIndex, pageSize } = event;
    const allData = this.parentPartsState.snapshot.data || [];
    this.parentPaginationObject = this.updateSortedPagination(allData, pageIndex, pageSize);
  }
}
