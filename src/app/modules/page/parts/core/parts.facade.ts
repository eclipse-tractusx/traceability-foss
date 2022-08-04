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
import { Observable, Subscription } from 'rxjs';

@Injectable()
export class PartsFacade {
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
}
