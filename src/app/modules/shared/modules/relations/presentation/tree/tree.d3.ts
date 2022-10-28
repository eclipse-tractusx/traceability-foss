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
import type { Selection } from 'd3';
import * as d3 from 'd3';
import { HierarchyNode, PieArcDatum } from 'd3';
import { HierarchyCircularLink, HierarchyCircularNode } from 'd3-hierarchy';
import type { MinimapConnector, RenderOptions, TreeNode, TreeSvg } from '../model.d3';

export class Tree {
  public static readonly CENTERING_MARGIN = 0.1;

  private readonly id: string;
  private readonly r: number;
  private readonly openDetails: (data: TreeStructure) => void;
  private readonly updateChildren: (data: TreeStructure) => void;
  private readonly mainElement: TreeSvg;

  private _zoom: number = 1;

  private width: number;
  private height: number;

  private _viewX: number = 0;
  private _viewY: number = 0;

  private viewWidth: number;
  private viewHeight: number;

  private _minimapConnector: MinimapConnector = {
    onZoom: (_zoomChange: number) => null,
    onDrag: (_x: number, _y: number) => null,
  };

  constructor(treeData: TreeData, private readonly renderOptions: RenderOptions = { preserveRight: 0 }) {
    this.id = treeData.id;

    this.mainElement = treeData.mainElement;

    this.width = this.getCalculatedWidth();
    this.height = this.getCalculatedHeight();

    this.r = 60;

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
    const root = d3.hierarchy(treeData);
    let svg = d3.select(`#${this.id}-svg`) as TreeSvg;

    if (svg.empty()) {
      svg = this.creatMainSvg();
      this.addZoomListener(svg);
    }

    d3.tree().nodeSize([this.r * 3, 250])(root);

    this.addPaths(svg, root);
    this.addNodes(svg, root);

    if (this.viewHeight === undefined) {
      this.initCamera();
    } else {
      this.updateCamera();
    }
    return svg;
  }

  public changeSize(sizeChange: number): void {
    this.zoom = this.zoom + sizeChange;
    this.updateCamera();
  }

  public translateCamera(x: number, y: number): void {
    this.viewX += x;
    this.viewY += y;

    this.updateCamera();
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

  private creatMainSvg(): TreeSvg {
    return this.mainElement
      .append('svg')
      .attr('id', this.id + '-svg')
      .attr('width', this.width)
      .attr('height', this.height)
      .call(HelperD3.initDrag(d3.select(`#${this.id}-svg`), this.updateCameraOnDrag.bind(this)))
      .attr('font-size', 10)
      .classed('tree--element', true)
      .append('g')
      .attr('id', this.id + '-scale-camera')
      .append('g')
      .attr('id', this.id + '-translate-camera');
  }

  private addZoomListener(svg: TreeSvg) {
    svg.on('wheel', e => {
      this.zoom = e.deltaY * 0.005 + this.zoom;
      this.updateCamera();
    });
  }

  private initCamera(): void {
    const { preserveRight } = this.renderOptions;
    const circlesGroup: SVGGElement = document.querySelector(`#${this.id}--nodes`);

    const viewBoxWidth = this.width - preserveRight;
    const viewBoxHeight = this.height;
    const circlesGroupBBox = circlesGroup?.getBBox();

    const renderedElementsWidth = circlesGroupBBox.width;
    const renderedElementsHeight = circlesGroupBBox.height;
    const centerXOffset = renderedElementsWidth * Tree.CENTERING_MARGIN;
    const centerYOffset = renderedElementsHeight * Tree.CENTERING_MARGIN;

    const renderToSizeRelation = Math.max(
      (renderedElementsWidth + centerXOffset) / viewBoxWidth,
      (renderedElementsHeight + centerYOffset) / viewBoxHeight,
    );

    this._zoom = Math.max(renderToSizeRelation, 1);

    const x = circlesGroupBBox.x - (viewBoxWidth * this.zoom - renderedElementsWidth) / 2;
    const y = circlesGroupBBox.y - (viewBoxHeight * this.zoom - renderedElementsHeight) / 2;

    this.viewX = x;
    this.viewY = y;
    this.viewWidth = viewBoxWidth + preserveRight;
    this.viewHeight = viewBoxHeight;

    this.updateCamera();
  }

  private updateCamera(): void {
    d3.select(`#${this.id}-translate-camera`).attr('transform', `translate(${-this.viewX}, ${-this.viewY})`);

    d3.select(`#${this.id}-scale-camera`).attr('transform', `scale(${1 / this.zoom})`);
  }

  private addPaths(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    const link = d3
      .linkHorizontal<HierarchyCircularLink<TreeStructure>, HierarchyCircularNode<TreeStructure>>()
      .source(({ source }) => ({ ...source, y: source.y + this.r + 15 }))
      .x(({ y }) => y)
      .y(({ x }) => x);

    let paths = d3.select(`#${this.id}--paths`);

    if (paths.empty()) {
      paths = svg.append('g').attr('id', `${this.id}--paths`).classed('tree--element__path', true);
    }

    paths
      .selectAll('path')
      .data(root.links())
      .join('path')
      .attr('d', (node: HierarchyCircularLink<TreeStructure>) => link(node));
  }

  private addNodes(svg: TreeSvg, root: HierarchyNode<TreeStructure>) {
    let nodes = d3.select(`#${this.id}--nodes`);

    if (nodes.empty()) {
      nodes = svg.append('g').attr('id', `${this.id}--nodes`);
    }

    const renderRelation = this.renderRelation.bind(this);
    const renderLoading = this.renderLoading.bind(this);
    const renderStatusBorder = this.renderStatusBorder.bind(this);
    const renderCircle = this.renderCircle.bind(this);

    nodes
      .selectAll('g.node')
      .data(root.descendants(), ({ id }) => id)
      .join(
        enter =>
          enter
            .append('g')
            .classed('node', true)
            .each(function (dataNode: TreeNode) {
              const el = d3.select(this);

              renderCircle(el, dataNode);
              renderStatusBorder(el, dataNode);
              renderLoading(el, dataNode);
              renderRelation(el, dataNode);
            }),
        update => {
          update.each(function (dataNode: TreeNode) {
            const el = d3.select(this);

            renderCircle(el, dataNode);
            renderStatusBorder(el, dataNode);
            renderLoading(el, dataNode);
            renderRelation(el, dataNode);
          });

          return update;
        },
        exit => {
          exit.remove();
        },
      );
  }

  private renderCircle(el: Selection<SVGGElement, unknown, null, undefined>, dataNode: TreeNode) {
    const r = this.r;
    const id = this.id;

    const { data, x, y } = dataNode;

    let circleNode = el.select(`#${id}--Circle`);

    if (circleNode.empty()) {
      circleNode = el
        .append('a')
        .attr('id', `${id}--Circle`)
        .on('click', () => this.openDetails(data));

      circleNode.append('circle').attr('r', r).classed('tree--element__circle', true);
      circleNode.append('title').text(() => data.title);

      circleNode
        .append('text')
        .attr('dy', '0.32em')
        .attr('textLength', '90px')
        .attr('lengthAdjust', 'spacing')
        .classed('tree--element__text', true)
        .text(() => HelperD3.shortenText(data.text || data.id));
    }

    circleNode.attr('transform', () => `translate(${y},${x})`);
  }

  private renderStatusBorder(el: Selection<SVGGElement, unknown, null, undefined>, dataNode: TreeNode) {
    const r = this.r;
    const id = this.id;

    const { data, x, y } = dataNode;
    const isLoaded = data.state !== 'loading';

    let statusBorder = el.select(`#${id}--StatusBorder`);

    if (!isLoaded) {
      statusBorder.remove();
      return;
    }

    if (statusBorder.empty()) {
      statusBorder = el.append('a').attr('id', `${id}--StatusBorder`);

      const addBorder = (innerRadius: number, outerRadius: number, startAngle: number, endAngle: number) => {
        const arc = d3
          .arc<HierarchyNode<TreeStructure>>()
          .innerRadius(innerRadius)
          .outerRadius(outerRadius)
          .startAngle(startAngle)
          .endAngle(endAngle);

        return statusBorder.append('path').attr('d', () => arc(dataNode));
      };

      const borderData = [
        { inner: -3.2, outer: 0, start: -0.3, end: 1.6 },
        { inner: -7, outer: -3, start: 0, end: 0.4 },
        { inner: -7, outer: -3, start: 1.5, end: 1.8 },
        { inner: -7, outer: -3, start: 0.7, end: 0.9 },
        { inner: -8, outer: -5, start: 0.7, end: 1.2 },
        { inner: -12, outer: -10, start: -1.6, end: -0.7 },
        { inner: -12, outer: -10, start: -0.6, end: 0.3 },
      ];

      borderData.forEach(a => addBorder(r + a.inner, r + a.outer, a.start * Math.PI, a.end * Math.PI));
    }

    statusBorder
      .attr('class', () => `tree--element__border tree--element__border-${data.state}`)
      .attr('transform', () => `translate(${y},${x})`);
  }

  private renderLoading(el: Selection<SVGGElement, unknown, null, undefined>, dataNode: TreeNode) {
    const r = this.r;
    const id = this.id;

    const { data, x, y } = dataNode;

    const isLoading = data.state === 'loading';

    el.select(`${id}--Loading`).remove();

    if (!isLoading) {
      return;
    }
    const arc = d3
      .arc<PieArcDatum<any>>()
      .outerRadius(r)
      .innerRadius(r - 5);

    const pie = d3.pie().padAngle(1);
    const arcs = pie(new Array(3).fill(1));
    arc.cornerRadius(5);

    const border = el
      .append('a')
      .attr(id, `${id}--Loading`)
      .classed('tree--element__border-loading', true)
      .attr('transform', () => `translate(${y},${x})`)
      .append('g');

    arcs.forEach((node, index) =>
      border
        .append('path')
        .attr('d', arc(node))
        .classed('tree--element__border-loading-' + index, true),
    );
  }

  private renderRelation(el: Selection<SVGGElement, unknown, null, undefined>, dataNode: TreeNode) {
    const id = this.id;

    const { data, x, y } = dataNode;

    const isLoaded = data.state !== 'loading';
    const isLoadedAndHasData = isLoaded && data.children?.length > 0;
    const isLoadedAndHasRelations = isLoaded && data.relations?.length > 0;

    el.select(`#${id}--RelationExpander`).remove();
    const createExpander = () => el.append('a').attr('id', `${id}--RelationExpander`);

    if (isLoadedAndHasData) {
      this.renderRelationCollapse(createExpander(), dataNode);
    } else if (isLoadedAndHasRelations) {
      this.renderRelationExpand(createExpander(), dataNode);
    }
  }

  private renderRelationCollapse(el: Selection<HTMLAnchorElement, unknown, null, undefined>, dataNode: TreeNode) {
    const r = this.r;
    const { data, x, y } = dataNode;

    const circleRadius = 15;
    el.attr('transform', () => `translate(${y + r + circleRadius + 5},${x})`)
      .on('click', () => this.updateChildren(data))
      .classed('tree--element__closing', true);

    el.append('circle')
      .attr('r', circleRadius)
      .classed('tree--element__closing-animation tree--element__closing-circle', true);

    el.append('text')
      .attr('dy', '0.26em')
      .classed('tree--element__text tree--element__closing-text tree--element__closing-animation', true)
      .text(' - ');
  }

  private renderRelationExpand(el: Selection<HTMLAnchorElement, unknown, null, undefined>, dataNode: TreeNode) {
    const r = this.r;
    const { data, x, y } = dataNode;
    el.classed('tree--element__arrow-container', true).on('click', () => this.updateChildren(data));

    const arc = d3
      .arc<HierarchyNode<TreeStructure>>()
      .innerRadius(r + 5)
      .outerRadius(r + 15)
      .startAngle(({ data }) => (data.children?.length ? 0 : 0.3) * Math.PI)
      .endAngle(({ data }) => (data.children?.length ? 0 : 0.7) * Math.PI);

    el.append('path')
      .attr('transform', () => `translate(${y},${x})`)
      .attr('d', () => arc(dataNode))
      .classed('tree--element__arrow', true);

    const rightArrow = [
      { x: r + 10 - 4, y: -r + 25 },
      { x: r + 30 - 4, y: 0 },
      { x: r + 10 - 4, y: r - 25 },
    ];

    const curveFunc = d3
      .area<{ x: number; y: number }>()
      .x(({ x }) => x)
      .y1(({ y }) => y)
      .y0(r / 2);

    el.append('path')
      .attr('transform', () => `translate(${y},${x})`)
      .attr('d', () => curveFunc(data.children?.length ? [] : rightArrow))
      .classed('tree--element__arrow', true);
  }

  private updateCameraOnDrag(x: number, y: number): void {
    this.minimapConnector.onDrag(x, y);
    this.translateCamera(x, y);
  }

  private initResizeListener(): void {
    window.addEventListener(
      'resize',
      _ => {
        this.width = this.getCalculatedWidth();
        this.height = this.getCalculatedHeight();

        d3.select(`#${this.id}-svg`).attr('width', this.width).attr('height', this.height);
      },
      { passive: true },
    );
  }
}

export default Tree;
