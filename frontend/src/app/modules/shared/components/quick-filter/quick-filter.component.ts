/********************************************************************************
 * Copyright (c) 2023,2025 Contributors to the Eclipse Foundation
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
import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Owner } from '@page/parts/model/owner.enum';
import { QuickfilterService } from '@shared/service/quickfilter.service';

@Component({
  selector: 'app-quick-filter',
  templateUrl: './quick-filter.component.html',
  styleUrls: [ './quick-filter.component.scss' ],
})
export class QuickFilterComponent implements OnInit {

  constructor(private quickFilterService: QuickfilterService) {}

  owner: Owner;
  @Output() buttonClickEvent = new EventEmitter<Owner>();

  ngOnInit(): void {
    this.owner = this.quickFilterService.getOwner();
  }

  emitQuickFilter(owner: Owner) {
    if (this.owner === owner) {
      this.owner = Owner.UNKNOWN;
    } else {
      this.owner = owner;
    }
    this.buttonClickEvent.emit(this.owner);
    this.quickFilterService.setOwner(this.owner);
  }


  handleKeyDownByOwner(event: KeyboardEvent, owner: Owner) {
    if (event.key === 'Enter') {
      this.emitQuickFilter(owner);
    }
  }

  protected readonly Owner = Owner;
}
