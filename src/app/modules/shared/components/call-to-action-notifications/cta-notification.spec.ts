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

import { TestBed } from '@angular/core/testing';
import { SharedModule } from '@shared/shared.module';
import { fireEvent, screen, waitForElementToBeRemoved } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { CtaNotificationService } from './cta-notification.service';

describe('cta-notification', () => {
  const prepareEnv = async () => {
    await renderComponent('', {
      imports: [SharedModule],
    });

    return TestBed.inject(CtaNotificationService);
  };

  it('should show message with action', async () => {
    const ctaNotificationService = await prepareEnv();

    ctaNotificationService.show('actions.back', [{ text: 'Open', link: '' }]);

    expect(await screen.findByText('Back')).toBeInTheDocument();
    expect(await screen.findByText('Open')).toBeInTheDocument();
  });

  it('should close message after click on action', async () => {
    const ctaNotificationService = await prepareEnv();

    ctaNotificationService.show('actions.back', [{ text: 'Open', link: '' }]);
    fireEvent.click(await screen.findByText('Open'));

    await waitForElementToBeRemoved(() => screen.queryByText('Open'));

    expect(screen.queryByText('Back')).not.toBeInTheDocument();
  });
});
