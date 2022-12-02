/********************************************************************************
 * Copyright (c) 2022,2023
 *        2022: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *        2022: ZF Friedrichshafen AG
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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

import { buildCustomProtocolResolver } from './fetch-custom-protocols';

describe('buildCustomProtocolResolver', () => {
  it('should pass as it is, if no protocols exists', () => {
    const resolver = buildCustomProtocolResolver({});
    expect(resolver('s3://test.json')).toEqual('s3://test.json');
  });

  it('should allow simple protocol resolution', () => {
    const resolver = buildCustomProtocolResolver({
      s3: 'https://aws.com/',
    });
    expect(resolver('s3://test.json')).toEqual('https://aws.com/test.json');
  });

  it('should allow to custom replace by prefix', () => {
    const resolver = buildCustomProtocolResolver({
      s3: {
        '//storage/': {
          pathname: 'https://aws.com/v1/storage/',
        },
      },
    });
    expect(resolver('s3://storage/test.json')).toEqual('https://aws.com/v1/storage/test.json');
    // do not resolve unknown prefixes
    expect(resolver('s3://test.json')).toEqual('s3://test.json');
  });

  it('should allow to pass custom query params by prefix', () => {
    const resolver = buildCustomProtocolResolver({
      s3: {
        '//storage/': {
          pathname: 'https://aws.com/v1/storage/',
          queryParams: {
            test: '',
            foo: 'bar',
          },
        },
      },
    });
    expect(resolver('s3://storage/test.json')).toEqual('https://aws.com/v1/storage/test.json?test=&foo=bar');
    expect(resolver('s3://storage/test.json?context=test')).toEqual(
      'https://aws.com/v1/storage/test.json?context=test&test=&foo=bar',
    );
  });

  it('should allow to define custom postfix by prefix', () => {
    const resolver = buildCustomProtocolResolver({
      s3: {
        '//storage/': {
          pathname: 'https://aws.com/v1/storage/',
          postfix: '.html',
        },
      },
    });
    expect(resolver('s3://storage/test')).toEqual('https://aws.com/v1/storage/test.html');
    expect(resolver('s3://storage/test?test=1')).toEqual('https://aws.com/v1/storage/test.html?test=1');
  });
});
