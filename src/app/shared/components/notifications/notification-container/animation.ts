/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { animate, state, style, transition, trigger } from '@angular/animations';

/**
 * Notification message animation
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
  transition('void => show, show => void', [animate('0.70s')]),
]);
