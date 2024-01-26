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

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { StaticIdService } from '@shared/service/staticId.service';

@Component({
  selector: 'app-card-icon',
  templateUrl: './card-icon.component.html',
  styleUrls: ['./card-icon.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CardIconComponent {
  public readonly htmlIdBase = 'app-card-icon-';
  public readonly htmlId: string;
  public readonly iconPath = '/assets/images/icons/';

  @Input() label: string;
  @Input() stats: number | string;
  @Input() icon: string;

  constructor(staticIdService: StaticIdService) {
    this.htmlId = staticIdService.generateId(this.htmlIdBase);
  }
}
