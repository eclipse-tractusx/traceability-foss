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

import { TreeData } from '@shared/modules/relations/model/relations.model';
import { TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import * as d3 from 'd3';
import { screen } from '@testing-library/angular';
import Tree from './tree.d3';
import { D3TreeDummyData } from './tree.d3.test.data';

// jsdom don't implement SVGGElement
(SVGElement.prototype as SVGGElement).getBBox = jest.fn().mockReturnValue({ width: 0, height: 0, x: 0, y: 0 });

describe('D3 Tree', () => {
  const id = 'id';
  const mainElement = d3.select(document.body).append('svg') as TreeSvg;
  const openDetails = jest.fn();
  const updateChildren = jest.fn();

  let treeData: TreeData;

  beforeEach(() => (treeData = { id, mainElement, openDetails, updateChildren }));

  it('should initialize tree class', () => {
    const tree = new Tree(treeData);
    const expected = {
      renderOptions: { preserveRight: 0 },
      _zoom: 1,
      _viewX: 0,
      _viewY: 0,
      _minimapConnector: { onZoom: expect.any(Function), onDrag: expect.any(Function) },
      id,
      mainElement,
      width: 1024,
      height: 568,
      r: 60,
    };

    expect(tree).toMatchObject(expected);
  });

  it('should render element borders', () => {
    const tree = new Tree(treeData);
    const treeSvg = tree.renderTree(D3TreeDummyData).node();

    expect(treeSvg.getElementsByClassName('tree--element__border-done').length).toBe(2);
    expect(treeSvg.getElementsByClassName('tree--element__border-loading').length).toBe(1);
  });

  it('should render modified text for different sizes', () => {
    new Tree(treeData);

    expect(screen.getByText('Small')).toBeInTheDocument();
    expect(screen.getByText('Long text...')).toBeInTheDocument();
  });
});
