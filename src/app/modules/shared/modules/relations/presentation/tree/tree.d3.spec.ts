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
import Tree from './tree.d3';
import { D3TreeDummyData } from './tree.d3.test.data';

describe('D3 Tree', () => {
  const id = 'id';
  const zoom = 10;
  const mainElement = d3.select(document.body).append('svg') as TreeSvg;
  const openDetails = jest.fn();
  const updateChildren = jest.fn();

  let treeData: TreeData;
  beforeEach(() => (treeData = { id, zoom, mainElement, openDetails, updateChildren }));

  it('should initialize tree class', () => {
    const tree = new Tree(treeData);
    const expected = {
      _zoom: zoom,
      _minimapConnector: { onZoom: (_zoomChange: number) => null, onDrag: (_x: number, _y: number) => null },
      id,
      mainElement,
      openDetails,
      updateChildren,
      width: 1024,
      height: 568,
      r: 60,
      _viewX: -90,
    };

    expect(JSON.stringify(tree)).toEqual(JSON.stringify(expected));
  });

  it('should render tree', () => {
    const tree = new Tree(treeData);

    const treeSvg = tree.renderTree(D3TreeDummyData).node();
    const treeChildren = treeSvg.children;

    const idMapping = [];
    for (let i = 0; i < treeChildren.length; i++) idMapping.push(treeChildren.item(i)?.id);

    const ids = ['id--paths', 'id--ClosingCircle', 'id--circles', 'id--statusBorder', 'id--loading', 'id--arrow'];
    expect(idMapping).toEqual(ids);
  });

  it('should render element borders', () => {
    const tree = new Tree(treeData);
    const treeSvg = tree.renderTree(D3TreeDummyData).node();

    expect(treeSvg.getElementsByClassName('tree--element__border-done').length).toBe(2);
    expect(treeSvg.getElementsByClassName('tree--element__border-loading').length).toBe(1);
  });

  it('should render modified text for different sizes', () => {
    const tree = new Tree(treeData);

    const treeSvg = tree.renderTree(D3TreeDummyData).node();
    const treeChildren = treeSvg.children;

    const circleChild = treeChildren.item(2);
    const smallText = circleChild.children.item(1).children.item(2).innerHTML;
    const longText = circleChild.children.item(2).children.item(2).innerHTML;

    expect(smallText).toBe('   Small    ');
    expect(longText).toBe('Long text...');
  });
});
