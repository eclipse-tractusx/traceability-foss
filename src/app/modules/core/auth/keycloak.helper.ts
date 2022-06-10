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

import { KeycloakService } from 'keycloak-angular';
import { environment } from '@env';

export function KeycloakHelper(keycloak: KeycloakService): () => Promise<boolean | void> {
  // Set default realm
  let realm = environment.defaultRealm;

  // Check multi-tenant
  if (environment.multiTenant) {
    const matches: RegExpExecArray = new RegExp(environment.realmRegExp).exec(window.location.href);
    if (matches) {
      realm = matches[1];

      // Update the <base> href attribute
      document.getElementsByTagName('base').item(0).attributes.getNamedItem('href').value =
        environment.baseUrl + realm + '/';
    } else {
      // Redirect user to the default realm page.
      window.location.href = document.getElementsByTagName('base').item(0).attributes.getNamedItem('href').value =
        environment.baseUrl + environment.defaultRealm + '/';

      return (): Promise<void> => Promise.resolve();
    }
  }

  return (): Promise<boolean> =>
    keycloak.init({
      config: {
        url: environment.keycloakUrl,
        realm,
        clientId: environment.clientId,
      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false,
        silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        pkceMethod: 'S256',
      },
    });
}
