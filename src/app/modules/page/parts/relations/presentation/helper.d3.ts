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

import { TreeSvg } from '@page/parts/relations/presentation/model.d3';
import * as d3 from 'd3';
import { DragBehavior, DraggedElementBaseType } from 'd3';

export class HelperD3 {
  public static initDrag(
    element: TreeSvg,
    updateSize: (x?: number, y?: number) => void,
  ): DragBehavior<DraggedElementBaseType, unknown, unknown> {
    let start_x, start_y;
    const dragStarted = ({ x, y }): void => {
      start_x = x;
      start_y = y;
    };

    const dragged = ({ x, y }): void => {
      updateSize(start_x - x, start_y - y);
      start_y = y;
      start_x = x;
      element.classed('tree--element__grabbing', true);
    };

    const draggedEnd = (_): void => {
      element.classed('tree--element__grabbing', false);
    };

    return d3.drag().on('start', dragStarted).on('drag', dragged).on('end', draggedEnd);
  }

  public static calculateWidth(mainElement: TreeSvg): number {
    return mainElement?.node?.()?.getBoundingClientRect?.()?.width || window.innerWidth;
  }

  public static calculateHeight(mainElement: TreeSvg): number {
    return mainElement?.node?.()?.getBoundingClientRect?.()?.height || window.innerHeight - 200;
  }

  public static shortenText(text: string): string {
    if (!text) {
      return '';
    }

    if (text.length > 12) {
      return text.substring(0, 9) + '...';
    }

    if (text.length < 12) {
      const spaceCount = 12 - text.length;
      const spacerStart = new Array(Math.floor(spaceCount / 2)).fill(' ').join('');
      const spacerEnd = new Array(Math.ceil(spaceCount / 2)).fill(' ').join('');

      return `${spacerStart}${text}${spacerEnd}`;
    }
    return text;
  }
}
