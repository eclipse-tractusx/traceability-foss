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

import { TreeStructure } from '@shared/modules/relations/model/relations.model';
import { D3RenderHelper } from '@shared/modules/relations/presentation/helper/d3.render.helper';
import { HelperD3 } from '@shared/modules/relations/presentation/helper/helper.d3';
import { TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import Tree from '@shared/modules/relations/presentation/tree/tree.d3';
import * as d3 from 'd3';
import { HierarchyNode, ZoomBehavior, ZoomedElementBaseType, ZoomTransform } from 'd3';

export interface MinimapData {
  treeInstance: Tree;
}

export class Minimap {
  private ids: {
    circle: string;
    closeButton: string;
    closing: string;
    icon: string;
    main: string;
    minimap: string;
    viewport: string;
    viewportContainer: string;
  };

  private readonly scale = 20;
  private readonly r: number;
  private readonly mainElement: TreeSvg;
  private readonly treeInstance: Tree;

  private readonly treeWidth: number;
  private readonly treeHeight: number;
  private readonly treeViewportX: number;
  private readonly treeViewportY: number;

  private readonly xOffset: number;
  private yOffset: number;

  private width: number;
  private height: number;

  private maxChildDepth: number;
  private currentZoomTransform = new ZoomTransform(1, 0, 0);
  private treeZoom: ZoomBehavior<ZoomedElementBaseType, unknown>;
  private nextTreeUpdateAt = 0;

  constructor(treeData: MinimapData) {
    this.treeInstance = treeData.treeInstance;
    this.treeInstance.minimapConnector = {
      onZoom: this.onExternalZoomChange.bind(this),
    };

    this.setIds();
    this.mainElement = d3.select(`#${this.ids.minimap}`);

    this.width = HelperD3.calculateWidth(this.mainElement);
    this.height = HelperD3.calculateHeight(this.mainElement);

    this.treeWidth = this.treeInstance.width;
    this.treeHeight = this.treeInstance.height;

    this.treeViewportX = -(this.treeWidth / 3 / this.scale);
    this.treeViewportY = -(this.treeHeight / 2 / this.scale);

    this.r = this.treeInstance.r / this.scale;

    this.xOffset = -this.r * 1.5;
    this.initResizeListener();
  }

  private setIds(): void {
    const minimap = this.treeInstance.id + '--minimap';
    const main = `${minimap}--main`;
    const closeButton = `${minimap}--closing`;
    const viewport = `${minimap}--rect`;
    const viewportContainer = `${minimap}--rect-group`;
    const circle = `${minimap}--Circle`;
    const closing = `${minimap}--closing`;
    const icon = `${minimap}--icon`;
    this.ids = { minimap, main, closeButton, viewport, viewportContainer, circle, closing, icon };
  }

  public renderMinimap(data: TreeStructure): TreeSvg {
    d3.select(`#${this.ids.main}`).remove();
    const root = d3.hierarchy(data);

    let svg = d3.select(`#${this.ids.main}`) as TreeSvg;
    if (svg.empty()) svg = this.creatMainSvg(root);

    // First draw paths so paths are behind circles.
    D3RenderHelper.renderTreePaths(svg, root, this.r, this.ids.minimap, true);
    D3RenderHelper.renderMinimapNodes(svg, root, this.r, this.ids.minimap);
    // Recalculate height after circles are drawn because of uneven distribution.
    this.setMapHeight();

    this.drawTreeViewport(svg);
    D3RenderHelper.renderMinimapClosing(
      svg,
      `${this.ids.closing}`,
      this.xOffset,
      this.yOffset,
      this.closingEventListener.bind(this),
    );

    return svg;
  }

  private getViewBox(): number[] {
    return [this.xOffset, this.yOffset, this.width, this.height];
  }

  private creatMainSvg(root: HierarchyNode<TreeStructure>): TreeSvg {
    d3.tree().nodeSize([this.r * 3, 250 / this.scale])(root);
    this.maxChildDepth = this.getHighestChildCount(root.data);
    this.height = HelperD3.calculateHeight(this.mainElement);

    const calculatedHeight = this.height / (root.height || 1);
    this.yOffset = this.yOffset || -calculatedHeight / 2;

    return this.mainElement
      .append('svg')
      .attr('id', this.ids.main)
      .attr('viewBox', this.getViewBox())
      .attr('width', this.width)
      .attr('height', this.height)
      .on('click', this.clickEventListener.bind(this));
  }

  private drawTreeViewport(svg: TreeSvg): void {
    const { width, height } = this.getBorderSize();

    const rectGroup = svg.append('g').attr('id', this.ids.viewportContainer);
    const rect = rectGroup
      .append('rect')
      .attr('x', this.treeViewportX)
      .attr('y', this.treeViewportY)
      .attr('width', width)
      .attr('height', height)
      .classed('tree--minimap', true);

    this.treeZoom = d3.zoom().scaleExtent([0.2, 1.5]);
    this.treeZoom.on('zoom', ({ transform }) => {
      // ToDo: Calculate differently on minimap zoom
      if (this.nextTreeUpdateAt < Date.now()) this.moveTree(transform);

      this.currentZoomTransform = new ZoomTransform(1 / transform.k, transform.x, transform.y);
      return rect.attr('transform', this.currentZoomTransform as any);
    });

    rectGroup.call(this.treeZoom);
  }

  private clickEventListener({ offsetX, offsetY }: MouseEvent): void {
    const xDistance = offsetX + -1 * (this.treeViewportX + this.currentZoomTransform.x);
    const yDistance = offsetY + -1 * (this.treeViewportY + this.currentZoomTransform.y);

    const { width, height } = this.getBorderSize();
    const xOffset = this.xOffset - width / 2;
    const yOffset = this.yOffset - height / 2;

    const total_distance_x = xDistance + xOffset;
    const total_distance_y = yDistance + yOffset;

    const { k, x, y } = this.currentZoomTransform;
    const transform = new ZoomTransform(k, x + total_distance_x, y + total_distance_y);

    this.currentZoomTransform = transform;
    const rect = d3.select(`#${this.ids.viewportContainer}`).transition().duration(500) as any;
    this.treeZoom.transform(rect, transform);
  }

  private getBorderSize(): { width: number; height: number } {
    const width = this.treeWidth / this.scale;
    const height = this.treeHeight / this.scale;
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
    this.nextTreeUpdateAt = Date.now() + 250;

    const transform = new ZoomTransform(k, -(x / this.scale) / k, -(y / this.scale) / k);
    d3.select(`#${this.ids.viewportContainer}`).call(this.treeZoom.transform as any, transform);
  }

  private moveTree({ k, x, y }: ZoomTransform): void {
    const newTransform = new ZoomTransform(k, -x * this.scale * k, -y * this.scale * k);
    this.treeInstance.changeViewPosition(newTransform);
  }

  private setMapHeight(): void {
    const circleContainer = d3.select(`#${this.ids.circle}`).node() as HTMLElement;
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

    d3.select(`#${this.ids.main}`).attr('viewBox', this.getViewBox()).attr('height', this.height);
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

  private getHighestChildCount(data: TreeStructure): number {
    const childCount: number[] = [1];
    const countChildren = (index: number, children: TreeStructure[]): void => {
      if (!children) return;
      if (children.length) children.forEach(child => countChildren(index + 1, child.children));

      childCount[index] = children.length + (childCount[index] || 0);
    };

    countChildren(1, data.children);
    return Math.max(...childCount);
  }

  private closingEventListener(event: MouseEvent): void {
    event.stopPropagation();

    d3.select(`#${this.ids.minimap}`).classed('tree--minimap__closed', true);
    d3.xml('/assets/images/layer-icon.svg').then(data => {
      const closeIcon = this.mainElement.node().appendChild(data.documentElement);
      closeIcon.setAttribute('id', this.ids.icon);
      closeIcon.addEventListener(
        'click',
        () => {
          d3.select(`#${this.ids.minimap}`).classed('tree--minimap__closed', false);
          d3.select(`#${this.ids.icon}`).remove();
        },
        { passive: true },
      );
    });
  }
}

export default Minimap;
