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

import { HelperD3 } from '@page/parts/relations/presentation/helper.d3';
import { TreeSvg } from '@page/parts/relations/presentation/model.d3';
import * as d3 from 'd3';

describe('D3 Helper', () => {
  d3.select('svg')?.remove();
  const mainElement = d3.select(document.body).append('svg') as TreeSvg;

  it('should init drag on element', () => {
    const mockFn = jest.fn();
    mainElement.call(HelperD3.initDrag(mainElement, mockFn));

    mainElement.node().dispatchEvent(new MouseEvent('mousedown', { view: window }));
    mainElement.node().dispatchEvent(new MouseEvent('mousemove', { view: window }));

    expect(mockFn).toHaveBeenCalledWith(0, 0);
    expect(mockFn).toHaveBeenCalledTimes(1);
    expect(mainElement.node().classList.toString()).toEqual('tree--element__grabbing');

    mainElement.node().dispatchEvent(new MouseEvent('mouseup', { view: window }));
    expect(mainElement.node().classList.toString()).toEqual('');
  });

  it('should calculate width for element', () => {
    expect(HelperD3.calculateWidth(mainElement)).toBe(0);
    expect(HelperD3.calculateWidth(null)).toBe(1024);
  });

  it('should calculate height for element', () => {
    expect(HelperD3.calculateHeight(mainElement)).toBe(0);
    expect(HelperD3.calculateHeight(null)).toBe(568);
  });

  describe('shortenText', () => {
    it('should return empty string if no text is given', () => {
      expect(HelperD3.shortenText('')).toEqual('');
    });
    it('should return spaced out string if string is shorter than 12 chars', () => {
      expect(HelperD3.shortenText('Test')).toEqual('    Test    ');
    });
    it('should return shorten string if string is longer than 12 chars', () => {
      expect(HelperD3.shortenText('Lorem ipsum dolor sit amet, consectetur adipiscing elit')).toEqual('Lorem ips...');
    });
  });
});
