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

import { TreeData, TreeStructure } from '@page/parts/relations/model/relations.model';
import RelationTree from '@page/parts/relations/presentation/d3.tree';
import { D3TreeDummyData } from '@page/parts/relations/presentation/d3.tree.test.data';
import { Selection } from 'd3-selection';
import * as d3 from 'd3';

describe('D3 Tree', () => {
  const id = 'id';
  const zoom = 10;
  const mainElement: Selection<Element, TreeStructure, HTMLElement, TreeStructure> = d3.select(document.body);
  const openDetails = jest.fn();
  const updateChildren = jest.fn();

  let treeData: TreeData;
  beforeEach(() => (treeData = { id, zoom, mainElement, openDetails, updateChildren }));

  it('should initialize tree class', () => {
    const tree = new RelationTree(treeData);

    expect(tree).toEqual({
      id,
      mainElement,
      openDetails,
      updateChildren,
      _zoom: zoom,
      height: 568,
      width: 1024,
      r: 60,
      viewX: -60,
    });
  });

  it('should render tree', () => {
    const tree = new RelationTree(treeData);

    const treeSvg = tree.renderTree(D3TreeDummyData).node();
    const treeChildren = treeSvg.children;

    const idMapping = [];
    for (let i = 0; i < treeChildren.length; i++) idMapping.push(treeChildren.item(i)?.id);

    const ids = ['id--paths', 'id--ClosingCircle', 'id--circles', 'id--statusBorder', 'id--loading', 'id--arrow'];
    expect(idMapping).toEqual(ids);
  });

  it('should render element borders', () => {
    const tree = new RelationTree(treeData);
    const treeSvg = tree.renderTree(D3TreeDummyData).node();

    expect(treeSvg.getElementsByClassName('tree--element__border-done').length).toBe(2);
    expect(treeSvg.getElementsByClassName('tree--element__border-loading').length).toBe(1);
  });

  it('should render modified text for different sizes', () => {
    const tree = new RelationTree(treeData);

    const treeSvg = tree.renderTree(D3TreeDummyData).node();
    const treeChildren = treeSvg.children;

    const circleChild = treeChildren.item(2);
    const smallText = circleChild.children.item(1).children.item(2).innerHTML;
    const longText = circleChild.children.item(2).children.item(2).innerHTML;

    expect(smallText).toBe('   Small    ');
    expect(longText).toBe('Long text...');
  });
});
