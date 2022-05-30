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

import { Component, OnInit } from '@angular/core';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { View } from '@shared';
import { TableConfig } from '@shared/components/table/table.model';
import { Observable } from 'rxjs';

interface Parts {}

@Component({
  selector: 'app-parts',
  templateUrl: './parts.component.html',
  styleUrls: ['./parts.component.scss'],
})
export class PartsComponent implements OnInit {
  public readonly title = 'Catena-X Parts';

  public readonly displayedColumns: string[] = [
    'id',
    'name',
    'manufacturer',
    'partNumber',
    'serialNumber',
    'qualityType',
    'productionDate',
    'productionCountry',
  ];

  public readonly sortableColumns: Record<string, boolean> = {
    id: true,
    name: true,
    manufacturer: true,
    partNumber: true,
    serialNumber: true,
    qualityType: true,
    productionDate: true,
    productionCountry: true,
  };

  public tableConfig: TableConfig;

  public parts$: Observable<View<Parts[]>>;

  constructor(private readonly partsFacade: PartsFacade) {
    this.parts$ = this.partsFacade.parts$;
    this.tableConfig = { displayedColumns: this.displayedColumns, sortableColumns: this.sortableColumns };
  }

  ngOnInit(): void {
    this.partsFacade.setParts();
  }

  public onSelectItem($event: Record<string, unknown>) {
    console.log($event);
  }
}
