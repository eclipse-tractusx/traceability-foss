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

import { TestBed } from '@angular/core/testing';
import { SharedModule } from '@shared/shared.module';
import { fireEvent, screen, waitForElementToBeRemoved } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { CtaSnackbarService } from './cta-snackbar.service';

describe('cta-snackbar', () => {
  const prepareEnv = async () => {
    await renderComponent('', {
      imports: [SharedModule],
    });

    return TestBed.inject(CtaSnackbarService);
  };

  it('should show message with action', async () => {
    const ctaSnackbarService = await prepareEnv();

    ctaSnackbarService.show('actions.back', [{ text: 'Open', link: '' }]);

    expect(await screen.findByText('Back')).toBeInTheDocument();
    expect(await screen.findByText('Open')).toBeInTheDocument();
  });

  it('should close message after click on action', async () => {
    const ctaSnackbarService = await prepareEnv();

    ctaSnackbarService.show('actions.back', [{ text: 'Open', link: '' }]);
    fireEvent.click(await screen.findByText('Open'));

    await waitForElementToBeRemoved(() => screen.queryByText('Open'));

    expect(screen.queryByText('Back')).not.toBeInTheDocument();
  });
});
