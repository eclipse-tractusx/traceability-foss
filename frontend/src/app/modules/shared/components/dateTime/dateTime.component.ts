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

import { Component, Inject, Injector, Input } from '@angular/core';
import { BaseInputComponent } from '@shared/abstraction/baseInput/baseInput.component';
import { StaticIdService } from '@shared/service/staticId.service';

type DateString = `${ string }${ string }${ string }${ string }-${ string }${ string }-${ string }${ string }`;
type TimeString = `${ string }${ string }:${ string }${ string }`;
export type DateTimeString = `${ DateString }T${ TimeString }`;

@Component({
  selector: 'app-date-time',
  templateUrl: './dateTime.component.html',
})
export class DateTimeComponent extends BaseInputComponent<Date> {
  @Input() set min(date: Date) {
    this.minDate = date.toISOString().substring(0, 16) as DateTimeString;
  }

  @Input() set max(date: Date) {
    this.maxDate = date.toISOString().substring(0, 16) as DateTimeString;
  }

  public minDate: DateTimeString;
  public maxDate: DateTimeString;

  constructor(@Inject(Injector) injector: Injector, staticIdService: StaticIdService) {
    super(injector, staticIdService);
  }

  openDatePicker(element: any): void{
      element.showPicker();
  }
}
