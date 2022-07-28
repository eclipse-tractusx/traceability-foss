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
import { ActivatedRoute, Router } from '@angular/router';
import { TablePaginationEventConfig } from '@shared/components/table/table.model';
import { map } from 'rxjs';
import { InvestigationsFacade } from '../core/investigations.facade';
import { InvestigationStatusGroup } from '../model/investigations.model';

@Component({
  selector: 'app-investigations',
  templateUrl: './investigations.component.html',
})
export class InvestigationsComponent implements OnInit {
  public readonly InvestigationStatusGroup = InvestigationStatusGroup;

  public readonly investigationsReceived$ = this.investigationsFacade.investigationsReceived$;
  public readonly investigationsQueuedNRequested$ = this.investigationsFacade.investigationsQueuedNRequested$;
  public readonly tabIndex$ = this.route.queryParams.pipe(map(params => parseInt(params.tabIndex, 10) || 0));

  constructor(
    private investigationsFacade: InvestigationsFacade,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  public ngOnInit() {
    this.investigationsFacade.setInvestigations();
  }

  public onPagination(type: InvestigationStatusGroup, pagination: TablePaginationEventConfig) {
    this.investigationsFacade.setInvestigationsPagination(type, pagination.page, pagination.pageSize);
  }

  public onTabChange(tabIndex: number) {
    this.router.navigate([], { queryParams: { tabIndex }, replaceUrl: true });
  }
}
