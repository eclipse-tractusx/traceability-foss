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

import { TreeData, TreeDirection } from '@shared/modules/relations/model/relations.model';
import { renderTree } from '@shared/modules/relations/presentation/minimap/minimap.d3.spec';
import { TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import { screen, waitFor } from '@testing-library/angular';
import * as d3 from 'd3';
import { sleepForTests } from '../../../../../../../test';
import Tree from './tree.d3';
import { D3TreeDummyData } from './tree.d3.test.data';

describe('D3 Tree', () => {
  const id = 'id';
  const openDetails = jasmine.createSpy();
  const updateChildren = jasmine.createSpy();

  let treeData: TreeData;

  beforeEach(() => {
    const mainElement = d3.select(document.body).append('svg') as TreeSvg;
    mainElement.attr('id', id);
    mainElement.attr('mainId', id);
    treeData = { id, mainId: id, openDetails, updateChildren };
  });

  afterEach(() => document.body.removeChild(document.getElementById(id)));

  it('should initialize tree class', () => {
    const tree = new Tree(treeData);
    expect(tree.renderTree).toBeTruthy();
  });

  it('should change size of tree when zoom buttons are clicked', async () => {
    const component = await renderTree();
    component.detectChanges();

    const minimapBody = await waitFor(() => screen.getByTestId('app-part-relation-0--minimap--main'));
    expect(minimapBody).toBeInTheDocument();

    // Wait for minimap to completely render
    await sleepForTests(200);

    const increaseButton = await waitFor(() => screen.getByTestId('tree--zoom__increase'));
    const decreaseButton = await waitFor(() => screen.getByTestId('tree--zoom__decrease'));
    const cameraElement = await waitFor(() => screen.getByTestId('app-part-relation-0--camera'));

    expect(cameraElement).toHaveAttribute('transform', 'translate(190,0) scale(1)');
    increaseButton.click();
    await sleepForTests(100);
    expect(cameraElement).toHaveAttribute('transform', 'translate(190,0) scale(1.1)');

    decreaseButton.click();
    await sleepForTests(100);
    expect(cameraElement).toHaveAttribute('transform', 'translate(190,0) scale(1)');
  });

  it('should render modified text for different sizes - TreeDirection.DOWN', () => {
    const tree = new Tree(treeData);
    tree.renderTree(D3TreeDummyData, TreeDirection.DOWN).node();

    expect(screen.getByText('Small')).toBeInTheDocument();
    expect(screen.getByText('Long tex...')).toBeInTheDocument();
  });

  it('should render modified text for different sizes - TreeDirection.UP', () => {
    const tree = new Tree(treeData);
    tree.renderTree(D3TreeDummyData, TreeDirection.UP).node();

    expect(screen.getByText('Small')).toBeInTheDocument();
    expect(screen.getByText('Long tex...')).toBeInTheDocument();
  });

});
