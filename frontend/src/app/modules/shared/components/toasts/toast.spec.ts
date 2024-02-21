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

import {TestBed} from '@angular/core/testing';
import {SharedModule} from '@shared/shared.module';
import {screen} from '@testing-library/angular';
import {renderComponent} from '@tests/test-render.utils';
import {ToastService} from './toast.service';

describe('toasts', () => {
  const renderToastLayout = async () => {
    await renderComponent(`<app-toast-container></app-toast-container>`, { imports: [ SharedModule ] });
    return TestBed.inject(ToastService);
  };

  it('should render success toast', async () => {
    const toastService = await renderToastLayout();
    toastService.success('some success');

    const toast = await screen.findByTestId('toast-container');
    expect(toast).toHaveClass('toast-icon-check');
  });

  it('should render info toast', async () => {
    const toastService = await renderToastLayout();
    toastService.info('some info');

    const toast = await screen.findByTestId('toast-container');

    expect(toast).toHaveClass('toast-icon-info');
  });

  it('should render warning toast', async () => {
    const toastService = await renderToastLayout();
    toastService.warning('some warning');

    const toast = await screen.findByTestId('toast-container');

    expect(toast).toHaveClass('toast-icon-warning');
  });

  it('should render error toast', async () => {
    const toastService = await renderToastLayout();
    toastService.error('some error');

    const toast = await screen.findByTestId('toast-container');

    expect(toast).toHaveClass('toast-icon-error');
  });

  it('should emit click action on toast', async () => {
    const toastService = await renderToastLayout();
    const toastActionSpy = spyOn(toastService.retryAction, 'emit')
    toastService.emitClick();
    expect(toastActionSpy).toHaveBeenCalled();
  });
});
