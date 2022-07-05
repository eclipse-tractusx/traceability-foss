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
import { PartsService } from '@page/parts/core/parts.service';
import { PartsState } from '@page/parts/core/parts.state';
import { Part, QualityType } from '@page/parts/model/parts.model';
import { LoadedElementsFacade } from '@page/parts/relations/core/loaded-elements.facade';
import { RelationsAssembler } from '@page/parts/relations/core/relations.assembler';
import { View } from '@shared';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { Observable, of } from 'rxjs';
import { catchError, delay, tap } from 'rxjs/operators';

@Injectable()
export class PartsFacade {
  constructor(
    private readonly partsService: PartsService,
    private readonly partsState: PartsState,
    private readonly loadedElementsFacade: LoadedElementsFacade,
  ) {}

  get selectedPart$(): Observable<View<Part>> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.partsState.selectedPart$.pipe(delay(0));
  }

  set selectedPart(part: Part) {
    this.partsState.selectedPart = { data: part };
  }

  get selectedPart(): Part {
    return this.partsState.selectedPart?.data;
  }

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

  public setPart(id: string): Observable<View<Part>> {
    return this.partsService.getPart(id).pipe(
      tap((part: Part) => {
        this.partsState.selectedPart = { data: part };
      }),
      catchError(error => {
        this.partsState.selectedPart = { error };
        return of(error);
      }),
    );
  }

  public updateQualityType(qualityType: QualityType): Observable<Part> {
    const part = { ...this.selectedPart, qualityType };
    this.loadedElementsFacade.addLoadedElement(RelationsAssembler.assemblePartForRelation(part));

    return this.partsService.patchPart(part);
  }
}
