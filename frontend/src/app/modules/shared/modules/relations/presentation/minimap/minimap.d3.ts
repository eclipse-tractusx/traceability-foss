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

import { TreeDirection, TreeStructure } from '@shared/modules/relations/model/relations.model';
import { D3RenderHelper } from '@shared/modules/relations/presentation/helper/d3.render.helper';
import { HelperD3 } from '@shared/modules/relations/presentation/helper/helper.d3';
import { MinimapIds, TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import Tree from '@shared/modules/relations/presentation/tree/tree.d3';
import * as d3 from 'd3';
import { HierarchyNode, ZoomBehavior, ZoomedElementBaseType, ZoomTransform } from 'd3';

export class Minimap {
  private ids: MinimapIds;

  private readonly scale = 20;
  private readonly r: number;
  private readonly mainElement: TreeSvg;

  private readonly treeViewportX: number;
  private readonly treeViewportY: number;

  private readonly xOffset: number;
  private yOffset: number;

  private width: number;
  private height: number;

  private currentZoom = new ZoomTransform(1, 0, 0);
  private zoom: ZoomBehavior<ZoomedElementBaseType, unknown>;
  private nextTreeUpdateAt = 0;
  private zoomChangeStore: ZoomTransform;

  constructor(private readonly treeInstance: Tree, private readonly direction: TreeDirection) {
    this.treeInstance.minimapConnector = {
      onZoom: this.onExternalZoomChange.bind(this),
    };

    this.setIds();
    this.mainElement = d3.select(`#${ this.ids.minimap }`);

    this.width = HelperD3.calculateWidth(this.mainElement);
    this.height = HelperD3.calculateHeight(this.mainElement);

    this.treeViewportX = -(this.treeInstance.width / 3 / this.scale);
    this.treeViewportY = -(this.treeInstance.height / 2 / this.scale);

    this.r = this.treeInstance.r / this.scale;

    this.xOffset = -this.r * 1.5;
    this.initResizeListener();
  }

  private setIds(): void {
    const minimap = this.treeInstance.mainId + '--minimap';
    const main = `${ minimap }--main`;
    const closeButton = `${ minimap }--closing`;
    const viewport = `${ minimap }--rect`;
    const viewportContainer = `${ minimap }--rect-group`;
    const circle = `${ minimap }--Circle`;
    const closing = `${ minimap }--closing`;
    const icon = `${ minimap }--icon`;

    this.ids = { minimap, main, closeButton, viewport, viewportContainer, circle, closing, icon };
  }

  public renderMinimap(data: TreeStructure): TreeSvg {
    d3.select(`#${ this.ids.main }`).remove();
    const root = d3.hierarchy(data);

    let svg = d3.select(`#${ this.ids.main }`) as TreeSvg;
    if (svg.empty()) svg = this.creatMainSvg(root);

    // First draw paths so paths are behind circles.
    D3RenderHelper.renderTreePaths(this.direction, svg, root, this.r, this.ids.minimap, true);
    D3RenderHelper.renderMinimapNodes(svg, root, this.r, this.ids.minimap);
    // Recalculate height after circles are drawn because of uneven distribution.
    this.setMapHeight();

    this.drawTreeViewport(svg);
    D3RenderHelper.renderMinimapClosing(
      svg,
      `${ this.ids.closing }`,
      this.xOffset,
      this.yOffset,
      this.closingEventListener.bind(this),
    );

    return svg;
  }

  private getViewBox(): number[] {
    return [ this.xOffset, this.yOffset, this.width, this.height ];
  }

  private creatMainSvg(root: HierarchyNode<TreeStructure>): TreeSvg {
    d3.tree().nodeSize([ this.r * 3, 250 / this.scale ])(root);
    this.height = HelperD3.calculateHeight(this.mainElement);

    const calculatedHeight = this.height / (root.height || 1);
    this.yOffset = this.yOffset || -calculatedHeight / 2;

    return this.mainElement
      .append('svg')
      .attr('id', this.ids.main)
      .attr('data-testid', this.ids.main)
      .attr('viewBox', this.getViewBox())
      .attr('width', this.width)
      .attr('height', this.height)
      .on('click', this.clickEventListener.bind(this));
  }

  private drawTreeViewport(svg: TreeSvg): void {
    const { width, height } = this.getBorderSize();

    const rectGroup = svg
      .append('g')
      .attr('id', this.ids.viewportContainer)
      .attr('data-testid', this.ids.viewportContainer);
    const rect = rectGroup
      .append('rect')
      .attr('x', this.treeViewportX)
      .attr('y', this.treeViewportY)
      .attr('width', width)
      .attr('height', height)
      .classed('tree--minimap', true);

    this.zoom = d3.zoom().scaleExtent([ 0, 0 ]);
    this.zoom.on('zoom', ({ transform }) => {
      const { k, x, y } = this.currentZoom;

      if (transform.k === 0) return rectGroup.call(this.zoom.transform, new ZoomTransform(1 / k, x, y));
      if (this.nextTreeUpdateAt < Date.now()) this.moveTree(transform);

      this.currentZoom = new ZoomTransform(1 / transform.k, transform.x, transform.y);
      return rect.attr('transform', this.currentZoom as any);
    });

    rectGroup.call(this.zoom);
    if (this.zoomChangeStore) this.onExternalZoomChange(this.zoomChangeStore);
  }

  private clickEventListener({ offsetX, offsetY }: MouseEvent): void {
    const xDistance = offsetX + -1 * (this.treeViewportX + this.currentZoom.x);
    const yDistance = offsetY + -1 * (this.treeViewportY + this.currentZoom.y);

    const { width, height } = this.getBorderSize();
    const xOffset = this.xOffset - width / 2;
    const yOffset = this.yOffset - height / 2;

    const total_distance_x = xDistance + xOffset;
    const total_distance_y = yDistance + yOffset;

    const { k, x, y } = this.currentZoom;
    const transform = new ZoomTransform(1 / k, x + total_distance_x, y + total_distance_y);

    this.currentZoom = transform;
    const rect = d3.select(`#${ this.ids.viewportContainer }`).transition().duration(500) as any;
    this.zoom.transform(rect, transform);
  }

  private getBorderSize(): { width: number; height: number } {
    const width = this.treeInstance.width / this.scale;
    const height = this.treeInstance.height / this.scale;
    return { width, height };
  }

  private initResizeListener(): void {
    const onResize = (width: number, height: number) => {
      this.width = width;
      this.height = height;

      this.mainElement.attr('viewBox', this.getViewBox()).attr('width', this.width).attr('height', this.height);
    };
    HelperD3.initResizeListener(this.mainElement, onResize);
  }

  private onExternalZoomChange({ k, x, y }: ZoomTransform): void {
    if (!this.zoom) {
      this.zoomChangeStore = new ZoomTransform(k, x, y);
      return;
    }

    this.nextTreeUpdateAt = Date.now() + 250;
    const transform = new ZoomTransform(k, -(x / this.scale) / k, -(y / this.scale) / k);
    d3.select(`#${ this.ids.viewportContainer }`).call(this.zoom.transform as any, transform);
  }

  private moveTree({ k, x, y }: ZoomTransform): void {
    const newTransform = new ZoomTransform(k, -x * this.scale * k, -y * this.scale * k);
    this.treeInstance.changeViewPosition(newTransform);
  }

  private setMapHeight(): void {
    const circleContainer = d3.select(`#${ this.ids.circle }`).node() as HTMLElement;
    let { lowestPoint, highestPoint } = this.getLowestAndHighestPointFromCircles(circleContainer);

    const padding = this.r / 2;
    lowestPoint -= padding;
    highestPoint += padding;

    const minHeightOfMap = -1 * lowestPoint + highestPoint + 50;
    this.height = minHeightOfMap > this.height ? minHeightOfMap : this.height;

    const minOffset = lowestPoint - 40;
    if (this.yOffset > minOffset || this.yOffset + this.height < highestPoint) {
      this.yOffset = minOffset;
    }

    d3.select(`#${ this.ids.main }`).attr('viewBox', this.getViewBox()).attr('height', this.height);
  }

  private getLowestAndHighestPointFromCircles(circlesContainer: HTMLElement) {
    return Array.from(circlesContainer.children).reduce(
      (p, c) => {
        const currentPosition = Number.parseInt(c.getAttribute('data-y'), 10);
        if (currentPosition < p.lowestPoint) p.lowestPoint = currentPosition;
        if (currentPosition > p.highestPoint) p.highestPoint = currentPosition;
        return p;
      },
      { lowestPoint: 0, highestPoint: 0 },
    );
  }

  private closingEventListener(event: MouseEvent): void {
    event?.stopPropagation();

    d3.select(`#${ this.ids.minimap }`).classed('tree--minimap__closed', true);
    d3.xml('/assets/images/layer-icon.svg')
      .then(data => {
        const closeIcon = this.mainElement.node().appendChild(data.documentElement);
        closeIcon.setAttribute('id', this.ids.icon);
        closeIcon.setAttribute('data-testid', this.ids.icon);
        closeIcon.addEventListener(
          'click',
          () => {
            d3.select(`#${ this.ids.minimap }`).classed('tree--minimap__closed', false);
            d3.select(`#${ this.ids.icon }`).remove();
          },
          { passive: true },
        );
      })
      .catch(error => {
        console.error(error);
      });
  }
}

export default Minimap;
