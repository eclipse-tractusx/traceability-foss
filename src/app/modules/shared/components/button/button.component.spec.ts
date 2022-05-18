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

import { MatIconModule } from '@angular/material/icon';
import { screen, fireEvent } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { ButtonComponent } from './button.component';

describe('Button', () => {
  it('should render regular button', async () => {
    await renderComponent(`<app-button label="Test" (click)="clickHandler"></app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toBeInTheDocument();
    expect(screen.getByText('Test')).toHaveClass('mat-primary');
    expect(screen.getByText('Test')).toHaveAttribute('mat-button');
    expect(screen.getByText('Test')).toBeEnabled();
  });

  it('should click regular button', async () => {
    const clickHandler = jest.fn();
    await renderComponent(`<app-button label="Test" (click)="clickHandler($event)"></app-button>`, {
      declarations: [ButtonComponent],
      componentProperties: {
        clickHandler,
      },
    });

    fireEvent.click(screen.getByText('Test'));

    expect(clickHandler).toHaveBeenCalledWith(expect.objectContaining({ type: 'click' }));
  });

  it('should render accent button', async () => {
    await renderComponent(`<app-button label="Test" color="accent"></app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toHaveClass('mat-accent');
  });

  it('should render warn button', async () => {
    await renderComponent(`<app-button label="Test" color="warn"></app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toHaveClass('mat-warn');
  });

  it('should render disabled button', async () => {
    await renderComponent(`<app-button [isDisabled]="true" label="Test"></app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toBeDisabled();
  });

  it('should render raised button', async () => {
    await renderComponent(`<app-button variant="raised" label="Test"></app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toHaveAttribute('mat-raised-button');
  });

  it('should render flat button', async () => {
    await renderComponent(`<app-button variant="flat" label="Test"></app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toHaveAttribute('mat-flat-button');
  });

  it('should render stroked button', async () => {
    await renderComponent(`<app-button variant="stroked" label="Test"></app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toHaveAttribute('mat-stroked-button');
  });

  it('should render stroked button', async () => {
    await renderComponent(`<app-button variant="stroked" label="Test"></app-button>`, {
      declarations: [ButtonComponent],
    });

    expect(screen.getByText('Test')).toHaveAttribute('mat-stroked-button');
  });

  it('should render icon button', async () => {
    await renderComponent(`<app-button variant="icon" iconName="home" label="Test"></app-button>`, {
      declarations: [ButtonComponent],
      imports: [MatIconModule],
    });

    expect(screen.getByText('home')).toHaveClass('mat-icon');
    expect(screen.getByText('Test')).toHaveAttribute('mat-icon-button');
  });

  it('should render fab button', async () => {
    await renderComponent(`<app-button variant="fab" iconName="home" label="Test"></app-button>`, {
      declarations: [ButtonComponent],
      imports: [MatIconModule],
    });

    expect(screen.getByText('home')).toHaveClass('mat-icon');
    expect(screen.getByText('Test')).toHaveAttribute('mat-fab');
  });

  it('should render mini fab button', async () => {
    await renderComponent(`<app-button variant="miniFab" iconName="home" label="Test"></app-button>`, {
      declarations: [ButtonComponent],
      imports: [MatIconModule],
    });

    expect(screen.getByText('home')).toHaveClass('mat-icon');
    expect(screen.getByText('Test')).toHaveAttribute('mat-mini-fab');
  });
});
