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

import Minimap, { MinimapData } from '@shared/modules/relations/presentation/minimap/minimap.d3';
import { TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import Tree from '@shared/modules/relations/presentation/tree/tree.d3';
import * as d3 from 'd3';
import { D3TreeDummyData } from '../tree/tree.d3.test.data';

describe('D3 Tree', () => {
  window.innerWidth = 1024;
  window.innerHeight = 768;
  const id = 'id';
  const mainElement = d3.select(document.body).append('svg') as TreeSvg;
  const openDetails = jasmine.createSpy();
  const updateChildren = jasmine.createSpy();
  const treeInstance = new Tree({ id, mainElement, openDetails, updateChildren });

  let minimapData: MinimapData;
  beforeEach(() => (minimapData = { id, mainElement, treeInstance }));

  it('should initialize minimap class', () => {
    const tree = new Minimap(minimapData);
    const expectedTreeInstance = {
      // treeInstance: {
      renderOptions: {
        preserveRight: 0,
      },
      _zoom: 1,
      id,
      mainElement,
      width: mainElement?.node?.()?.getBoundingClientRect?.()?.width,
      height: mainElement?.node?.()?.getBoundingClientRect?.()?.height,
      r: 60,
      _viewX: 0,
      _viewY: 0,
      // },
    };
    const expected = {
      scale: 20,
      isMinimapClosed: false,
      id,
      mainElement,
      width: mainElement?.node?.()?.getBoundingClientRect?.()?.width,
      height: mainElement?.node?.()?.getBoundingClientRect?.()?.height,
      minimapX: 0,
      minimapY: 0,
      r: 3,
      zoom: 0.05,
      xOffset: -4.5,
    };

    expect(tree).toEqual(jasmine.objectContaining(expected));
    expect(tree).toEqual(jasmine.objectContaining({ treeInstance: jasmine.objectContaining(expectedTreeInstance) }));
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
