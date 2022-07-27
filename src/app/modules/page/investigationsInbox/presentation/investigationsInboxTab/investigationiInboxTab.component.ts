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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { InvestigationsInboxFacade } from '@page/investigationsInbox/core/investigationsInbox.facade';
import { Investigation, InvestigationType } from '@page/investigationsInbox/model/investigationsInbox.model';
import { TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-investigations-inbox-tab',
  templateUrl: './investigationsInboxTab.component.html',
  styleUrls: [],
})
export class InvestigationsInboxTabComponent implements OnInit, OnDestroy {
  public readonly displayedColumns: (keyof Investigation)[] = ['description', 'created'];

  readonly investigations$ = this.investigationsInboxFacade.investigations$;

  readonly tableConfig: TableConfig = {
    displayedColumns: this.displayedColumns,
    header: this.displayedColumns.map(column => `pageInvestigationsInbox.column.${column}`),
  };

  private routeSubscription: Subscription;

  constructor(private route: ActivatedRoute, private investigationsInboxFacade: InvestigationsInboxFacade) {}

  ngOnInit(): void {
    this.investigationsInboxFacade.setInvestigations();
    this.routeSubscription = this.route.data.pipe(map(({ type }) => type)).subscribe((type: InvestigationType) => {
      this.investigationsInboxFacade.setInvestigationsType(type);
    });
  }

  ngOnDestroy(): void {
    this.routeSubscription.unsubscribe();
  }

  onTableConfigChange({ page, pageSize }: TableEventConfig) {
    this.investigationsInboxFacade.setInvestigationsPagination(page, pageSize);
  }
}
