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
import { Part } from '@page/parts/model/parts.model';
import { View } from '@shared/model/view.model';
import { PartDetailsState } from '@shared/modules/part-details/core/partDetails.state';
import { PartsService } from '@shared/service/parts.service';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { SortDirection } from '../../../../../mocks/services/pagination.helper';

@Injectable()
export class PartDetailsFacade {
  constructor(
    private readonly partsService: PartsService,
    private readonly partDetailsState: PartDetailsState,
  ) {
  }

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

  public getRootPart(id: string): Observable<View<Part>> {
    return this.partsService.getPart(id).pipe(
      map((part: Part) => ({ data: part })),
      catchError((error: Error) => of({ error })),
    );
  }

  public getChildPartDetails(part): Observable<Part[]> {
    return this.partsService.getPartDetailOfIds(part.children);
  }

  public sortChildParts(view: View<Part[]>, key: string, direction: SortDirection): Part[] {
    if (!view.data) return [];

    return this.partsService.sortParts(view.data, key, direction);
  }
}
