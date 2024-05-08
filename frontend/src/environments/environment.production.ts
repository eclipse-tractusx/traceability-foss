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

import { _environment } from './_environment.base';

const SCRIPT_EL_ID = 'envConfig';
const SUPPORTED_ENV_PARAMS = [ 'keycloakUrl', 'clientId', 'apiUrl', 'baseUrl', 'portalUrl', 'gitTag', 'bpn' ];

export const readDynamicEnv = () => {
  const scriptEl = document.getElementById(SCRIPT_EL_ID) as HTMLScriptElement;
  if (scriptEl && scriptEl.tagName === 'SCRIPT') {
    try {
      const dynamicEnv = JSON.parse(scriptEl.text);

      return SUPPORTED_ENV_PARAMS.reduce(
        (acc, curr) =>
          dynamicEnv.hasOwnProperty(curr)
            ? {
              ...acc,
              [curr]: dynamicEnv[curr] || _environment[curr],
            }
            : acc,
        {},
      );
    } catch (err) {
      console.warn(`Cannot parse JSON from <script id='${ SCRIPT_EL_ID }'>`, err);
    }
  }

  return {};
};

export const environment = {
  ..._environment,
  production: true,
  authDisabled: false,
  mockService: false,
  keycloakUrl: 'https://centralidp.dev.demo.catena-x.net/auth',
  ...readDynamicEnv(),
};
