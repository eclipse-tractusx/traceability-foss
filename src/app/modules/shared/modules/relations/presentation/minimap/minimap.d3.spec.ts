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

import { ActivatedRoute } from '@angular/router';
import { PartsModule } from '@page/parts/parts.module';
import { PartRelationComponent } from '@shared/modules/relations/presentation/part-relation.component';
import { RelationsModule } from '@shared/modules/relations/relations.module';
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
import { renderComponent } from '@tests/test-render.utils';
import { BehaviorSubject } from 'rxjs';
import { delay } from 'rxjs/operators';
import { MOCK_part_1 } from '../../../../../../mocks/services/parts-mock/parts.test.model';

describe('D3 Minimap', () => {
  beforeAll(() => server.start({ onUnhandledRequest: 'bypass' }));
  afterEach(() => server.resetHandlers());
  afterAll(() => server.stop());

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
    expect(await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--main'))).not.toBeInTheDocument();
  });

  it('should render minimap', async () => {
    await renderBase();

    expect((await waitFor(() => screen.getAllByTestId('node'))).length).toEqual(6);
    expect((await waitFor(() => screen.getAllByTestId('tree--element__path'))).length).toEqual(2);
  });

  it('should render minimap status colors', async done => {
    await renderBase();
    await setTimeout(async () => {
      expect((await waitFor(() => screen.getAllByTestId('tree--element__circle-done'))).length).toBe(2);
      expect((await waitFor(() => screen.getAllByTestId('tree--element__circle-loading'))).length).toBe(1);
      done();
    }, 3000);
  });

  /*
  it('should close minimap', async () => {
    await renderBase();
    const minimap = new Minimap(treeInstance);
    const minimapSvg = minimap.renderMinimap(D3TreeDummyData).node();
    const closeButton = await waitFor(() => screen.getByTestId('id--minimap--closing'));
    expect(minimapSvg.getElementsByClassName('tree--minimap__closed').length).toBe(0);
    expect(closeButton).toBeInTheDocument();
    console.log(closeButton);
    closeButton.click();
    expect(minimapSvg.getElementsByClassName('tree--minimap__closed').length).toBe(0);
  });*/
});
