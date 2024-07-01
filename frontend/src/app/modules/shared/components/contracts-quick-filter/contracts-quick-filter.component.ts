/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import { Component, EventEmitter, Output } from '@angular/core';
import { ContractType } from '@page/admin/core/admin.model';

@Component({
  selector: 'app-contracts-quick-filter',
  templateUrl: './contracts-quick-filter.component.html',
  styleUrls: [ './contracts-quick-filter.component.scss' ],
})
export class ContractsQuickFilterComponent {

  activeContractTypes: ContractType[] = [];
  @Output() buttonClickEvent = new EventEmitter<any>();

  emitQuickFilter(contractTypeList: ContractType[]) {
    if (this.activeContractTypes?.includes(contractTypeList[0])) {
      this.activeContractTypes = this.activeContractTypes.filter(type => type !== contractTypeList[0]);
      this.activeContractTypes = this.activeContractTypes.filter(type => type !== contractTypeList?.[1]);
    } else {
      this.activeContractTypes.push(...contractTypeList);
    }
    this.buttonClickEvent.emit(this.activeContractTypes);
  }

  protected readonly ContractType = ContractType;
}
