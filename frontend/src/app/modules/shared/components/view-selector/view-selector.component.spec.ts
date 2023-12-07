/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import { ButtonComponent } from '@shared/components/button/button.component';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { fireEvent, screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('AppViewSelector', () => {

  it('should click regular button', async () => {
    const clickHandler = jasmine.createSpy();
    const fixture = await renderComponent(`<app-view-selector (click)="clickHandler($event)">Test</app-view-selector>`, {
      declarations: [ ButtonComponent ],
      imports: [ SharedModule, TemplateModule ],
      componentProperties: {
        clickHandler,
      },
    });

    const buttonEl = screen.getByText('Test').parentNode;

    fireEvent.click(buttonEl);
    fixture.detectChanges();

    expect(clickHandler).toHaveBeenCalledWith(jasmine.objectContaining({ type: 'click' }));
  });

  it('should render accent button', async () => {
    await renderComponent(`<app-view-selector color="accent">Test</app-view-selector>`, {
      declarations: [ ButtonComponent ],
      imports: [ SharedModule, TemplateModule ],
    });

    expect(screen.getByText('Test').parentNode).toHaveClass('mat-accent');
  });

  it('should render warn button', async () => {
    await renderComponent(`<app-view-selector color="warn">Test</app-view-selector>`, {
      declarations: [ ButtonComponent ],
      imports: [ SharedModule, TemplateModule ],
    });

    expect(screen.getByText('Test').parentNode).toHaveClass('mat-warn');
  });

  it('should render enabled button', async () => {
    await renderComponent(`<app-view-selector [isDisabled]="false">Test</app-view-selector>`, {
      declarations: [ ButtonComponent ],
      imports: [ SharedModule, TemplateModule ],
    });

    const buttonEl = screen.getByText('Test').parentNode;

    expect(buttonEl).toBeEnabled();
    expect(buttonEl).not.toHaveAttribute('disabled');
  });

  it('should render disabled button', async () => {
    await renderComponent(`<app-view-selector [isDisabled]="true">Test</app-view-selector>`, {
      declarations: [ ButtonComponent ],
      imports: [ SharedModule, TemplateModule ],
    });

    const buttonEl = screen.getByText('Test').parentNode;
    expect(buttonEl).toBeDisabled();
  });
});
