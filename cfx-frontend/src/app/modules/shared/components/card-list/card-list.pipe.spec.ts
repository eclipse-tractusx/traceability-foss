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

import { ToKeyValuePipe } from './card-list.pipe';

describe('ToKeyValuePipe', () => {
  const pipe = new ToKeyValuePipe();

  it('should return empty array if no data is given.', () => {
    expect(pipe.transform(null)).toEqual([]);
  });

  it('should return empty array if string is given.', () => {
    expect(pipe.transform('test' as any)).toEqual([]);
  });

  it('should return empty array if number is given.', () => {
    expect(pipe.transform(123 as any)).toEqual([]);
  });

  it('should return empty array if array is given.', () => {
    expect(pipe.transform([ 123, 'test' ] as any)).toEqual([]);
  });

  it('should return transformed object ', () => {
    const test1 = '123';
    const test2 = 0;
    const test3 = false;
    const test4 = null;
    const test5 = undefined;

    const expected = [
      { key: 'test1', value: test1 },
      { key: 'test2', value: test2 },
      { key: 'test3', value: test3 },
      { key: 'test4', value: test4 },
      { key: 'test5', value: test5 },
    ];
    const testData = { test1, test2, test3, test4, test5 };
    expect(pipe.transform(testData)).toEqual(expected);
  });
});
