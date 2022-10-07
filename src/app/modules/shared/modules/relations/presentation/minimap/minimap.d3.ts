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
import { HelperD3 } from '@shared/modules/relations/presentation/helper.d3';
import { TreeNode, TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import Tree from '@shared/modules/relations/presentation/tree/tree.d3';
import * as d3 from 'd3';
import { HierarchyNode } from 'd3';
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

  private minimapX: number;
  private minimapY: number;

  private zoom: number;
  private isMinimapClosed = false;

  private maxChildDepth: number;

  constructor(treeData: MinimapData) {
    this.id = treeData.id;

    this.treeInstance = treeData.treeInstance;
    this.treeInstance.minimapConnector = {
      onDrag: this.refreshBorderBasedOnTree.bind(this),
      onZoom: this.onZoomChange.bind(this),
    };

    this.mainElement = treeData.mainElement;

    this.width = HelperD3.calculateWidth(this.mainElement);
    this.height = HelperD3.calculateHeight(this.mainElement);

    this.minimapX = 0;
    this.minimapY = 0;

    this.r = 60 / this.scale;
    this.zoom = 1 / this.scale;

    this.xOffset = -this.r * 1.5;
    this.initResizeListener();
  }

  public renderMinimap(data: TreeStructure): TreeSvg {
    d3.select(`#${this.id}-svg`).remove();
    const root = d3.hierarchy(data);
    const svg = this.creatMainSvg(root);
    // First draw paths so paths are behind circles.
    this.addPaths(svg, root);
    this.addCircles(svg, root);
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

  private addCircles(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    const descendants = root.descendants();
    const node = svg
      .append('g')
      .attr('id', `${this.id}--circles`)
      .selectAll('a')
      .data(descendants)
      .join('a')
      .attr('transform', ({ x, y }: TreeNode) => `translate(${y},${x})`)
      .attr('data-y', ({ x }: TreeNode) => x);

    node
      .append('circle')
      .attr('r', this.r)
      .attr('class', ({ data }: TreeNode) => `tree--element__circle-${data.state}`);
  }

  private addPaths(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    const link = d3
      .linkHorizontal<HierarchyCircularLink<TreeStructure>, HierarchyCircularNode<TreeStructure>>()
      .x(({ y }) => y)
      .y(({ x }) => x);

    svg
      .append('g')
      .attr('id', `${this.id}--paths`)
      .classed('tree--element__path', true)
      .selectAll('path')
      .data(root.links())
      .join('path')
      .attr('d', (node: HierarchyCircularLink<TreeStructure>) => link(node));
  }

  private addBorder(svg: TreeSvg): void {
    const id = this.id + '-rect';
    const { width, height } = this.getBorderSize();

    this.minimapX = this.getMinimapXFromTree();
    this.minimapY = this.getMinimapYFromTree();

    svg
      .append('rect')
      .attr('id', id)
      .attr('x', this.minimapX)
      .attr('y', this.minimapY)
      .attr('width', width)
      .attr('height', height)
      .call(HelperD3.initDrag(d3.select(`#${id}`), this.updateBorderAndTreeOnDrag.bind(this)))
      .classed('tree--minimap', true);
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
    const xDistance = offsetX + -1 * this.minimapX;
    const yDistance = offsetY + -1 * this.minimapY;

    const { width, height } = this.getBorderSize();
    const xOffset = this.xOffset - width / 2;
    const yOffset = this.yOffset - height / 2;

    const completeDistance = Math.abs(xDistance + xOffset) + Math.abs(yDistance + yOffset);
    const animationInterval = Math.max(Math.trunc(completeDistance / 5), 1);

    const x = (xDistance + xOffset) / animationInterval;
    const y = (yDistance + yOffset) / animationInterval;

    let i = 0;
    const animationFunction = () => {
      i++;
      this.updateTreeFromBorder(x, y);
      this.refreshBorderBasedOnTree();
      if (i < animationInterval) setTimeout(animationFunction, 5);
    };

    setTimeout(animationFunction, 5);
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
    const zoom = this.treeInstance.zoom / this.scale;
    const width = this.treeInstance.getCalculatedWidth() * zoom;
    const height = this.treeInstance.getCalculatedHeight() * zoom;
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

  private onZoomChange(zoom: number): void {
    zoom = zoom / this.scale;
    if (this.zoom === zoom) {
      return;
    }

    const width = this.treeInstance.getCalculatedWidth() * zoom;
    const height = this.treeInstance.getCalculatedHeight() * zoom;
    this.zoom = zoom;

    this.refreshBorderBasedOnTree();
    d3.select(`#${this.id}-rect`).attr('width', width).attr('height', height);
  }

  private updateBorderAndTreeOnDrag(x: number, y: number): void {
    this.updateTreeFromBorder(-x, -y);
    this.refreshBorderBasedOnTree();
  }

  private refreshBorderBasedOnTree(): void {
    this.minimapX = this.getMinimapXFromTree();
    this.minimapY = this.getMinimapYFromTree();

    d3.select(`#${this.id}-rect`).attr('x', this.minimapX).attr('y', this.minimapY);
  }

  private updateTreeFromBorder(x: number, y: number): void {
    const normalZoom = this.zoom * this.scale;
    x = (x / normalZoom) * this.scale;
    y = (y / normalZoom) * this.scale;

    this.treeInstance.translateCamera(x, y);
  }

  private getMinimapXFromTree(): number {
    const normalZoom = this.zoom * this.scale;
    return (this.treeInstance.viewX / this.scale) * normalZoom;
  }

  private getMinimapYFromTree(): number {
    const normalZoom = this.zoom * this.scale;
    return (this.treeInstance.viewY / this.scale) * normalZoom;
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
    const circles = d3.select(`#${this.id}--circles`).node() as HTMLElement;
    const positions = Array.from(circles.children).reduce(
      (p, c) => {
        const currentPosition = Number.parseInt(c.getAttribute('data-y'), 10);

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
    this.refreshBorderBasedOnTree();
  }

  private getViewBox(): number[] {
    return [this.xOffset, this.yOffset, this.width, this.height];
  }
}

export default Minimap;
