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
import { PartsService } from '@page/parts/core/parts.service';
import { PartsState } from '@page/parts/core/parts.state';
import { Part } from '@page/parts/model/parts.model';
import { View } from '@shared';
import { Observable, of } from 'rxjs';
import { catchError, delay, tap } from 'rxjs/operators';

@Injectable()
export class PartsFacade {
  constructor(private partsService: PartsService, private partsState: PartsState) {}

  get selectedPart$(): Observable<View<Part>> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.partsState.selectedPart$.pipe(delay(0));
  }

  set selectedPart(part: Part) {
    this.partsState.selectedPart = { data: part };
  }

  get parts$(): Observable<View<Part[]>> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.partsState.parts$.pipe(delay(0));
  }

  public setParts(): void {
    this.partsState.parts = { loader: true };

    this.partsService.getParts().subscribe({
      next: (parts: Part[]) => {
        this.partsState.parts = { data: parts };
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
}
