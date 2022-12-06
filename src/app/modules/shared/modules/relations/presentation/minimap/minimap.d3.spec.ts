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

import { ActivatedRoute } from '@angular/router';
import { PartsModule } from '@page/parts/parts.module';
import { PartRelationComponent } from '@shared/modules/relations/presentation/part-relation.component';
import { RelationsModule } from '@shared/modules/relations/relations.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { BehaviorSubject } from 'rxjs';
import { delay } from 'rxjs/operators';
import { MOCK_part_1 } from '../../../../../../mocks/services/parts-mock/parts.test.model';

describe('D3 Minimap', () => {
  const renderBase = async () => {
    return renderComponent('<app-part-relation></app-part-relation>', {
      declarations: [PartRelationComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: new BehaviorSubject({ get: () => MOCK_part_1.id }).pipe(delay(10)),
          },
        },
      ],
      imports: [RelationsModule, PartsModule],
    });
  };

  it('should initialize minimap class', async () => {
    await renderBase();
    expect(await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--main'))).toBeInTheDocument();
  });

  it('should render minimap', async () => {
    await renderBase();

    expect((await waitFor(() => screen.getAllByTestId('node'))).length).toEqual(6);
    expect((await waitFor(() => screen.getAllByTestId('tree--element__path'))).length).toEqual(2);
  });

  it('should render minimap status colors', async () => {
    const component = await renderBase();
    component.detectChanges();
    expect((await waitFor(() => screen.getAllByTestId('tree--element__circle-done'))).length).toBe(1);
    expect((await waitFor(() => screen.getAllByTestId('tree--element__circle-loading'))).length).toBe(2);
  });

  it('should close minimap', async () => {
    const component = await renderBase();
    component.detectChanges();
    expect(await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--main'))).toBeInTheDocument();
    const closeButton = await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--closing-text'));
    expect(closeButton).toBeInTheDocument();
    closeButton.dispatchEvent(new Event('click'));
    expect((await waitFor(() => screen.getAllByTestId('app-part-relation-0--minimap--icon'))).length).toBe(0);
  });
});
