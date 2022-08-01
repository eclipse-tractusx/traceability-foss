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

import { Injectable } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { PartsState } from '@page/parts/core/parts.state';
import { Part } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartsService } from '@shared/service/parts.service';
import { merge, Observable, Subject } from 'rxjs';
import { delay, takeUntil, tap } from 'rxjs/operators';

@Injectable()
export class PartsFacade {
  private subjectList: Record<string, Subject<void>> = {};

  constructor(private readonly partsService: PartsService, private readonly partsState: PartsState) {}

  get parts$(): Observable<View<Pagination<Part>>> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.partsState.parts$.pipe(delay(0));
  }

  public setParts(page = 0, pageSize = 5, sorting: TableHeaderSort = null): void {
    this.partsService.getParts(page, pageSize, sorting).subscribe({
      next: (partsPage: Pagination<Part>) => {
        this.partsState.parts = { data: partsPage };
      },
      error: error => (this.partsState.parts = { error }),
    });
  }

  get selectedParts$(): Observable<View<Part[]>> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.partsState.selectedParts$.pipe(delay(0));
  }

  set selectedParts(parts: View<Part[]>) {
    this.partsState.selectedParts = parts;
  }

  public setSelectedParts(selectedPartIds: string[]): void {
    selectedPartIds.forEach(id => (this.subjectList[id] = new Subject()));
    const parts: Part[] = selectedPartIds.map(id => ({ id } as Part));
    this.partsState.selectedParts = { loader: true, data: parts };

    merge(...selectedPartIds.map(id => this.partsService.getPart(id).pipe(takeUntil(this.subjectList[id]))))
      .pipe(tap(_ => (this.subjectList = {})))
      .subscribe({
        next: data => this.updateSelectedParts(data),
        error: error => (this.partsState.selectedParts = { error }),
        complete: () => (this.partsState.selectedParts = { ...this.partsState.selectedParts, loader: false }),
      });
  }

  public removeSelectedPart(part: Part): void {
    if (Object.keys(this.subjectList).length) {
      this.subjectList[part.id].next();
    }

    this.selectedParts = {
      ...this.partsState.selectedParts,
      data: this.partsState.selectedParts.data?.filter(({ id }) => id !== part.id),
    };
  }

  private updateSelectedParts(part: Part) {
    const data = this.partsState.selectedParts.data.map(currentPart =>
      currentPart.id === part.id ? part : currentPart,
    );

    this.partsState.selectedParts = { ...this.partsState.selectedParts, data };
  }
}
