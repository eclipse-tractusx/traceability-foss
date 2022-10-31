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

import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { ConfirmModalData } from '../core/modal.model';
import { ModalComponent } from './modal.component';

describe('modalComponent', () => {
  beforeEach(() => (confirmModalData = confirmModalDataDefault));

  const confirmModalDataDefault = {
    title: 'Modal title',
    cancelText: 'Cancel text',
    confirmText: 'Confirm text',
    formGroup: undefined,
  } as ConfirmModalData;

  let confirmModalData = confirmModalDataDefault;

  // by default we use component as a string, but when need to use spyOn we pass componend class
  const renderModalComponent = (component = `<app-confirm></app-confirm>` as any) => {
    return renderComponent(component, {
      declarations: [ModalComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: confirmModalData },
        { provide: MatDialogRef, useValue: {} },
      ],
      imports: [SharedModule],
      translations: [],
      componentProperties: {},
    });
  };

  it('should render', async () => {
    await renderModalComponent();
    const title = await waitFor(() => screen.getByText(confirmModalData.title));
    expect(title).toBeInTheDocument();

    const cancelText = await waitFor(() => screen.getByText(confirmModalData.cancelText));
    expect(cancelText).toBeInTheDocument();

    const confirmText = await waitFor(() => screen.getByText(confirmModalData.confirmText));
    expect(confirmText).toBeInTheDocument();
  });

  it('should click cancel button and close', async () => {
    const { fixture } = await renderModalComponent(ModalComponent);

    const cancelTextElement = await waitFor(() => screen.getByText(confirmModalData.cancelText));
    expect(cancelTextElement).toBeInTheDocument();

    const spyOnCancel = spyOn(fixture.componentInstance as any, 'cancel').and.callThrough();
    const spyOnClose = spyOn(fixture.componentInstance as any, 'close');

    cancelTextElement.click();

    await waitFor(() => expect(spyOnCancel).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledTimes(1));
  });

  it('should click confirm button and close - no formGroup', async () => {
    const { fixture } = await renderModalComponent(ModalComponent);

    const confirmTextElement = await waitFor(() => screen.getByText(confirmModalData.confirmText));
    expect(confirmTextElement).toBeInTheDocument();

    const spyOnConfirm = spyOn(fixture.componentInstance as any, 'confirm').and.callThrough();
    const spyOnClose = spyOn(fixture.componentInstance as any, 'close');

    confirmTextElement.click();

    await waitFor(() => expect(spyOnConfirm).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledTimes(1));
  });

  it('should click confirm button and close - with valid formGroup', async () => {
    const spyOnFormGroup = jasmine.createSpyObj('FormGroup', ['markAllAsTouched', 'updateValueAndValidity'], {
      valid: true,
    });
    confirmModalData.formGroup = spyOnFormGroup as any;

    const { fixture } = await renderModalComponent(ModalComponent);

    const confirmTextElement = await waitFor(() => screen.getByText(confirmModalData.confirmText));
    expect(confirmTextElement).toBeInTheDocument();

    const spyOnConfirm = spyOn(fixture.componentInstance as any, 'confirm').and.callThrough();
    const spyOnClose = spyOn(fixture.componentInstance as any, 'close');

    confirmTextElement.click();

    await waitFor(() => expect(spyOnConfirm).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnFormGroup.markAllAsTouched).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnFormGroup.updateValueAndValidity).toHaveBeenCalledTimes(1));
  });

  it('should click confirm button but not close', async () => {
    const spyOnFormGroup = jasmine.createSpyObj('FormGroup', ['markAllAsTouched', 'updateValueAndValidity'], {
      valid: false,
    });
    confirmModalData.formGroup = spyOnFormGroup as any;

    const { fixture } = await renderModalComponent(ModalComponent);

    const confirmTextElement = await waitFor(() => screen.getByText(confirmModalData.confirmText));
    expect(confirmTextElement).toBeInTheDocument();

    const spyOnConfirm = spyOn(fixture.componentInstance as any, 'confirm').and.callThrough();
    const spyOnClose = spyOn(fixture.componentInstance as any, 'close');

    confirmTextElement.click();

    await waitFor(() => expect(spyOnConfirm).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledTimes(0));
    await waitFor(() => expect(spyOnFormGroup.markAllAsTouched).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnFormGroup.updateValueAndValidity).toHaveBeenCalledTimes(1));
  });
});
