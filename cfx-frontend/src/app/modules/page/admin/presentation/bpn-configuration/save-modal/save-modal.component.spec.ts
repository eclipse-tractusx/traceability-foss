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

import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { AfterViewInit, Component, Input, ViewChild } from '@angular/core';
import { of } from 'rxjs';
import { ChangedInformation } from '@page/admin/presentation/bpn-configuration/bpn-configuration.model';
import { SaveBpnConfigModal } from '@page/admin/presentation/bpn-configuration/save-modal/save-modal.component';
import { BpnConfig } from '@page/admin/core/admin.model';
import { AdminModule } from '@page/admin/admin.module';

@Component({
  selector: '',
  template:
    '<app-toast-container></app-toast-container><app-save-bpn-config-modal [updateCall]="call"></app-save-bpn-config-modal>',
})
class SaveModalComponent implements AfterViewInit {
  @ViewChild(SaveBpnConfigModal) modal: SaveBpnConfigModal;
  @Input() changedInformation: ChangedInformation;
  @Input() originalValues: BpnConfig[];
  public call = (id: string) => of(null);

  public ngAfterViewInit() {
    this.modal.show(this.changedInformation, this.originalValues);
  }
}

export const renderSaveModal = async (changedInformation: ChangedInformation, originalValues: BpnConfig[]) => {
  const { fixture } = await renderComponent(SaveModalComponent, {
    declarations: [SaveModalComponent, SaveBpnConfigModal],
    imports: [AdminModule, SharedModule, TemplateModule],
    componentProperties: { changedInformation, originalValues },
  });

  return fixture;
};

describe('SaveBpnConfigModal', () => {
  const originalValues: BpnConfig[] = [
    { bpn: '123', url: 'test.de' },
    { bpn: '321', url: 'test.com' },
  ];
  const changedInformation: ChangedInformation = {
    added: [{ bpn: '111', url: 'newUrl.com' }],
    changed: [{ ...originalValues[1], url: 'newUrl.de' }],
    deleted: [originalValues[0]],
  };

  it('should create save modal', async () => {
    await renderSaveModal(changedInformation, originalValues);
    const title = await waitFor(() => screen.getByText('pageAdmin.bpnConfig.modal.title'));
    const hint = await waitFor(() => screen.getByText('pageAdmin.bpnConfig.modal.description'));
    const buttonL = await waitFor(() => screen.getByText('actions.cancel'));
    const buttonR = await waitFor(() => screen.getByText('actions.save'));

    expect(title).toBeInTheDocument();
    expect(hint).toBeInTheDocument();
    expect(buttonL).toBeInTheDocument();
    expect(buttonR).toBeInTheDocument();
  });

  it('should render investigation description', async () => {
    await renderSaveModal(changedInformation, originalValues);
    const deletedEntry = await waitFor(() => screen.getByText(changedInformation.deleted[0].url));
    const changedEntry = await waitFor(() => screen.getByText(changedInformation.changed[0].url));
    const originalEntry = await waitFor(() => screen.getByText(originalValues[1].url));
    const addedEntry = await waitFor(() => screen.getByText(changedInformation.added[0].url));

    expect(deletedEntry).toBeInTheDocument();
    expect(changedEntry).toBeInTheDocument();
    expect(originalEntry).toBeInTheDocument();
    expect(addedEntry).toBeInTheDocument();
  });

  it('should call close function', async () => {
    await renderSaveModal(changedInformation, originalValues);

    fireEvent.click(await waitFor(() => screen.getByText('actions.save')));

    await waitFor(() => expect(screen.getByText('pageAdmin.bpnConfig.modal.successfullySaved')).toBeInTheDocument());
  });
});
