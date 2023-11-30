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

import { animate, state, style, transition, trigger } from '@angular/animations';

/**
 * Toast message animation
 * Slides the message and the status bar from right to left and the opposite way
 */
export /** @type {*} */
const notifyAnimation = trigger('notify', [
  state(
    'void',
    style({
      opacity: 0,
      height: 0,
      transform: 'translateX(100%)',
    }),
  ),
  state('show', style({ transform: 'translateX(5%)' })),
  transition('void => show, show => void', [ animate('0.30s') ]),
]);
