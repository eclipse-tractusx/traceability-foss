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
import { ApiService } from '@core/api/api.service';
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import { PartsAssembler } from '@page/parts/core/parts.assembler';
import { Part, PartResponse, PartsResponse } from '@page/parts/model/parts.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class PartsService {
  private url = environment.apiUrl;

  constructor(private apiService: ApiService) {}

  public getParts(): Observable<Pagination<Part>> {
    return this.apiService
      .get<PartsResponse>(`${this.url}/assets`)
      .pipe(map(parts => PartsAssembler.assembleParts(parts)));
  }

  public getPart(id: string): Observable<Part> {
    return this.apiService
      .get<PartResponse>(`${this.url}/assets/${id}`)
      .pipe(map(part => PartsAssembler.assemblePart(part)));
  }

  public getRelation(partId: string, childId: string): Observable<Part> {
    return this.apiService
      .get<PartResponse>(`${this.url}/assets/${partId}/children/${childId}`)
      .pipe(map(part => PartsAssembler.assemblePart(part)));
  }
}
