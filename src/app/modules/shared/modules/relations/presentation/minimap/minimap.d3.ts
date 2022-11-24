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
import { HelperD3 } from '@shared/modules/relations/presentation/helper/helper.d3';
import { D3RenderHelper } from '@shared/modules/relations/presentation/helper/d3.render.helper';
import { TreeNode, TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import Tree from '@shared/modules/relations/presentation/tree/tree.d3';
import * as d3 from 'd3';
import { HierarchyNode, ZoomBehavior, ZoomedElementBaseType, ZoomTransform } from 'd3';
import { HierarchyCircularLink, HierarchyCircularNode } from 'd3-hierarchy';

export interface MinimapData {
  id: string;
  mainElement?: TreeSvg;
  treeInstance: Tree;
}

export class Minimap {
  private readonly id: string;
  private readonly scale = 20;
  private readonly r: number;
  private readonly mainElement: TreeSvg;
  private readonly treeInstance: Tree;

  private readonly xOffset: number;
  private yOffset: number;

  private width: number;
  private height: number;

  private treeWidth: number;
  private treeHeight: number;

  private minimapX: number;
  private minimapY: number;

  private isMinimapClosed = false;
  private maxChildDepth: number;
  private currentZoomTransform = new ZoomTransform(1, 0, 0);
  private treeZoom: ZoomBehavior<ZoomedElementBaseType, unknown>;
  private minimapTrigger = 0;

  constructor(treeData: MinimapData) {
    this.id = treeData.id;

    this.treeInstance = treeData.treeInstance;
    this.treeInstance.minimapConnector = {
      onZoom: this.onZoomChange.bind(this),
    };

    this.mainElement = treeData.mainElement;

    this.width = HelperD3.calculateWidth(this.mainElement);
    this.height = HelperD3.calculateHeight(this.mainElement);

    this.treeWidth = HelperD3.calculateWidth(this.treeInstance.mainElement);
    this.treeHeight = HelperD3.calculateHeight(this.treeInstance.mainElement);

    this.minimapX = -(this.treeWidth / 3 / this.scale);
    this.minimapY = -(this.treeHeight / 2 / this.scale);

    this.r = this.treeInstance.r / this.scale;

    this.xOffset = -this.r * 1.5;
    this.initResizeListener();
  }

  public renderMinimap(data: TreeStructure): TreeSvg {
    d3.select(`#${this.id}-svg`).remove();
    const root = d3.hierarchy(data);
    const svg = this.creatMainSvg(root);
    // First draw paths so paths are behind circles.
    D3RenderHelper.renderTreePaths(svg, root, this.r, this.id, true);
    D3RenderHelper.renderMinimapNodes(svg, root, this.r, this.id);
    // Recalculate height after circles are drawn because of uneven distribution.
    this.calculateMapHeight();

    this.addBorder(svg);
    this.addClosing(svg);

    return svg;
  }

  private creatMainSvg(root: HierarchyNode<TreeStructure>): TreeSvg {
    d3.tree().nodeSize([this.r * 3, 250 / this.scale])(root);
    this.maxChildDepth = this.calculateChildCount(root.data);
    this.height = HelperD3.calculateHeight(this.mainElement);

    const calculatedHeight = this.height / (root.height || 1);
    this.yOffset = this.yOffset || -calculatedHeight / 2;

    return this.mainElement
      .append('svg')
      .attr('id', this.id + '-svg')
      .attr('viewBox', this.getViewBox())
      .attr('width', this.width)
      .attr('height', this.height)
      .on('click', this.clickEventListener.bind(this));
  }

  // Darek: draw rect angle that mirrors view border of tree
  private addBorder(svg: TreeSvg): void {
    const id = this.id + '-rect';
    const { width, height } = this.getBorderSize();

    const rectGroup = svg.append('g').attr('id', id + '-group');
    const rect = rectGroup
      .append('rect')
      .attr('id', id)
      .attr('x', this.minimapX)
      .attr('y', this.minimapY)
      .attr('width', width)
      .attr('height', height)
      .classed('tree--minimap', true);

    // Darek: This is the zoom listener. Here it is only created
    this.treeZoom = d3
      .zoom()
      .extent([
        [this.xOffset, this.yOffset],
        [this.width, this.height],
      ])
      .scaleExtent([0.6666, 5])
      .on('zoom', ({ transform }) => {
        if (this.minimapTrigger < Date.now()) {
          this.moveTree(transform);
        }
        return rect.attr('transform', transform);
      });

    // Darek: here the listener is assigned to the rectangle group.
    // The listener needs to be on the element above otherwise it will stutter like hell.
    // You can test that by replacing "const rect = rectGroup" => "const rect = svg"
    // and rectGroup.call(this.treeZoom, d3.zoomIdentity); => rect.call(this.treeZoom, d3.zoomIdentity);
    rectGroup.call(this.treeZoom, this.treeInstance.zoomIdentity);
  }

  private addClosing(svg: TreeSvg): void {
    const id = `${this.id}-closing`;
    const closingButton = svg
      .append('a')
      .attr('id', id)
      .on('click', this.closingEventListener.bind(this))
      .classed('tree--minimap__closing', true);

    closingButton
      .append('text')
      .attr('id', `${id}-text`)
      .attr('x', this.xOffset + 20)
      .attr('y', this.yOffset + 25)
      .attr('width', 20)
      .attr('height', 20)
      .text(' x ')
      .classed('tree--element__text', true);
  }

  private clickEventListener({ offsetX, offsetY }: MouseEvent): void {
    const xDistance = offsetX + -1 * (this.minimapX + this.currentZoomTransform.x);
    const yDistance = offsetY + -1 * (this.minimapY + this.currentZoomTransform.y);

    const { width, height } = this.getBorderSize();
    const xOffset = this.xOffset - width / 2;
    const yOffset = this.yOffset - height / 2;

    const total_distance_x = xDistance + xOffset;
    const total_distance_y = yDistance + yOffset;

    const { k, x, y } = this.currentZoomTransform;
    const newTransform = new ZoomTransform(k, x + total_distance_x, y + total_distance_y);

    this.moveRect(newTransform);
  }

  private closingEventListener(event: MouseEvent): void {
    event.stopPropagation();

    const id = `${this.id}-closing`;
    const iconId = `${id}-icon`;
    this.isMinimapClosed = true;
    d3.select(`#${this.id}`).classed('tree--minimap__closed', this.isMinimapClosed);

    d3.xml('/assets/images/layer-icon.svg').then(data => {
      const mapIconElement = this.mainElement.node().appendChild(data.documentElement);

      mapIconElement.setAttribute('id', iconId);
      mapIconElement.addEventListener(
        'click',
        () => {
          this.isMinimapClosed = false;
          d3.select(`#${this.id}`).classed('tree--minimap__closed', this.isMinimapClosed);
          d3.select(`#${iconId}`).remove();
        },
        { passive: true },
      );
    });
  }

  private getBorderSize(): { width: number; height: number } {
    const width = this.treeWidth / this.scale;
    const height = this.treeHeight / this.scale;
    return { width, height };
  }

  private initResizeListener(): void {
    window.addEventListener(
      'resize',
      _ => {
        this.width = HelperD3.calculateWidth(this.mainElement);
        this.height = HelperD3.calculateHeight(this.mainElement);

        d3.select(`#${this.id}-svg`)
          .attr('viewBox', this.getViewBox())
          .attr('width', this.width)
          .attr('height', this.height);
      },
      { passive: true },
    );
  }

  private onZoomChange(transform: ZoomTransform): void {
    const zoom = 1 / transform.k;
    const x = -(transform.x / this.scale) * zoom;
    const y = -(transform.y / this.scale) * zoom;
    const svg = d3.select(`#${this.id}-rect`) as any;

    this.minimapTrigger = Date.now() + 500;
    svg.call(this.treeZoom.transform as any, this.treeInstance.zoomIdentity.translate(x, y).scale(zoom));
  }

  private moveRect(transform: ZoomTransform): void {
    this.currentZoomTransform = transform;
    const rect = d3.select(`#${this.id}-rect`).transition().duration(500) as any;
    this.treeZoom.transform(rect, transform);
  }

  private moveTree({ k, x, y }: ZoomTransform): void {
    const zoom = 1 / k;
    const newTransform = new ZoomTransform(zoom, -x * this.scale * zoom, -y * this.scale * zoom);
    this.treeInstance.changeViewPosition(newTransform);
  }

  private calculateChildCount(data: TreeStructure): number {
    return this.getHighestChildrenArea(data).reduce((a, b) => a + b, 0);
  }

  private getHighestChildrenArea(data: TreeStructure): number[] {
    let childCount: number[][] = [[1]];
    const countChildren = (index: number, children: TreeStructure[]): void => {
      if (!children) return;

      if (children.length) {
        children.forEach(child => countChildren(index + 1, child.children));
      }

      childCount[index] = childCount[index] || ([] as number[]);
      childCount[index].push(children.length);
    };

    countChildren(1, data.children);

    const sum = (a = 0, b = 0) => a + b;
    return childCount.reduce((p, c) => (p.reduce(sum) > c.reduce(sum) ? p : c));
  }

  private calculateMapHeight() {
    const circles = d3.select(`#${this.id}--Circle`).node() as HTMLElement;
    const positions = Array.from(circles.children).reduce(
      (p, c) => {
        const currentPosition = Number.parseInt(c.getAttribute('data-y'), 10);
        //TODO: Explain what happens here or improve code.
        if (currentPosition < p.lowest) {
          p.lowest = currentPosition;
        }

        if (currentPosition > p.highest) {
          p.highest = currentPosition;
        }

        return p;
      },
      { lowest: 0, highest: 0 },
    );

    positions.lowest -= this.r / 2;
    positions.highest += this.r / 2;

    const minHeight = -1 * positions.lowest + positions.highest + 50;
    this.height = minHeight > this.height ? minHeight : this.height;

    const calculatedOffset = positions.lowest - 40;
    if (this.yOffset > calculatedOffset || this.yOffset + this.height < positions.highest) {
      this.yOffset = calculatedOffset;
    }

    d3.select(`#${this.id}-svg`).attr('viewBox', this.getViewBox()).attr('height', this.height);
  }

  private getViewBox(): number[] {
    return [this.xOffset, this.yOffset, this.width, this.height];
  }
}

export default Minimap;
