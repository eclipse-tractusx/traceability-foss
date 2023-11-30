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
import * as d3 from 'd3';
import { BehaviorSubject } from 'rxjs';
import { delay } from 'rxjs/operators';
import { sleepForTests } from '../../../../../../../test';
import { MOCK_part_1 } from '../../../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';

export const renderTree = async () => {
  return renderComponent(
    '<app-part-relation style="width: 1000px; height: 1000px; position: absolute"></app-part-relation>',
    {
      declarations: [ PartRelationComponent ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: new BehaviorSubject({ get: () => MOCK_part_1.id }).pipe(delay(10)),
          },
        },
      ],
      imports: [ RelationsModule, PartsModule ],
    },
  );
};

describe('D3 Minimap', () => {
  it('should initialize minimap class', async () => {
    await renderTree();
    expect(await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--main'))).toBeInTheDocument();
  });

  it('should render minimap status colors', async () => {
    const component = await renderTree();
    component.detectChanges();
    expect((await waitFor(() => screen.getAllByTestId('tree--element__circle-BATCH'))).length).toBe(1);
  });

  it('should close minimap', async () => {
    const component = await renderTree();
    component.detectChanges();
    expect(await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--main'))).toBeInTheDocument();

    const closeButton = await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--closing'));
    expect(closeButton).toBeInTheDocument();

    const closeButtonD3 = d3.select('#app-part-relation-0--minimap--closing');
    closeButtonD3.on('click').call(closeButtonD3.node(), closeButtonD3.datum());
    expect((await waitFor(() => screen.getAllByTestId('app-part-relation-0--minimap--icon'))).length).toBe(1);
  });

  it('should click on minimap and move view', async () => {
    const component = await renderTree();
    component.detectChanges();

    const minimapBody = await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--main'));
    expect(minimapBody).toBeInTheDocument();

    // Wait for minimap to completely render
    await sleepForTests(200);
    const mainBodyD3 = d3.select('#app-part-relation-0--minimap--main');
    mainBodyD3.on('click').call(mainBodyD3.node(), { offsetX: 0, offsetY: 0 });

    const viewportContainer = screen.getByTestId('app-part-relation-0--minimap--rect-group').firstChild;

    // Wait for minimap to completely render wait for animation (500 ms)
    await sleepForTests(1000);
    const expectedTransform = 'translate(-12.833333333333336,-90) scale(1)';
    expect(viewportContainer).toHaveAttribute('transform', expectedTransform);
  });
});
