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
import { StaticIdService } from '@shared/service/staticId.service';
import { TablePaginationEventConfig } from '@shared/components/table/table.model';
import { map } from 'rxjs';
import { InvestigationsFacade } from '../core/investigations.facade';

@Component({
  selector: 'app-investigations',
  templateUrl: './investigations.component.html',
})
export class InvestigationsComponent implements OnInit {
  public readonly investigationsReceived$ = this.investigationsFacade.investigationsReceived$;
  public readonly investigationsQueuedAndRequested$ = this.investigationsFacade.investigationsQueuedAndRequested$;
  public readonly tabIndex$ = this.route.queryParams.pipe(map(params => parseInt(params.tabIndex, 10) || 0));

  public readonly receivedTabLabelId = this.staticIdService.generateId('InvestigationsComponent.receivedTabLabel');
  public readonly queuedAndRequestedTabLabelId = this.staticIdService.generateId(
    'InvestigationsComponent.queuedAndRequestedTabLabel',
  );

  constructor(
    private readonly investigationsFacade: InvestigationsFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly staticIdService: StaticIdService,
  ) {}

  public ngOnInit() {
    this.investigationsFacade.setReceivedInvestigation();
    this.investigationsFacade.setQueuedAndRequestedInvestigations();
  }

  public onReceivedPagination(pagination: TablePaginationEventConfig) {
    this.investigationsFacade.setReceivedInvestigation(pagination.page, pagination.pageSize);
  }

  public onQueuedAndRequestedPagination(pagination: TablePaginationEventConfig) {
    this.investigationsFacade.setQueuedAndRequestedInvestigations(pagination.page, pagination.pageSize);
  }

  public onTabChange(tabIndex: number) {
    void this.router.navigate([], { queryParams: { tabIndex }, replaceUrl: true });
  }
}
