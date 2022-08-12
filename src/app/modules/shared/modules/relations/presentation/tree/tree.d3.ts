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

import { TreeData, TreeStructure } from '@shared/modules/relations/model/relations.model';
import { HelperD3 } from '@shared/modules/relations/presentation/helper.d3';
import * as d3 from 'd3';
import { HierarchyNode, PieArcDatum } from 'd3';
import { HierarchyCircularLink, HierarchyCircularNode } from 'd3-hierarchy';
import { MinimapConnector, TreeNode, TreeSvg } from '../model.d3';

export class Tree {
  private readonly id: string;
  private readonly r: number;
  private readonly openDetails: (data: TreeStructure) => void;
  private readonly updateChildren: (data: TreeStructure) => void;
  private readonly mainElement: TreeSvg;

  private _zoom: number = 1;

  private width: number;
  private height: number;

  private _viewX: number;
  private _viewY: number;

  private _minimapConnector: MinimapConnector = {
    onZoom: (_zoomChange: number) => null,
    onDrag: (_x: number, _y: number) => null,
  };

  constructor(treeData: TreeData) {
    this.id = treeData.id;

    this._zoom = treeData.zoom >= 1 ? treeData.zoom : 1;
    this.mainElement = treeData.mainElement;

    this.width = this.getCalculatedWidth();
    this.height = this.getCalculatedHeight();

    this.r = 60;
    this._viewX = -this.r * 1.5;

    this.openDetails = treeData.openDetails;
    this.updateChildren = treeData.updateChildren;

    this.initResizeListener();
  }

  public get zoom(): number {
    return this._zoom;
  }

  public set zoom(zoom: number) {
    if (this.zoom < 1.5 || zoom < 1.5) {
      const difference = Math.abs(this.zoom - zoom);
      const divergence = Math.min(difference, 0.1);

      zoom = this.zoom + (this.zoom < zoom ? divergence : -divergence);
    }

    this._zoom = zoom >= 1 ? zoom : 1;
    this.minimapConnector.onZoom(this.zoom);
  }

  public renderTree(treeData: TreeStructure): TreeSvg {
    d3.select(`#${this.id}-svg`).remove();
    const root = d3.hierarchy(treeData);

    const svg = this.creatMainSvg(root);
    this.addZoomListener(svg);
    this.addPaths(svg, root);
    this.addClosingCircle(svg, root);
    this.addCircles(svg, root);
    this.addStatusBorder(svg, root);
    this.addLoading(svg, root);
    this.addOpeningArrow(svg, root);

    return svg;
  }

  public updateViewBox(x: number, y: number): void {
    this.viewX += x * this.zoom;
    this.viewY += y;

    d3.select(`#${this.id}-svg`).attr('viewBox', this.calculateViewbox());
  }

  public getCalculatedWidth(): number {
    return HelperD3.calculateWidth(this.mainElement);
  }

  public getCalculatedHeight(): number {
    return HelperD3.calculateHeight(this.mainElement);
  }

  public get viewX(): number {
    return this._viewX;
  }

  public set viewX(viewX: number) {
    this._viewX = viewX;
  }

  public get viewY(): number {
    return this._viewY;
  }

  public set viewY(viewY: number) {
    this._viewY = viewY;
  }

  public set minimapConnector(connector: MinimapConnector) {
    this._minimapConnector = connector;
  }

  public get minimapConnector(): MinimapConnector {
    return this._minimapConnector;
  }

  private creatMainSvg(root: HierarchyNode<TreeStructure>): TreeSvg {
    d3.tree().nodeSize([this.r * 3, 250])(root);

    const dy = this.height / (root.height || 1);
    this.viewY = this.viewY || (-dy / 2) * this.zoom;

    return this.mainElement
      .append('svg')
      .attr('id', this.id + '-svg')
      .attr('viewBox', this.calculateViewbox())
      .attr('width', this.width)
      .attr('height', this.height)
      .call(HelperD3.initDrag(d3.select(`#${this.id}-svg`), this.updateViewBoxOnDrag.bind(this)))
      .attr('font-size', 10)
      .classed('tree--element', true);
  }

  private addZoomListener(svg: TreeSvg) {
    svg.on('wheel', e => {
      this.zoom = e.deltaY * 0.005 + this.zoom;
      d3.select(`#${this.id}-svg`).attr('viewBox', this.calculateViewbox());
    });
  }

  private addPaths(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    const link = d3
      .linkHorizontal<HierarchyCircularLink<TreeStructure>, HierarchyCircularNode<TreeStructure>>()
      .source(({ source }) => ({ ...source, y: source.y + this.r + 15 }))
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

  private addClosingCircle(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    const circleRadius = 15;
    const closeNode = svg
      .append('g')
      .attr('id', `${this.id}--ClosingCircle`)
      .selectAll('a')
      .data(root.descendants())
      .join('a')
      .filter(({ data }) => data.state !== 'loading' && data.children?.length > 0)
      .attr('transform', ({ x, y }: TreeNode) => `translate(${y + this.r + circleRadius + 5},${x})`)
      .on('click', (_, { data }) => this.updateChildren(data))
      .classed('tree--element__closing', true);

    closeNode
      .append('circle')
      .attr('r', circleRadius)
      .classed('tree--element__closing-animation tree--element__closing-circle', true);

    closeNode
      .append('text')
      .attr('dy', '0.26em')
      .classed('tree--element__text tree--element__closing-text tree--element__closing-animation', true)
      .text(' - ');
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
      .on('click', (_, { data }) => this.openDetails(data));

    node.append('circle').attr('r', this.r).classed('tree--element__circle', true);
    node.append('title').text(({ data }: TreeNode) => data.title);

    node
      .append('text')
      .attr('dy', '0.32em')
      .attr('textLength', '90px')
      .attr('lengthAdjust', 'spacing')
      .classed('tree--element__text', true)
      .text(({ data }) => HelperD3.shortenText(data.text || data.id));
  }

  private addStatusBorder(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    const border = svg
      .append('g')
      .attr('id', `${this.id}--statusBorder`)
      .selectAll('a')
      .data(root.descendants())
      .join('a')
      .filter(({ data }: TreeNode) => data.state !== 'loading')
      .attr('class', ({ data }: TreeNode) => `tree--element__border tree--element__border-${data.state}`)
      .attr('transform', ({ y, x }: TreeNode) => `translate(${y},${x})`);

    const addBorder = (innerRadius, outerRadius, startAngle, endAngle) => {
      const arc = d3
        .arc<HierarchyNode<TreeStructure>>()
        .innerRadius(innerRadius)
        .outerRadius(outerRadius)
        .startAngle(startAngle)
        .endAngle(endAngle);

      return border.append('path').attr('d', node => arc(node));
    };

    const data = [
      { inner: -3.2, outer: 0, start: -0.3, end: 1.6 },
      { inner: -7, outer: -3, start: 0, end: 0.4 },
      { inner: -7, outer: -3, start: 1.5, end: 1.8 },
      { inner: -7, outer: -3, start: 0.7, end: 0.9 },
      { inner: -8, outer: -5, start: 0.7, end: 1.2 },
      { inner: -12, outer: -10, start: -1.6, end: -0.7 },
      { inner: -12, outer: -10, start: -0.6, end: 0.3 },
    ];

    data.forEach(a => addBorder(this.r + a.inner, this.r + a.outer, a.start * Math.PI, a.end * Math.PI));
  }

  private addLoading(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    const arc = d3
      .arc<PieArcDatum<any>>()
      .outerRadius(this.r)
      .innerRadius(this.r - 5);

    const pie = d3.pie().padAngle(1);
    const arcs = pie(new Array(3).fill(1));
    arc.cornerRadius(5);

    const border = svg
      .append('g')
      .attr('id', `${this.id}--loading`)
      .selectAll('a')
      .data(root.descendants())
      .join('a')
      .filter(({ data }: TreeNode) => data.state === 'loading')
      .classed('tree--element__border-loading', true)
      .attr('transform', ({ y, x }: TreeNode) => `translate(${y},${x})`)
      .append('g');

    arcs.forEach((node, index) =>
      border
        .append('path')
        .attr('d', arc(node))
        .classed('tree--element__border-loading-' + index, true),
    );
  }

  private addOpeningArrow(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    const arrowSelection = svg
      .append('g')
      .attr('id', `${this.id}--arrow`)
      .selectAll('a')
      .data(root.descendants())
      .join('a')
      .filter(({ data }) => data.state !== 'loading' && data.relations?.length > 0)
      .classed('tree--element__arrow-container', true)
      .on('click', (_, { data }) => this.updateChildren(data));

    const arc = d3
      .arc<HierarchyNode<TreeStructure>>()
      .innerRadius(this.r + 5)
      .outerRadius(this.r + 15)
      .startAngle(({ data }) => (data.children?.length ? 0 : 0.3) * Math.PI)
      .endAngle(({ data }) => (data.children?.length ? 0 : 0.7) * Math.PI);

    arrowSelection
      .append('path')
      .attr('transform', ({ y, x }: TreeNode) => `translate(${y},${x})`)
      .attr('d', node => arc(node))
      .classed('tree--element__arrow', true);

    const rightArrow = [
      { x: this.r + 10 - 4, y: -this.r + 25 },
      { x: this.r + 30 - 4, y: 0 },
      { x: this.r + 10 - 4, y: this.r - 25 },
    ];

    const curveFunc = d3
      .area<{ x: number; y: number }>()
      .x(({ x }) => x)
      .y1(({ y }) => y)
      .y0(this.r / 2);

    arrowSelection
      .append('path')
      .attr('transform', ({ y, x }: TreeNode) => `translate(${y},${x})`)
      .attr('d', ({ data: { children } }: TreeNode) => curveFunc(children?.length ? [] : rightArrow))
      .classed('tree--element__arrow', true);
  }

  private updateViewBoxOnDrag(x: number, y: number): void {
    this.minimapConnector.onDrag(x, y);
    this.updateViewBox(x, y);
  }

  private initResizeListener(): void {
    window.addEventListener('resize', _ => {
      this.width = this.getCalculatedWidth();
      this.height = this.getCalculatedHeight();

      d3.select(`#${this.id}-svg`)
        .attr('viewBox', this.calculateViewbox())
        .attr('width', this.width)
        .attr('height', this.height);
    });
  }

  private calculateViewbox(): number[] {
    const heightOffset = this.viewY / this.height;

    return [this.viewX, this.height * this.zoom * heightOffset, this.width * this.zoom, this.height * this.zoom];
  }
}

export default Tree;
