/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { Pagination } from '@core/model/pagination.model';
import { PartsState } from '@page/parts/core/parts.state';
import { Part } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartsService } from '@shared/service/parts.service';
import { merge, Observable, of, Subject, Subscription } from 'rxjs';
import { catchError, delay, map, takeUntil } from 'rxjs/operators';

@Injectable()
export class PartsFacade {
  private subjectList: Record<string, Subject<void>> = {};
  private partsSubscription: Subscription;

  constructor(private readonly partsService: PartsService, private readonly partsState: PartsState) {}

  public get parts$(): Observable<View<Pagination<Part>>> {
    return this.partsState.parts$;
  }

  public setParts(page = 0, pageSize = 5, sorting: TableHeaderSort = null): void {
    this.partsSubscription?.unsubscribe();
    this.partsSubscription = this.partsService.getParts(page, pageSize, sorting).subscribe({
      next: data => (this.partsState.parts = { data }),
      error: error => (this.partsState.parts = { error }),
    });
  }

  get selectedParts$(): Observable<Part[]> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.partsState.selectedParts$.pipe(delay(0));
  }

  set selectedParts(parts: Part[]) {
    this.partsState.selectedParts = parts;
  }

  public setSelectedParts(selectedPartIds: string[]): void {
    this.subjectList = selectedPartIds.reduce((list, id) => ({ ...list, [id]: new Subject() }), {});

    this.selectedParts = selectedPartIds.map(id => ({ id } as Part));
    const selectedPartsObservable = selectedPartIds.map(id => this.getSelectedPartData(id));

    merge(...selectedPartsObservable).subscribe(data => this.updateSelectedParts(data));
  }

  public removeSelectedPart(part: Part): void {
    if (Object.keys(this.subjectList).length) {
      this.subjectList[part.id]?.next();
    }

    this.selectedParts = this.partsState.selectedParts?.filter(({ id }) => id !== part.id);
  }

  public addItemToSelection(part: Part): void {
    this.selectedParts = [...this.partsState.selectedParts, part];
    if (part.name) return;

    // If the part hase no name, the complete part will be pulled from the BE.
    this.getSelectedPartData(part.id).subscribe(data => this.updateSelectedParts(data));
  }

  private updateSelectedParts(part: Part): void {
    this.selectedParts = this.partsState.selectedParts.map(_part => (_part.id === part.id ? part : _part));
  }

  private getSelectedPartData(id: string): Observable<Part> {
    return this.partsService.getPart(id).pipe(
      map(part => part || ({ id, error: true } as Part)),
      takeUntil(this.subjectList[id] || new Subject()),
      catchError(_ => of({ id, error: true } as Part)),
    );
  }
}
