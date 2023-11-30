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

import { TreeData, TreeDirection, TreeStructure } from '@shared/modules/relations/model/relations.model';
import { D3RenderHelper } from '@shared/modules/relations/presentation/helper/d3.render.helper';
import { HelperD3 } from '@shared/modules/relations/presentation/helper/helper.d3';
import * as d3 from 'd3';
import { ZoomBehavior, ZoomedElementBaseType, ZoomTransform } from 'd3';
import type { MinimapConnector, TreeSvg } from '../model.d3';

export class Tree {
  public static readonly CENTERING_MARGIN = 0.1;
  public readonly mainElement: TreeSvg;
  public readonly id: string;
  public readonly mainId: string;
  public readonly r: number;

  private readonly defaultZoom: number;
  private readonly zoomConfig: [ number, number ] = [ 0.2, 1.5 ];

  private currentZoom = new ZoomTransform(1, 0, 0);

  public width: number;
  public height: number;

  private readonly openDetails: (data: TreeStructure) => void;
  private readonly updateChildren: (data: TreeStructure) => void;

  private _minimapConnector: MinimapConnector = {
    onZoom: (_zoomChange: ZoomTransform) => null,
  };
  private zoom: ZoomBehavior<ZoomedElementBaseType, unknown>;
  private nextMinimapUpdate = 0;

  constructor(treeData: TreeData) {
    this.id = treeData.id;
    this.mainId = treeData.mainId;
    this.mainElement = d3.select(`#${ this.mainId }`);

    this.width = HelperD3.calculateWidth(this.mainElement);
    this.height = HelperD3.calculateHeight(this.mainElement);
    this.defaultZoom = treeData.defaultZoom;

    this.r = 60;

    this.openDetails = treeData.openDetails.bind(this);
    this.updateChildren = treeData.updateChildren.bind(this);

    this.initResizeListener();
  }

  public renderTree(data: TreeStructure, direction: TreeDirection): TreeSvg {
    const root = d3.hierarchy(data);

    let svg = d3.select(`#${ this.mainId }--camera`) as TreeSvg;
    if (svg.empty()) svg = this.creatMainSvg();

    d3.tree().nodeSize([ this.r * 3, 250 ])(root);

    D3RenderHelper.renderTreePaths(direction, svg, root, this.r, this.id);
    D3RenderHelper.renderTreeNodes(direction, svg, root, this.r, this.id, this.updateChildren, this.openDetails);
    return svg;
  }

  public changeSize(sizeChange: number): void {
    if (!this.zoom) return;
    const { k, x, y } = this.currentZoom;
    const [ min, max ] = this.zoomConfig;
    const newScale = k - sizeChange;

    if (newScale < min || newScale > max) return;

    const newTransform = new ZoomTransform(newScale, x, y);
    d3.select(`#${ this.mainId }-svg`).call(this.zoom.transform as any, newTransform);
  }

  public set minimapConnector(connector: MinimapConnector) {
    this._minimapConnector = connector;
  }

  public get minimapConnector(): MinimapConnector {
    return this._minimapConnector;
  }

  public changeViewPosition(transform: ZoomTransform): void {
    if (!this.zoom) return;
    this.nextMinimapUpdate = Date.now() + 500;
    d3.select(`#${ this.mainId }-svg`).call(this.zoom.transform as any, transform);
  }

  private creatMainSvg(): TreeSvg {
    const svg = this.mainElement
      .append('svg')
      .attr('id', this.mainId + '-svg')
      .attr('data-testid', this.mainId + '-svg')
      .attr('viewBox', [ -this.width / 3, -this.height / 2, this.width, this.height ])
      .attr('width', this.width)
      .attr('height', this.height)
      .attr('font-size', 10)
      .classed('tree--element', true);

    const cameraBody = svg
      .append('g')
      .attr('id', this.mainId + '--camera')
      .attr('data-testid', this.mainId + '--camera');

    this.zoom = d3.zoom().scaleExtent(this.zoomConfig);
    this.zoom.on('zoom', ({ transform }) => {
      this.currentZoom = transform;
      if (this.nextMinimapUpdate < Date.now()) this.minimapConnector.onZoom(transform);
      return cameraBody.attr('transform', transform);
    });

    svg.call(this.zoom);
    if (this.defaultZoom !== 1) svg.call(this.zoom.transform as any, new ZoomTransform(this.defaultZoom, 0, 0));

    return cameraBody;
  }

  private initResizeListener(): void {
    const onResize = (width: number, height: number) => {
      this.width = width;
      this.height = height;

      d3.select(`#${ this.mainId }-svg`).attr('width', this.width).attr('height', this.height);
    };
    HelperD3.initResizeListener(this.mainElement, onResize);
  }
}

export default Tree;
