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

const NGINX_CONF_PATH = '/etc/nginx/nginx.conf';
const NGINX_SECURITY_CONF_PATH = '/etc/nginx/security-headers.conf';
const HTML_PATH = '/usr/share/nginx/html/index.html';

const files = fs.readdirSync('/usr/share/nginx/html/');
const RUNTIME_PATH = '/usr/share/nginx/html/' + files.find((file) => file.startsWith('runtime.') && file.endsWith('.js'));

const BASE_HREF_PLACEHOLDER_VAR = '/{baseHrefPlaceholder}';
const BACKEND_DOMAIN_PLACEHOLDER_VAR = '{backendDomain}';

const BASE_URL = process.env.CATENAX_PORTAL_BASE_URL || '';
const BACKEND_DOMAIN = process.env.CATENAX_PORTAL_BACKEND_DOMAIN || '';

const INDEX_HTML = fs.readFileSync(HTML_PATH, 'utf8');
const INDEX_HTML_WITH_BASE = INDEX_HTML.split(BASE_HREF_PLACEHOLDER_VAR).join(BASE_URL);
fs.writeFileSync(HTML_PATH, INDEX_HTML_WITH_BASE, 'utf8');

const RUNTIME_JS = fs.readFileSync(RUNTIME_PATH, 'utf8');
const RUNTIME_JS_WITH_BASE = RUNTIME_JS.split(BASE_HREF_PLACEHOLDER_VAR).join(BASE_URL);
fs.writeFileSync(RUNTIME_PATH, RUNTIME_JS_WITH_BASE, 'utf8');

const NGINX_CONF = fs.readFileSync(NGINX_CONF_PATH, 'utf8');
const NGINX_CONF_WITH_BASE = NGINX_CONF.split(BASE_HREF_PLACEHOLDER_VAR).join(BASE_URL);
fs.writeFileSync(NGINX_CONF_PATH, NGINX_CONF_WITH_BASE, 'utf8');

const NGINX_SECURITY_CONF = fs.readFileSync(NGINX_SECURITY_CONF_PATH, 'utf8');
const NGINX_SECURITY_CONF_WITH_DOMAIN = NGINX_SECURITY_CONF.split(BACKEND_DOMAIN_PLACEHOLDER_VAR).join(BACKEND_DOMAIN);
fs.writeFileSync(NGINX_SECURITY_CONF_PATH, NGINX_SECURITY_CONF_WITH_DOMAIN, 'utf8');
