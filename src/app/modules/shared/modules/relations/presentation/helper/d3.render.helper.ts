/********************************************************************************
 * Copyright (c) 2022,2023
 *        2022: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *        2022: ZF Friedrichshafen AG
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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
import { TreeNode, TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import * as d3 from 'd3';
import { HierarchyNode, PieArcDatum, Selection } from 'd3';
import { HierarchyCircularLink, HierarchyCircularNode } from 'd3-hierarchy';

type SVGSelection = Selection<SVGGElement | HTMLAnchorElement, unknown, null, undefined>;

export class D3RenderHelper {
  public static renderTreePaths(
    svg: TreeSvg,
    root: HierarchyNode<TreeStructure>,
    r: number,
    id: string,
    isMinimap?: boolean,
  ): void {
    const offset = isMinimap ? 0 : r + 15;
    const link = d3
      .linkHorizontal<HierarchyCircularLink<TreeStructure>, HierarchyCircularNode<TreeStructure>>()
      .source(({ source }) => ({ ...source, y: source.y + offset }))
      .x(({ y }) => y)
      .y(({ x }) => x);

    let paths = d3.select(`#${id}--paths`);

    if (paths.empty()) {
      paths = svg
        .append('g')
        .attr('id', `${id}--paths`)
        .attr('data-testid', 'tree--element__path')
        .classed('tree--element__path', true);
    }

    paths
      .selectAll('path')
      .data(root.links())
      .join('path')
      .attr('d', (node: HierarchyCircularLink<TreeStructure>) => link(node));
  }

  public static renderMinimapNodes(svg: TreeSvg, root: HierarchyNode<TreeStructure>, r: number, id: string): void {
    function renderElements(dataNode: TreeNode) {
      const el = d3.select(this);
      const { x, y } = dataNode;

      let circleNode = el.select(`#${id}--Circle`);

      if (circleNode.empty()) {
        circleNode = el
          .append('a')
          .attr('id', `${id}--Circle`)
          .append('circle')
          .attr('r', r)
          .attr('data-testid', ({ data }: TreeNode) => `tree--element__circle-${data.state}`)
          .attr('class', ({ data }: TreeNode) => `tree--element__circle-${data.state}`);
      }

      circleNode.attr('transform', () => `translate(${y},${x})`);
    }

    D3RenderHelper.renderNodes(svg, root, r, id, renderElements);
  }

  public static renderTreeNodes(
    svg: TreeSvg,
    root: HierarchyNode<TreeStructure>,
    r: number,
    id: string,
    updateChildren: (data: TreeStructure) => void,
    openDetails: (data: TreeStructure) => void,
  ): void {
    function renderElements(dataNode: TreeNode) {
      const el = d3.select(this);

      D3RenderHelper.renderCircle(el, dataNode, r, id, openDetails);
      D3RenderHelper.renderStatusBorder(el, dataNode, r, id);
      D3RenderHelper.renderLoading(el, dataNode, r, id);
      D3RenderHelper.renderRelationArrow(el, dataNode, r, id, updateChildren);
    }

    D3RenderHelper.renderNodes(svg, root, r, id, renderElements);
  }

  public static renderCircle(el: SVGSelection, dataNode: TreeNode, r: number, id: string, callback: (data) => void) {
    const { data, x, y } = dataNode;

    let circleNode = el.select(`#${id}--Circle`);

    if (circleNode.empty()) {
      circleNode = el
        .append('a')
        .attr('id', `${id}--Circle`)
        .on('click', () => callback(data));

      circleNode
        .append('circle')
        .attr('r', r)
        .attr('data-testid', 'tree--element__circle')
        .classed('tree--element__circle', true);
      circleNode.append('title').text(() => data.title);

      circleNode
        .append('text')
        .attr('dy', '0.32em')
        .attr('textLength', '90px')
        .attr('lengthAdjust', 'spacing')
        .attr('data-testid', 'tree--element__text')
        .classed('tree--element__text', true)
        .text(() => HelperD3.shortenText(data.text || data.id));
    }

    circleNode.attr('transform', () => `translate(${y},${x})`);
  }

  public static renderStatusBorder(el: SVGSelection, dataNode: TreeNode, r: number, id: string) {
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

  public static renderLoading(el: SVGSelection, dataNode: TreeNode, r: number, id: string) {
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
      .attr('data-testid', 'tree--element__border-loading')
      .classed('tree--element__border-loading', true)
      .attr('transform', () => `translate(${y},${x})`)
      .append('g');

    arcs.forEach((node, index) =>
      border
        .append('path')
        .attr('d', arc(node))
        .attr('data-testid', 'tree--element__border-loading-' + index)
        .classed('tree--element__border-loading-' + index, true),
    );
  }

  public static renderMinimapClosing(svg: TreeSvg, id: string, xOffset, yOffset, callback: (_: any) => void) {
    const closingButton = svg
      .append('a')
      .attr('id', id)
      .on('click', callback)
      .attr('data-testid', 'tree--minimap__closing')
      .classed('tree--minimap__closing', true);

    closingButton
      .append('text')
      .attr('id', `${id}-text`)
      .attr('x', xOffset + 20)
      .attr('y', yOffset + 25)
      .attr('width', 20)
      .attr('height', 20)
      .text(' x ')
      .attr('data-testid', 'tree--element__text')
      .classed('tree--element__text', true);
  }

  public static renderRelationArrow(
    el: SVGSelection,
    dataNode: TreeNode,
    r: number,
    id: string,
    callback: (data) => void,
  ) {
    const { data } = dataNode;

    const isLoaded = data.state !== 'loading';
    const isLoadedAndHasData = isLoaded && data.children?.length > 0;
    const isLoadedAndHasRelations = isLoaded && data.relations?.length > 0;

    el.select(`#${id}--RelationExpander`).remove();
    const createExpander = () => el.append('a').attr('id', `${id}--RelationExpander`);

    if (isLoadedAndHasData) {
      this.renderRelationCollapse(createExpander(), dataNode, r, callback);
    } else if (isLoadedAndHasRelations) {
      this.renderRelationExpand(createExpander(), dataNode, r, callback);
    }
  }

  public static renderRelationCollapse(el: SVGSelection, dataNode: TreeNode, r: number, callback: (data) => void) {
    const { data, x, y } = dataNode;

    const circleRadius = 15;
    el.attr('transform', () => `translate(${y + r + circleRadius + 5},${x})`)
      .on('click', () => callback(data))
      .attr('data-testid', 'tree--element__closing')
      .classed('tree--element__closing', true);

    el.append('circle')
      .attr('r', circleRadius)
      .attr('data-testid', 'tree--element__closing-animation tree--element__closing-circle')
      .classed('tree--element__closing-animation tree--element__closing-circle', true);

    el.append('text')
      .attr('dy', '0.26em')
      .attr('data-testid', 'tree--element__text tree--element__closing-text tree--element__closing-animation')
      .classed('tree--element__text tree--element__closing-text tree--element__closing-animation', true)
      .text(' - ');
  }

  public static renderRelationExpand(el: SVGSelection, dataNode: TreeNode, r: number, callback: (data) => void) {
    const { data, x, y } = dataNode;
    el.attr('data-testid', 'tree--element__arrow-container')
      .classed('tree--element__arrow-container', true)
      .on('click', () => callback(data));

    const arc = d3
      .arc<HierarchyNode<TreeStructure>>()
      .innerRadius(r + 5)
      .outerRadius(r + 15)
      .startAngle(({ data }) => (data.children?.length ? 0 : 0.3) * Math.PI)
      .endAngle(({ data }) => (data.children?.length ? 0 : 0.7) * Math.PI);

    el.append('path')
      .attr('transform', () => `translate(${y},${x})`)
      .attr('d', () => arc(dataNode))
      .attr('data-testid', 'tree--element__arrow')
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
      .attr('data-testid', 'tree--element__arrow')
      .classed('tree--element__arrow', true);
  }

  private static renderNodes(
    svg: TreeSvg,
    root: HierarchyNode<TreeStructure>,
    r: number,
    id: string,
    renderElements: (dataNode: TreeNode) => void,
  ): void {
    let nodes = d3.select(`#${id}--nodes`);

    if (nodes.empty()) {
      nodes = svg.append('g').attr('id', `${id}--nodes`);
    }

    nodes
      .selectAll('g.node')
      .data(root.descendants(), ({ id }) => id)
      .join(
        enter => enter.append('g').attr('data-testid', 'node').classed('node', true).each(renderElements),
        update => update.each(renderElements),
        exit => exit.remove(),
      );
  }
}
