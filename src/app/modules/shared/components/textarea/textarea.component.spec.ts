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

import { FormControl, Validators } from '@angular/forms';
import { TextareaComponent } from '@shared/components/textarea/textarea.component';
import { SharedModule } from '@shared/shared.module';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('TextareaComponent', () => {
  it('should render textarea', async () => {
    const control = new FormControl('');
    const label = 'Some label';
    await renderComponent(`<app-textarea [control]='control' [label]="'${label}'"></app-textarea>`, {
      declarations: [TextareaComponent],
      imports: [SharedModule],
      componentProperties: { control },
    });

    const textareaLabelElement = screen.getByText(label);
    expect(textareaLabelElement).toBeInTheDocument();
  });

  it('should not render error message if textarea is not touched', async () => {
    const control = new FormControl('', Validators.required);
    control.updateValueAndValidity();

    const label = 'Some label';
    await renderComponent(`<app-textarea [control]='control' [label]="'${label}'"></app-textarea>`, {
      declarations: [TextareaComponent],
      imports: [SharedModule],
      componentProperties: { control },
    });

    const textareaLabelElement = screen.queryByText('This field is required!');
    expect(control.errors).toEqual({ required: true });
    expect(textareaLabelElement).not.toBeInTheDocument();
  });

  it('should render error message if textarea is touched', async () => {
    const control = new FormControl('', Validators.required);
    control.updateValueAndValidity();
    control.markAsTouched();

    const label = 'Some label';
    await renderComponent(`<app-textarea [control]='control' [label]="'${label}'"></app-textarea>`, {
      declarations: [TextareaComponent],
      imports: [SharedModule],
      componentProperties: { control },
    });

    const textareaLabelElement = screen.getByText('This field is required!');
    expect(control.errors).toEqual({ required: true });
    expect(textareaLabelElement).toBeInTheDocument();
  });
});
