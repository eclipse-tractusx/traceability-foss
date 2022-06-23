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

const SCRIPT_EL_ID = 'envConfig';
const SUPPORTED_ENV_PARAMS = ['keycloakUrl', 'clientId', 'defaultRealm', 'realmLogo', 'apiUrl', 'baseUrl'];

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
                [curr]: dynamicEnv[curr],
              }
            : acc,
        {},
      );
    } catch (err) {
      console.warn(`Cannot parse JSON from <script id="${SCRIPT_EL_ID}">`, err);
    }
  }

  return {};
};
