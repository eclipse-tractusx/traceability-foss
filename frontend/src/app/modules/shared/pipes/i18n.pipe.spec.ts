/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2025 Contributors to the Eclipse Foundation
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

import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { I18NextPipe } from 'angular-i18next';
import { I18nPipe, SharedModule } from '..';

describe('I18nPipe', () => {
  let pipe: I18nPipe;
  let mockI18NextPipe: jasmine.SpyObj<I18NextPipe>;

  beforeEach(() => {
    mockI18NextPipe = jasmine.createSpyObj('I18NextPipe', ['transform']);
    pipe = new I18nPipe(mockI18NextPipe);
  });

  it('should format string without errors', async () => {
    await renderComponent(`{{ 'unitTest.test01' | i18n }}`, {
      imports: [ SharedModule ],
    });

    expect(screen.getByText('unitTest.test01')).toBeInTheDocument();
  });

  it('should not format string if string is not found', async () => {
    const testString = 'notFound.test.notFound';
    await renderComponent(`{{ '${ testString }' | i18n }}`, {
      imports: [ SharedModule ],
    });

    expect(screen.getByText(testString)).toBeInTheDocument();
  });

  it('should format string with correct data passed in string', async () => {
    const errorMessageObject = { id: 'unitTest.test02', values: { test: 5 } };
    await renderComponent(`{{ errorMessageObject | i18n }}`, {
      imports: [ SharedModule ],
      componentProperties: { errorMessageObject },
    });

    expect(screen.getByText('unitTest.test02')).toBeInTheDocument();
  });

  it('should format string with correct data passed as property', async () => {
    await renderComponent(`{{ 'unitTest.test02' | i18n:{test: 10} }}`, {
      imports: [ SharedModule ],
    });

    expect(screen.getByText('unitTest.test02')).toBeInTheDocument();
  });

  it('should not format id string', async () => {
    const id = 'urn:uuid:55c376ed-e39d-4525-96e6-71f3def7ae41';
    await renderComponent(`{{ '${ id }' | i18n }}`, {
      imports: [ SharedModule ],
    });

    expect(screen.getByText(id)).toBeInTheDocument();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return empty string if key is falsy', () => {
    expect(pipe.transform(null)).toBe('');
    expect(pipe.transform(undefined)).toBe('');
    expect(pipe.transform('')).toBe('');
  });

  it('should return key as-is if it contains ":"', () => {
    const key = 'custom:label';
    expect(pipe.transform(key)).toBe(key);
  });

  it('should use i18NextPipe.transform for string keys without ":"', () => {
    const key = 'home.title';
    const options = { count: 2 };
    mockI18NextPipe.transform.and.returnValue('Localized Title');

    const result = pipe.transform(key, options);
    expect(mockI18NextPipe.transform).toHaveBeenCalledWith(key, options);
    expect(result).toBe('Localized Title');
  });

  it('should extract key and options from I18nMessage object and delegate to i18NextPipe', () => {
    const keyObject = { id: 'dashboard.greeting', values: { user: 'John' } };
    mockI18NextPipe.transform.and.returnValue('Hello, John');

    const result = pipe.transform(keyObject);
    expect(mockI18NextPipe.transform).toHaveBeenCalledWith('dashboard.greeting', { user: 'John' });
    expect(result).toBe('Hello, John');
  });
});
