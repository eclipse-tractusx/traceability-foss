/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import { AbbreviateNumberPipe } from './abbreviate-number.pipe';

describe('AbbreviateNumberPipe', () => {
  let pipe: AbbreviateNumberPipe;

  beforeEach(() => {
    pipe = new AbbreviateNumberPipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return undefined for null value', () => {
    expect(pipe.transform(null)).toBeUndefined();
  });

  it('should return undefined for undefined value', () => {
    expect(pipe.transform(undefined)).toBeUndefined();
  });

  it('should return "Out of Range" for numbers greater than 9999999', () => {
    expect(pipe.transform('10,000,000')).toBe('Out of Range');
    expect(pipe.transform('10000000')).toBe('Out of Range');
  });

  it('should convert numbers >= 1,000,000 and <= 9,999,999 to abbreviated format', () => {
    expect(pipe.transform('1,000,000')).toBe('1.0');
    expect(pipe.transform('1500000')).toBe('1.5');
    expect(pipe.transform('9,999,999')).toBe('10.0');
  });

  it('should return original string for numbers less than 1,000,000', () => {
    expect(pipe.transform('999,999')).toBe('999,999');
    expect(pipe.transform('500000')).toBe('500000');
  });

  it('should handle value with periods as thousand separators', () => {
    expect(pipe.transform('1.000.000')).toBe('1.0');
  });

  it('should handle mixed comma and period', () => {
    expect(pipe.transform('1,000.000')).toBe('1.0');
  });
});
