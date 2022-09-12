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

import { enableProdMode } from '@angular/core';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { CoreModule } from '@core/core.module';
import { registerCustomProtocols } from '@core/extensions/fetch-custom-protocols';
import { environment } from '@env';

if (environment.production) {
  enableProdMode();
}

if (environment.customProtocols) {
  registerCustomProtocols(environment.customProtocols);
}

// if the zone has already been loaded, go ahead a bootstrap the app
if (window['Zone']) {
  bootstrap();

  // otherwise, wait to bootstrap the app until zone.js is imported
} else {
  import('zone.js/dist/zone').then(() => bootstrap());
}

function bootstrap() {
  platformBrowserDynamic()
    .bootstrapModule(CoreModule)
    .catch(err => console.error(err));
}
