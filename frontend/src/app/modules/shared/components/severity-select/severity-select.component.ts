/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SelectOption } from '@shared/components/select/select.component';
import { Severity } from '@shared/model/severity.model';

@Component({
  selector: 'app-severity-select',
  templateUrl: './severity-select.component.html',
})
export class SeveritySelectComponent {
  public options: SelectOption[];

  @Input() selectedValue: Severity;
  @Input() translationContext: string;
  @Output() selectedEvent = new EventEmitter<Severity>();

  constructor() {
    this.options = Object.values(Severity).map(value => ({
      lable: value,
      value: value,
    }));
  }

  public selectValue(selectedSeverity: string) {
    this.selectedValue = selectedSeverity as Severity;
    this.selectedEvent.emit(this.selectedValue);
  }
}
