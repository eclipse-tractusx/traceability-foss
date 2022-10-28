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

import { MatIconModule } from '@angular/material/icon';
import { ButtonComponent } from '@shared/components/button/button.component';
import { fireEvent, screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('Button', () => {
  it('should render regular button', async () => {
    await renderComponent(`<app-button (click)="clickHandler">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    const buttonEl = screen.getByText('Test');

    expect(buttonEl).toBeInTheDocument();
    expect(buttonEl).toHaveAttribute('mat-button');
    expect(buttonEl).toBeEnabled();
  });

  it('should click regular button', async () => {
    const clickHandler = jasmine.createSpy();
    await renderComponent(`<app-button (click)="clickHandler($event)">Test</app-button>`, {
      declarations: [ButtonComponent],
      componentProperties: {
        clickHandler,
      },
    });

    const buttonEl = screen.getByText('Test');

    fireEvent.click(buttonEl);

    expect(clickHandler).toHaveBeenCalledWith(jasmine.objectContaining({ type: 'click' }));
  });

  it('should render accent button', async () => {
    await renderComponent(`<app-button color="accent">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toHaveClass('mat-accent');
  });

  it('should render warn button', async () => {
    await renderComponent(`<app-button color="warn">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toHaveClass('mat-warn');
  });

  it('should render enabled button', async () => {
    await renderComponent(`<app-button [isDisabled]="false">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    const buttonEl = screen.getByText('Test');

    expect(buttonEl).toBeEnabled();
    expect(buttonEl).not.toHaveAttribute('disabled');
  });

  it('should render disabled button', async () => {
    await renderComponent(`<app-button [isDisabled]="true">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    const buttonEl = screen.getByText('Test');

    expect(buttonEl).toBeDisabled();
  });

  it('should render raised button', async () => {
    await renderComponent(`<app-button variant="raised">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    const buttonEl = screen.getByText('Test');

    expect(buttonEl).toHaveAttribute('mat-raised-button');
  });

  it('should render flat button', async () => {
    await renderComponent(`<app-button variant="flat">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    const buttonEl = screen.getByText('Test');

    expect(buttonEl).toHaveAttribute('mat-flat-button');
  });

  it('should render stroked button', async () => {
    await renderComponent(`<app-button variant="stroked">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    const buttonEl = screen.getByText('Test');

    expect(buttonEl).toHaveAttribute('mat-stroked-button');
  });

  it('should render stroked button', async () => {
    await renderComponent(`<app-button variant="stroked">Test</app-button>`, {
      declarations: [ButtonComponent],
    });

    const buttonEl = screen.getByText('Test');

    expect(buttonEl).toHaveAttribute('mat-stroked-button');
  });

  it('should render icon button', async () => {
    await renderComponent(`<app-button variant="icon" iconName="home">Test</app-button>`, {
      declarations: [ButtonComponent],
      imports: [MatIconModule],
    });

    const buttonEl = screen.getByText('Test');
    const iconEl = screen.getByText('home');

    expect(iconEl).toHaveClass('mat-icon');
    expect(buttonEl).toHaveAttribute('mat-icon-button');
  });

  it('should render fab button', async () => {
    await renderComponent(`<app-button variant="fab" iconName="home">Test</app-button>`, {
      declarations: [ButtonComponent],
      imports: [MatIconModule],
    });

    const buttonEl = screen.getByText('Test');
    const iconEl = screen.getByText('home');

    expect(iconEl).toHaveClass('mat-icon');
    expect(buttonEl).toHaveAttribute('mat-fab');
  });

  it('should render mini fab button', async () => {
    await renderComponent(`<app-button variant="miniFab" iconName="home">Test</app-button>`, {
      declarations: [ButtonComponent],
      imports: [MatIconModule],
    });

    const buttonEl = screen.getByText('Test');
    const iconEl = screen.getByText('home');

    expect(iconEl).toHaveClass('mat-icon');
    expect(buttonEl).toHaveAttribute('mat-mini-fab');
  });
});
