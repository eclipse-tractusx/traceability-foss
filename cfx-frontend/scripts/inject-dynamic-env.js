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

const fs = require('fs');

const ENV_VARS_MAPPING = {
  CATENAX_PORTAL_KEYCLOAK_URL: 'keycloakUrl',
  CATENAX_PORTAL_CLIENT_ID: 'clientId',
  CATENAX_PORTAL_REALM: 'realm',
  CATENAX_PORTAL_API_URL: 'apiUrl',
  CATENAX_PORTAL_BASE_URL: 'baseUrl',
  CATENAX_PORTAL_URL: 'portalUrl',
  GIT_TAG: 'gitTag',
  BPN: 'bpn',
  IMPRINT_URL: 'imprintPath',
  PRIVACY_URL: 'privacyPath'
};

const ROOT_DIR = '/usr/share/nginx/html';

const REFERENCE_HTML_PATH = ROOT_DIR + '/index.html.reference';
const TARGET_HTML_PATH = ROOT_DIR + '/index.html';

const ENV_VARS_ANCHOR = '<!--CUSTOM_CONFIG_PLACEHOLDER-->';

const getEnvVarsJson = () => {
  return JSON.stringify(
    Object.entries(ENV_VARS_MAPPING).reduce((current, [envVar, configName]) => {
      if (typeof process.env[envVar] === 'string') {
        current[configName] = process.env[envVar];
      }
      return current;
    }, {}),
  );
};

const indexHtmlReference = fs.readFileSync(REFERENCE_HTML_PATH, 'utf8');
const indexHtmlWithInjectedEnvVars = indexHtmlReference.replace(ENV_VARS_ANCHOR, getEnvVarsJson());
fs.writeFileSync(TARGET_HTML_PATH, indexHtmlWithInjectedEnvVars, 'utf8');
