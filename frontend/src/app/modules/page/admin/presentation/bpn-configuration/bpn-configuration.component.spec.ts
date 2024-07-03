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

import { AdminModule } from '@page/admin/admin.module';
import { BpnConfigurationComponent } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { sleepForTests } from '../../../../../../test';

describe('BpnConfigurationComponent', () => {
  const renderBpnConfigurationComponent = () => renderComponent(BpnConfigurationComponent, { imports: [ AdminModule ] });

  it('should create', async () => {
    await renderBpnConfigurationComponent();

    expect(await screen.getByText('pageAdmin.bpnConfig.title')).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('pageAdmin.bpnConfig.existingEntries'))).toBeInTheDocument();
  });

  it('should render data', async () => {
    await renderBpnConfigurationComponent();
    const inputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-3'))) as HTMLInputElement;
    await waitFor(() => expect(inputElement.value).toEqual('BPNL000000TESTRE'));
  });

  it('should validate bpn', async () => {
    await renderBpnConfigurationComponent();
    const inputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-0'))) as HTMLInputElement;
    const buttonElement = (await waitFor(() => screen.getByText('actions.add'))) as HTMLButtonElement;
    fireEvent.click(buttonElement);

    expect(screen.getAllByText('errorMessage.required').length).toEqual(2);

    fireEvent.input(inputElement, { target: { value: 'testText' } });
    expect(screen.getAllByText('errorMessage.required').length).toEqual(1);
    expect(screen.getByText('errorMessage.bpn')).toBeInTheDocument();

    fireEvent.input(inputElement, { target: { value: 'BPNxtesttesttest' } });
    expect(screen.getByText('errorMessage.bpn')).toBeInTheDocument();

    fireEvent.input(inputElement, { target: { value: 'BPNLtesttesttest' } });
    expect(screen.queryByText('errorMessage.bpn')).not.toBeInTheDocument();
  });

  it('should validate url', async () => {
    await renderBpnConfigurationComponent();
    const inputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-1'))) as HTMLInputElement;
    const buttonElement = (await waitFor(() => screen.getByText('actions.add'))) as HTMLButtonElement;
    fireEvent.click(buttonElement);

    expect(screen.getAllByText('errorMessage.required').length).toEqual(2);

    fireEvent.input(inputElement, { target: { value: 'https://test' } });
    expect(screen.getAllByText('errorMessage.required').length).toEqual(1);
    expect(screen.getByText('errorMessage.url')).toBeInTheDocument();

    fireEvent.input(inputElement, { target: { value: 'test' } });
    expect(screen.getByText('errorMessage.url')).toBeInTheDocument();

    fireEvent.input(inputElement, { target: { value: 'test.de' } });
    expect(screen.queryByText('errorMessage.url')).not.toBeInTheDocument();

    fireEvent.input(inputElement, { target: { value: 'https://test.de' } });
    expect(screen.queryByText('errorMessage.url')).not.toBeInTheDocument();

    fireEvent.input(inputElement, { target: { value: 'http://test.de' } });
    expect(screen.queryByText('errorMessage.url')).not.toBeInTheDocument();
  });

  it('should add valid data', async () => {
    await renderBpnConfigurationComponent();
    const bpnInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-0'))) as HTMLInputElement;
    const urlInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-1'))) as HTMLInputElement;
    const buttonElement = (await waitFor(() => screen.getByText('actions.add'))) as HTMLButtonElement;

    expect(await waitFor(() => screen.queryByTestId('BaseInputElement-27'))).not.toBeInTheDocument();
    expect(await waitFor(() => screen.queryByTestId('BaseInputElement-28'))).not.toBeInTheDocument();

    const bpn = 'BPNL123412341234';
    const url = 'test.de';
    fireEvent.input(bpnInputElement, { target: { value: bpn } });
    fireEvent.input(urlInputElement, { target: { value: url } });
    fireEvent.click(buttonElement);

    const newBpnInput = (await waitFor(() => screen.getByTestId('BaseInputElement-27'))) as HTMLInputElement;
    const newUrlInput = (await waitFor(() => screen.getByTestId('BaseInputElement-28'))) as HTMLInputElement;

    expect(newBpnInput).toBeInTheDocument();
    expect(newBpnInput.value).toEqual(bpn);
    expect(newUrlInput.value).toEqual(url);


    const removeButton = (await waitFor(() => screen.getByText('actions.remove'))) as HTMLButtonElement;
    fireEvent.click(removeButton);

    expect(await waitFor(() => screen.queryByTestId('BaseInputElement-27'))).not.toBeInTheDocument();
    expect(await waitFor(() => screen.queryByTestId('BaseInputElement-28'))).not.toBeInTheDocument();
  });

  it('should edit correctly', async () => {
    await renderBpnConfigurationComponent();
    const urlInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-4'))) as HTMLInputElement;
    const containerElement = await waitFor(() => screen.getByTestId('bpn-url-container-0'));

    expect(containerElement).not.toHaveClass('bpn-config__edit');
    expect(containerElement).not.toHaveClass('bpn-config__new');

    const originalUrl = urlInputElement.value;
    const url = 'test.de';

    fireEvent.input(urlInputElement, { target: { value: url } });

    expect(containerElement).toHaveClass('bpn-config__edit');
    expect(urlInputElement.value).toEqual(url);

    const resetIcon = await waitFor(() => screen.getByTestId('BaseInputElement-4-icon'));
    fireEvent.click(resetIcon);
    expect(containerElement).not.toHaveClass('bpn-config__edit');
    expect(urlInputElement.value).toEqual(originalUrl);
  });

  it('should change to new entry correctly', async () => {
    await renderBpnConfigurationComponent();
    const bpnInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-3'))) as HTMLInputElement;
    const containerElement = await waitFor(() => screen.getByTestId('bpn-url-container-0'));

    expect(containerElement).not.toHaveClass('bpn-config__edit');
    expect(containerElement).not.toHaveClass('bpn-config__new');

    const bpn = 'BPNLtesttesttest';

    fireEvent.input(bpnInputElement, { target: { value: bpn } });

    expect(containerElement).toHaveClass('bpn-config__new');
    expect(bpnInputElement.value).toEqual(bpn);
  });

  it('should edit correctly', async () => {
    await renderBpnConfigurationComponent();
    const urlInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-4'))) as HTMLInputElement;
    const containerElement = await waitFor(() => screen.getByTestId('bpn-url-container-0'));


    expect(containerElement).not.toHaveClass('bpn-config__edit');
    expect(containerElement).not.toHaveClass('bpn-config__new');

    const originalUrl = urlInputElement.value;
    const url = 'test.de';

    fireEvent.input(urlInputElement, { target: { value: url } });

    expect(containerElement).toHaveClass('bpn-config__edit');
    expect(urlInputElement.value).toEqual(url);

    const resetIcon = await waitFor(() => screen.getByTestId('BaseInputElement-4-icon'));
    fireEvent.click(resetIcon);
    expect(containerElement).not.toHaveClass('bpn-config__edit');
    expect(urlInputElement.value).toEqual(originalUrl);
  });

  it('should delete correctly', async () => {
    const { fixture } = await renderBpnConfigurationComponent();
    const bpnInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-3'))) as HTMLInputElement;
    expect(bpnInputElement).toBeInTheDocument();

    const deleteButton = (await waitFor(() => screen.getAllByText('actions.delete')))[0];
    fireEvent.click(deleteButton);

    // wait for animation
    await sleepForTests(1000);
    fixture.detectChanges();
    expect(await waitFor(() => screen.queryByTestId('BaseInputElement-3'))).not.toBeInTheDocument();
  });

  it('should search', async () => {
    await renderBpnConfigurationComponent();
    const searchInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-2'))) as HTMLInputElement;
    const urlInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-3'))) as HTMLInputElement;
    const firstBpnInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-3'))) as HTMLInputElement;
    const originalBpnValue = firstBpnInputElement.value;

    expect(firstBpnInputElement).toBeInTheDocument();
    fireEvent.input(searchInputElement, { target: { value: 'randomText' } });

    expect(await waitFor(() => screen.queryByTestId('BaseInputElement-3'))).not.toBeInTheDocument();
    fireEvent.input(searchInputElement, { target: { value: '' } });

    const bpnInputField = (await waitFor(() => screen.queryByTestId('BaseInputElement-27'))) as HTMLInputElement;
    expect(originalBpnValue).toEqual(bpnInputField.value);
    expect(bpnInputField).toBeInTheDocument();
    fireEvent.input(searchInputElement, { target: { value: bpnInputField.value } });

    expect(await waitFor(() => screen.queryByTestId('BaseInputElement-27'))).toBeInTheDocument();
    fireEvent.input(searchInputElement, { target: { value: urlInputElement.value } });
    expect(await waitFor(() => screen.queryByTestId('BaseInputElement-27'))).toBeInTheDocument();
  });

  it('should scroll to error element', async () => {
    await renderBpnConfigurationComponent();
    const isInViewPort = ({ top, left, bottom, right }: DOMRect) =>
      top >= 0 &&
      left >= 0 &&
      bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
      right <= (window.innerWidth || document.documentElement.clientWidth);

    const buttonElement = (await waitFor(() => screen.getByTestId('bpn-config-save-button'))) as HTMLButtonElement;
    const bpnInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-15'))) as HTMLInputElement;

    expect(bpnInputElement).toBeInTheDocument();
    expect(isInViewPort(bpnInputElement.getBoundingClientRect())).toBeFalsy();

    fireEvent.input(bpnInputElement, { target: { value: 'randomText' } });
    fireEvent.blur(bpnInputElement);
    fireEvent.click(buttonElement);
    // Wait for scroll animation
    await sleepForTests(1000);

    expect(isInViewPort(bpnInputElement.getBoundingClientRect())).toBeTruthy();
  });

  it('should save data correctly', async () => {
    const { fixture } = await renderBpnConfigurationComponent();

    const bpnInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-3'))) as HTMLInputElement;
    const urlInputElement = (await waitFor(() => screen.getByTestId('BaseInputElement-6'))) as HTMLInputElement;

    fireEvent.input(bpnInputElement, { target: { value: 'BPNLnewnewnewnew' } });
    fireEvent.input(urlInputElement, { target: { value: 'test.de' } });



    const buttonElement = (await waitFor(() => screen.getByText('actions.saveChanges'))) as HTMLButtonElement;
    expect(buttonElement).toBeInTheDocument();
    fireEvent.click(buttonElement);
    fixture.detectChanges();
    fixture.autoDetectChanges();

    const saveButtonElement = (await waitFor(() => screen.getByTestId('bpn-config-save-button'))) as HTMLButtonElement;
    expect(saveButtonElement).toBeInTheDocument();
  });
});
