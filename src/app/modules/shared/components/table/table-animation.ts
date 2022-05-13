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

import { animate, keyframes, state, style, transition, trigger } from '@angular/animations';

export /** @type {*} */
const tableAnimation = [
  trigger('itemStatus', [
    state('deleted', style({ display: 'none' })),
    transition(
      '* => deleted',
      animate(
        '1.5s 10ms',
        keyframes([
          style({ opacity: '1', background: '#ff5050' }),
          style({ opacity: '0.5' }),
          style({ opacity: '0.4' }),
          style({ opacity: '0.3' }),
          style({ opacity: '0.1' }),
        ]),
      ),
    ),
  ]),
  trigger('detailExpand', [
    state('void', style({ height: '0px', minHeight: '0', visibility: 'hidden' })),
    state('*', style({ height: '*', visibility: 'visible' })),
    transition('void <=> *', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
  ]),
];
