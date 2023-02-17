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

import { HttpParams } from '@angular/common/http';

interface CustomProtocolResolution {
  pathname: string;
  postfix?: string;
  queryParams?: Record<string, string | boolean>;
}
type CustomProtocolsRegistry = Record<string, string | Record<string, CustomProtocolResolution>>;

export const buildCustomProtocolResolver = (customProtocolsRegistry: CustomProtocolsRegistry) => {
  const PROTOCOL_SEPARATOR = '://';

  return (url: string) => {
    const protocolIndex = url.indexOf(PROTOCOL_SEPARATOR);

    if (protocolIndex === -1) {
      return url;
    }

    const customProtocolName = url.substring(0, protocolIndex);
    const customProtocolResolution = customProtocolsRegistry[url.substring(0, protocolIndex)];

    if (!customProtocolResolution) {
      return url;
    }

    if (typeof customProtocolResolution === 'string') {
      return url.replace(customProtocolName + PROTOCOL_SEPARATOR, customProtocolResolution);
    }

    const prefix = Object.keys(customProtocolResolution).find(possiblePrefix => url.includes(possiblePrefix));

    if (!prefix) {
      return url;
    }

    const resolution = customProtocolResolution[prefix];
    const fullPrefix = customProtocolName + ':' + prefix;

    const originalQueryParams = {};
    new URL(url).searchParams.forEach((key, value) => (originalQueryParams[key] = value));

    const cleanUrl = url.split('?')[0];

    const targetQueryParams = new HttpParams({
      fromObject: { ...originalQueryParams, ...resolution.queryParams },
    }).toString();

    const hasQueryParams = Object.keys(targetQueryParams).length > 0;
    const targetUrlWithoutParams = cleanUrl.replace(fullPrefix, resolution.pathname) + (resolution.postfix ?? '');
    return targetUrlWithoutParams + (hasQueryParams ? '?' + targetQueryParams : '');
  };
};

export const registerCustomProtocols = (customProtocolsRegistry: CustomProtocolsRegistry) => {
  const originalFetch = fetch;

  const customProtocolResolver = buildCustomProtocolResolver(customProtocolsRegistry);

  globalThis.fetch = (input: RequestInfo, init?: RequestInit) => {
    if (typeof input === 'string') {
      return originalFetch(customProtocolResolver(input), init);
    } else {
      return originalFetch(
        new Request(customProtocolResolver(input.url), {
          method: input.method,
          headers: input.headers,
          body: input.body,
          mode: input.mode,
          credentials: input.credentials,
          cache: input.cache,
          redirect: input.redirect,
          referrer: input.referrer,
          referrerPolicy: input.referrerPolicy,
          integrity: input.integrity,
          keepalive: input.keepalive,
          signal: input.signal,
        }),
        init,
      );
    }
  };
};
