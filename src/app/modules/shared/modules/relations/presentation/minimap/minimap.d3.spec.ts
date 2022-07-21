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

import Minimap, { MinimapData } from '@shared/modules/relations/presentation/minimap/minimap.d3';
import { TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import Tree from '@shared/modules/relations/presentation/tree/tree.d3';
import * as d3 from 'd3';
import { D3TreeDummyData } from '../tree/tree.d3.test.data';

describe('D3 Tree', () => {
  const id = 'id';
  const zoom = 10;
  const mainElement = d3.select(document.body).append('svg') as TreeSvg;
  const openDetails = jest.fn();
  const updateChildren = jest.fn();
  const treeInstance = new Tree({ id, zoom, mainElement, openDetails, updateChildren });

  let minimapData: MinimapData;
  beforeEach(() => (minimapData = { id, mainElement, treeInstance }));

  it('should initialize minimap class', () => {
    const tree = new Minimap(minimapData);
    const expected = {
      scale: 20,
      isMinimapClosed: false,
      id,
      treeInstance: {
        _zoom: zoom,
        _minimapConnector: {},
        id,
        mainElement,
        width: 0,
        height: 0,
        r: 60,
        _viewX: -90,
      },
      mainElement,
      width: 0,
      height: 0,
      minimapX: 0,
      minimapY: 0,
      r: 3,
      zoom: 0.05,
      xOffset: -4.5,
    };

    expect(JSON.stringify(tree)).toEqual(JSON.stringify(expected));
  });

  it('should render minimap', () => {
    const minimap = new Minimap(minimapData);

    const minimapSvg = minimap.renderMinimap(D3TreeDummyData).node();
    const minimapChildren = minimapSvg.children;

    const idMapping = [];
    for (let i = 0; i < minimapChildren.length; i++) idMapping.push(minimapChildren.item(i)?.id);

    const ids = ['id--paths', 'id--circles', 'id-rect', 'id-closing'];
    expect(idMapping).toEqual(ids);
  });

  it('should render minimap status colors', () => {
    const minimap = new Minimap(minimapData);
    const minimapSvg = minimap.renderMinimap(D3TreeDummyData).node();

    expect(minimapSvg.getElementsByClassName('tree--element__circle-done').length).toBe(2);
    expect(minimapSvg.getElementsByClassName('tree--element__circle-loading').length).toBe(1);
  });
});
