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
import { PartsState } from '@page/parts/core/parts.state';
import { Part, QualityType } from '@page/parts/model/parts.model';
import { View } from '@shared/model/view.model';
import { PartDetailsState } from '@shared/modules/part-details/core/partDetails.state';
import { LoadedElementsFacade } from '@shared/modules/relations/core/loaded-elements.facade';
import { RelationsAssembler } from '@shared/modules/relations/core/relations.assembler';
import { PartsService } from '@shared/service/parts.service';
import { cloneDeep as _cloneDeep } from 'lodash-es';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

@Injectable()
export class PartDetailsFacade {
  constructor(
    private readonly partsService: PartsService,
    private readonly partsState: PartsState,
    private readonly partDetailsState: PartDetailsState,
    private readonly loadedElementsFacade: LoadedElementsFacade,
  ) {}

  public get selectedPart$(): Observable<View<Part>> {
    return this.partDetailsState.selectedPart$;
  }

  public set selectedPart(part: Part) {
    this.partDetailsState.selectedPart = { data: part };
  }

  public get selectedPart(): Part {
    return this.partDetailsState.selectedPart?.data;
  }

  public setPartFromTree(id: string): Observable<View<Part>> {
    return this.partsService.getPart(id).pipe(
      tap((part: Part) => {
        this.partDetailsState.selectedPart = { data: part };
      }),
      catchError(error => {
        this.partDetailsState.selectedPart = { error };
        return of(error);
      }),
    );
  }

  public updateQualityType(qualityType: QualityType): Observable<Part> {
    const part = { ...this.selectedPart, qualityType };

    this.loadedElementsFacade.addLoadedElement(RelationsAssembler.assemblePartForRelation(part));

    const { id } = part;
    const { data } = _cloneDeep(this.partsState.parts);

    data.content = data.content.map(currentPart => (currentPart.id === id ? part : currentPart));
    this.partsState.parts = { data };

    return this.partsService.patchPart(part);
  }
}
