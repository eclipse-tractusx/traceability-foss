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

import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { CardIconComponent } from '@shared/components/card-icon/card-icon.component';
import { SharedModule } from '@shared/shared.module';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('CardIcon', () => {
  it('should render card icon', async () => {
    await renderComponent(`<app-card-icon label='Test' stats='123'></app-card-icon>`, {
      declarations: [CardIconComponent],
      imports: [MatCardModule, MatIconModule, SharedModule],
    });

    const cardLabelElement = screen.getByText('Test');
    expect(cardLabelElement).toBeInTheDocument();

    const cardStatsElement = screen.getByText('123');
    expect(cardStatsElement).toBeInTheDocument();
  });

  it('should increment ids for multiple cards', async () => {
    await renderComponent(
      `
      <app-card-icon label='Test' stats='' icon=''></app-card-icon>
      <app-card-icon label='Test01' stats='' icon=''></app-card-icon>
    `,
      {
        declarations: [CardIconComponent],
        imports: [MatCardModule, MatIconModule, SharedModule],
      },
    );

    const cardLabelElement01 = screen.getByText('Test');
    const cardLabelElement02 = screen.getByText('Test01');

    expect(cardLabelElement01.id).not.toEqual(cardLabelElement02.id);
  });
});
