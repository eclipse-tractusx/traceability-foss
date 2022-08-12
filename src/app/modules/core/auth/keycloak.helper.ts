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

import { environment } from '@env';
import { KeycloakService } from 'keycloak-angular';

export function KeycloakHelper(keycloak: KeycloakService): () => Promise<boolean | void> {
  // Set default realm
  let realm = environment.defaultRealm;

  // Check multi-tenant
  if (environment.multiTenant) {
    const matches: RegExpExecArray = new RegExp(environment.realmRegExp).exec(window.location.href);
    if (!matches) {
      // Redirect user to the default realm page.
      window.location.href = environment.baseUrl + environment.defaultRealm + '/';
      return () => Promise.resolve();
    }

    realm = matches[1];
  }

  return (): Promise<boolean> =>
    keycloak.init({
      config: {
        realm,
        url: environment.keycloakUrl,
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
