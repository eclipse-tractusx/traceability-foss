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

import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { InvestigationType } from '../model/investigationsInbox.model';

@Component({
  selector: 'app-investigations-inbox',
  templateUrl: './investigationsInbox.component.html',
})
export class InvestigationsInboxComponent {
  constructor(private route: ActivatedRoute) {}

  tabsByType = [InvestigationType.RECEIVED, InvestigationType.QUEUED, InvestigationType.REQUESTED];

  isActive(tab: string): boolean {
    // in test env firstChild doesn't exists on creation, however it's not the case in real app
    return this.route.snapshot.firstChild?.url.some(segment => segment.path.includes(tab));
  }
}
