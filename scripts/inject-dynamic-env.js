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

const fs = require('fs');

const ENV_VARS_MAPPING = {
  CATENAX_PORTAL_KEYCLOAK_URL: 'keycloakUrl',
  CATENAX_PORTAL_CLIENT_ID: 'clientId',
  CATENAX_PORTAL_DEFAULT_REALM: 'defaultRealm',
  CATENAX_PORTAL_REALM_LOGO: 'realmLogo',
  CATENAX_PORTAL_API_URL: 'apiUrl',
  CATENAX_PORTAL_BASE_URL: 'baseUrl',
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
