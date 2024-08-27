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

import { TreeSvg } from '@shared/modules/relations/presentation/model.d3';
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

  public static calculateWidth(element: TreeSvg): number {
    return element?.node?.()?.getBoundingClientRect?.()?.width || window.innerWidth;
  }

  public static calculateHeight(element: TreeSvg): number {
    return element?.node?.()?.getBoundingClientRect?.()?.height || window.innerHeight - 200;
  }

  public static shortenText(text: string, maxLength = 11): string {
    if (!text) {
      return '';
    }

    const shortenedLength = maxLength - 3;

    if (text.length > maxLength) {
      return text.substring(0, shortenedLength) + '...';
    }

    if (text.length < maxLength) {
      const spaceCount = maxLength - text.length;
      const spacerStart = new Array(Math.floor(spaceCount / 2)).fill(' ').join('');
      const spacerEnd = new Array(Math.ceil(spaceCount / 2)).fill(' ').join('');

      return `${spacerStart}${text}${spacerEnd}`;
    }
    return text;
  }

  public static initResizeListener(element: TreeSvg, onResize: (width: number, height: number) => void): void {
    const listener = () => onResize(HelperD3.calculateWidth(element), HelperD3.calculateHeight(element));
    window.addEventListener('resize', listener, { passive: true });
  }
}
