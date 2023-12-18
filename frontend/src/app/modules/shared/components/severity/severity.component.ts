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

import { Component, Input } from '@angular/core';
import { Severity } from '@shared/model/severity.model';

@Component({
  selector: 'app-severity',
  templateUrl: './severity.component.html',
  styleUrls: [ './severity.component.scss' ],
})
export class SeverityComponent {
  @Input() severity: Severity;

  public getIconBySeverity(severity: Severity): string {
    const iconMap = new Map<Severity, string>([
      [ Severity.MINOR, 'info' ],
      [ Severity.MAJOR, 'warning' ],
      [ Severity.CRITICAL, 'error_outline' ],
      [ Severity.LIFE_THREATENING, 'error' ],
    ]);
    return iconMap.get(severity) || '';
  }
}
