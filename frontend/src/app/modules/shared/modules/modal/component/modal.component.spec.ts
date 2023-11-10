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

import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { SharedModule } from '@shared/shared.module';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { cloneDeep } from 'lodash-es';
import { ModalData } from '../core/modal.model';
import { ModalComponent } from './modal.component';

describe('modalComponent', () => {
  beforeEach(() => (confirmModalData = cloneDeep(confirmModalDataDefault)));

  const confirmModalDataDefault = {
    title: 'Modal title',
    message: 'Modal message',
    buttonLeft: 'Cancel text',
    buttonRight: 'Confirm text',
    formGroup: undefined,
  } as ModalData;

  let confirmModalData = confirmModalDataDefault;

  // by default we use component as a string, but when need to use spyOn we pass componend class
  const renderModalComponent = (component = `<app-confirm></app-confirm>` as any) => {
    return renderComponent(component, {
      declarations: [ModalComponent],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: confirmModalData },
        { provide: MatDialogRef, useValue: { close: jasmine.createSpy() } },
      ],
      imports: [SharedModule],
    });
  };

  it('should render', async () => {
    await renderModalComponent();
    const title = await waitFor(() => screen.getByText(confirmModalData.title));
    expect(title).toBeInTheDocument();

    const cancelText = await waitFor(() => screen.getByText(confirmModalData.buttonLeft));
    expect(cancelText).toBeInTheDocument();

    const messageText = await waitFor(() => screen.getByText(confirmModalData.message));
    expect(messageText).toBeInTheDocument();

    const confirmText = await waitFor(() => screen.getByText(confirmModalData.buttonRight));
    expect(confirmText).toBeInTheDocument();
  });

  it('should click cancel button and close', async () => {
    const { fixture } = await renderModalComponent(ModalComponent);

    const cancelTextElement = await waitFor(() => screen.getByText(confirmModalData.buttonLeft));
    expect(cancelTextElement).toBeInTheDocument();

    const spyOnCancel = spyOn(fixture.componentInstance as any, 'cancel').and.callThrough();
    const spyOnClose = spyOn(fixture.componentInstance as any, 'close').and.callThrough();

    cancelTextElement.click();

    await waitFor(() => expect(spyOnCancel).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledWith(false));
  });

  it('should click esc key and close', async () => {
    const { fixture } = await renderModalComponent(ModalComponent);

    const spyOnEsc = spyOn(fixture.componentInstance as any, 'onEsc').and.callThrough();
    const spyOnClose = spyOn(fixture.componentInstance as any, 'close').and.callThrough();

    fireEvent.keyDown(screen.getByText(confirmModalData.title), { key: 'Esc', code: 'Escape' });

    await waitFor(() => expect(spyOnEsc).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledWith(false));
  });

  it('should set left button as confirm when leftIsConfirm is true', async () => {
    confirmModalData.leftIsConfirm = true;

    const { fixture } = await renderModalComponent(ModalComponent);

    const confirmTextElement = await waitFor(() => screen.getByText(confirmModalData.buttonLeft));
    expect(confirmTextElement).toBeInTheDocument();

    const spyOnConfirm = spyOn(fixture.componentInstance as any, 'confirm').and.callThrough();

    confirmTextElement.click();

    await waitFor(() => expect(spyOnConfirm).toHaveBeenCalledTimes(1));
  });

  it('should click confirm button and close - with valid formGroup', async () => {
    const spyOnFormGroup = jasmine.createSpyObj('FormGroup', ['markAllAsTouched', 'updateValueAndValidity'], {
      valid: true,
    });
    confirmModalData.formGroup = spyOnFormGroup as any;

    const { fixture } = await renderModalComponent(ModalComponent);

    const confirmTextElement = await waitFor(() => screen.getByText(confirmModalData.buttonRight));
    expect(confirmTextElement).toBeInTheDocument();

    const spyOnConfirm = spyOn(fixture.componentInstance as any, 'confirm').and.callThrough();
    const spyOnClose = spyOn(fixture.componentInstance as any, 'close').and.callThrough();

    confirmTextElement.click();

    await waitFor(() => expect(spyOnConfirm).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledWith(true));
    await waitFor(() => expect(spyOnFormGroup.markAllAsTouched).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnFormGroup.updateValueAndValidity).toHaveBeenCalledTimes(1));
  });

  it('should click confirm button but not close', async () => {
    const spyOnFormGroup = jasmine.createSpyObj('FormGroup', ['markAllAsTouched', 'updateValueAndValidity'], {
      valid: false,
    });
    confirmModalData.formGroup = spyOnFormGroup as any;

    const { fixture } = await renderModalComponent(ModalComponent);

    const confirmTextElement = await waitFor(() => screen.getByText(confirmModalData.buttonRight));
    expect(confirmTextElement).toBeInTheDocument();

    const spyOnConfirm = spyOn(fixture.componentInstance as any, 'confirm').and.callThrough();
    const spyOnClose = spyOn(fixture.componentInstance as any, 'close');

    confirmTextElement.click();

    await waitFor(() => expect(spyOnConfirm).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnClose).toHaveBeenCalledTimes(0));
    await waitFor(() => expect(spyOnFormGroup.markAllAsTouched).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(spyOnFormGroup.updateValueAndValidity).toHaveBeenCalledTimes(1));
  });

  it('should call onConfirm when the confirm button is clicked', async () => {
    const { fixture } = await renderModalComponent(ModalComponent);

    const confirmTextElement = await waitFor(() => screen.getByText(confirmModalData.buttonRight));
    expect(confirmTextElement).toBeInTheDocument();

    confirmModalData.onConfirm = jasmine.createSpy('onConfirm');

    confirmTextElement.click();

    await waitFor(() => expect(confirmModalData.onConfirm).toHaveBeenCalledTimes(1));
  });
});
