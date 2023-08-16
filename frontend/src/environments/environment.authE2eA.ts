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

import { _environment } from './_environment.base';

export const environment = {
  ..._environment,
  mockService: false,
  authDisabled: false,
  apiUrl: 'https://traceability-e2e-a.dev.demo.catena-x.net/api',
  keycloakUrl: 'https://centralidp.dev.demo.catena-x.net/auth',
  clientId: 'Cl17-CX-Part',
  api: '',
  gitTag: "local"
};
