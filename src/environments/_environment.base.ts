/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

export const _environment = {
  production: false,
  mockService: true,
  authDisabled: true,
  multiTenant: false,
  keycloakUrl: 'http://localhost:8080/',
  clientId: 'catenax-portal',
  defaultRealm: 'mock',
  realmLogo: '/assets/images/logo.png',
  apiUrl: '/api',
  realmRegExp: '^https?://[^/]+/([-a-z-A-Z-0-9]+)',
  baseUrl: '/',
  mapBoxAccessToken: 'pk.eyJ1IjoiZmVsaXhnZXJiaWciLCJhIjoiY2sxNmh4d2dvMTJkdTNpcGZtcWhvaHpuNyJ9.2hJW4R6PoiqIgytqUn1kbg',
};
