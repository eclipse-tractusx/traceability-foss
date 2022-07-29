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

import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { SharedModule } from '..';

describe('I18nPipe', () => {
  it('should format string without errors', async () => {
    await renderComponent(`{{ 'unitTest.test01' | i18n }}`, {
      imports: [SharedModule],
    });

    expect(screen.getByText('This is for unit tests purposes.')).toBeInTheDocument();
  });

  it('should not format string if string is not found', async () => {
    const testString = 'notFound.test.notFound';
    await renderComponent(`{{ '${testString}' | i18n }}`, {
      imports: [SharedModule],
    });

    expect(screen.getByText(testString)).toBeInTheDocument();
  });

  it('should format string with correct data passed in string', async () => {
    await renderComponent(`{{ 'unitTest.test02:test:5' | i18n }}`, {
      imports: [SharedModule],
    });

    expect(screen.getByText('This is for unit tests purposes. 5')).toBeInTheDocument();
  });

  it('should format string with correct data passed as property', async () => {
    await renderComponent(`{{ 'unitTest.test02' | i18n:{test: 10} }}`, {
      imports: [SharedModule],
    });

    expect(screen.getByText('This is for unit tests purposes. 10')).toBeInTheDocument();
  });
});
