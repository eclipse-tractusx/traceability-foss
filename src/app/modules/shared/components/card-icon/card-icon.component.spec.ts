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

import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { CardIconComponent, SharedModule } from '@shared';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('CardIcon', () => {
  it('should render card icon', async () => {
    await renderComponent(`<app-card-icon label='Test' stats='123' icon='directions_car'></app-card-icon>`, {
      declarations: [CardIconComponent],
      imports: [MatCardModule, MatIconModule, SharedModule],
    });

    const cardLabelElement = screen.getByText('Test');
    expect(cardLabelElement).toBeInTheDocument();

    const cardStatsElement = screen.getByText('123');
    expect(cardStatsElement).toBeInTheDocument();

    const iconElement = screen.getByText('directions_car');
    expect(iconElement).toBeInTheDocument();
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
