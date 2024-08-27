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

import { SemanticDataModel } from '@page/parts/model/parts.model';
import { TreeDirection, TreeStructure } from '@shared/modules/relations/model/relations.model';
import { HelperD3 } from '@shared/modules/relations/presentation/helper/helper.d3';
import { TreeNode, TreeSvg } from '@shared/modules/relations/presentation/model.d3';
import * as d3 from 'd3';
import { HierarchyNode, PieArcDatum, Selection } from 'd3';
import { HierarchyCircularLink, HierarchyCircularNode } from 'd3-hierarchy';

type SVGSelection = Selection<SVGGElement | HTMLAnchorElement, unknown, null, undefined>;

export class D3RenderHelper {
  public static renderTreePaths(
    direction: TreeDirection,
    svg: TreeSvg,
    root: HierarchyNode<TreeStructure>,
    r: number,
    id: string,
    isMinimap?: boolean,
  ): void {
    const offset = isMinimap ? 0 : r + 15;
    const link = d3
      .linkVertical<HierarchyCircularLink<TreeStructure>, HierarchyCircularNode<TreeStructure>>()
      .source(({ source }) => ({ ...source, y: source.y + offset * 2 + 15 } as HierarchyCircularNode<TreeStructure>))
      .x(({ x }) => x)
      .y(({ y }) => D3RenderHelper.modifyByDirection(direction, y));

    let paths = d3.select(`#${id}--paths`);

    if (paths.empty()) {
      paths = svg
        .append('g')
        .attr('id', `${id}--paths`)
        .attr('data-testid', `${id}--paths`)
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

      circleNode.attr('transform', () => `translate(${x + 75},${y})`);
    }

    D3RenderHelper.renderNodes(svg, root, r, id, renderElements);
  }

  public static renderTreeNodes(
    direction: TreeDirection,
    svg: TreeSvg,
    root: HierarchyNode<TreeStructure>,
    r: number,
    id: string,
    updateChildren: (data: TreeStructure, direction: TreeDirection) => void,
    openDetails: (data: TreeStructure) => void,
  ): void {
    function renderElements(dataNode: TreeNode) {
      const el = d3.select(this);
      const { depth } = dataNode;

      if (depth !== 0) {
        r = 40;
      } else {
        r = 60;
      }

      D3RenderHelper.renderCircle(direction, el, dataNode, r, id, openDetails);
      D3RenderHelper.renderLoading(direction, el, dataNode, r, id);
      D3RenderHelper.renderRelationArrow(direction, el, dataNode, r, id, updateChildren);
    }

    D3RenderHelper.renderNodes(svg, root, r, id, renderElements);
  }

  public static renderCircle(
    direction: TreeDirection,
    el: SVGSelection,
    dataNode: TreeNode,
    r: number,
    id: string,
    callback: (data) => void,
  ) {
    const { data, x, y } = dataNode;
    const text = HelperD3.shortenText(data.text || data.id, 11);
    const textLength = text.length * 6.81;

    let circleNode = el.select(`#${id}--Circle`);

    if (!circleNode.empty()) {
      circleNode.remove();
    }

    circleNode = el
      .append('a')
      .attr('id', `${id}--Circle`)
      .on('click', () => callback(data));

    const statusBorderColor = data.state === SemanticDataModel.BATCH ? 'Batch' : data.state.toString();

    // This is to make the border of the circle
    circleNode
      .append('circle')
      .attr('r', r + 2)
      .attr('data-testid', 'tree--element__circle')
      .attr('class', () => `tree--element__border-${statusBorderColor}`);
    circleNode.append('title').text(() => data.title);

    circleNode
      .append('circle')
      .attr('r', r)
      .attr('data-testid', 'tree--element__circle')
      .classed('tree--element__circle', true);
    circleNode.append('title').text(() => data.title);

    circleNode
      .append('text')
      .attr('dy', '0.25em')
      .attr('textLength', `${textLength}px`)
      .attr('lengthAdjust', 'spacing')
      .attr('data-testid', 'tree--element__text')
      .classed('tree--element__text', true)
      .text(() => text);

    circleNode.attr('transform', () => `translate(${D3RenderHelper.modifyByDirection(direction, x)},${D3RenderHelper.modifyByDirection(direction, y)})`);
  }

  public static renderLoading(direction: TreeDirection, el: SVGSelection, dataNode: TreeNode, r: number, id: string) {
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
      .attr('transform', () => `translate(${D3RenderHelper.modifyByDirection(direction, x)},${y})`)
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
      .attr('data-testid', id)
      .on('click', callback)
      .classed('tree--minimap__closing', true);

    closingButton
      .append('text')
      .attr('id', `${id}-text`)
      .attr('x', xOffset + 20)
      .attr('y', yOffset + 25)
      .attr('width', 20)
      .attr('height', 20)
      .text(' x ')
      .attr('data-testid', `${id}-text`)
      .classed('tree--element__text', true);
  }

  public static renderRelationArrow(
    direction: TreeDirection,
    el: SVGSelection,
    dataNode: TreeNode,
    r: number,
    id: string,
    callback: (data, direction) => void,
  ) {
    const { data } = dataNode;

    const isLoaded = data.state !== 'loading';
    const isLoadedAndHasData = isLoaded && data.children?.length > 0;
    const isLoadedAndHasRelations = isLoaded && data.relations?.length > 0;

    el.select(`#${id}--RelationExpander`).remove();
    const createExpander = () => el.append('a').attr('id', `${id}--RelationExpander`);

    if (isLoadedAndHasData) {
      this.renderRelationCollapse(direction, createExpander(), dataNode, r, callback);
    } else if (isLoadedAndHasRelations) {
      this.renderRelationExpand(direction, createExpander(), dataNode, r, callback);
    }
  }

  public static renderRelationCollapse(
    direction: TreeDirection,
    el: SVGSelection,
    dataNode: TreeNode,
    r: number,
    callback: (data, direction) => void,
  ) {
    const { data, x, y } = dataNode;

    const circleRadius = 14;
    el.attr(
      'transform',
      () => `translate(${x},${D3RenderHelper.modifyByDirection(direction, y + r + circleRadius + 2)})`,
    )
      .on('click', () => callback(data, direction))
      .attr('data-testid', 'tree--element__closing')
      .classed('tree--element__closing', true);

    const imageId = crypto.randomUUID(); // Unique ID for the associated image

    el.append('circle')
      .attr('data-associated-image-id', imageId)
      .attr('r', circleRadius)
      .attr('data-testid', 'tree--element__closing-animation tree--element__closing-circle')
      .classed('tree--element__closing-animation tree--element__closing-circle', true)
      .on('mouseover', function () {
        // Change the image source when hovering over the associated circle
        const associatedImageId = d3.select(this).attr('data-associated-image-id');
        d3.select(`svg image[data-associated-image-id="${associatedImageId}"]`)
          .attr('xlink:href', '/assets/images/icons/collapse_relation_hover_icon.svg');
      })
      .on('mouseout', function () {
        // Revert the image source when mouse leaves the associated circle
        const associatedImageId = d3.select(this).attr('data-associated-image-id');
        d3.select(`svg image[data-associated-image-id="${associatedImageId}"]`)
          .attr('xlink:href', '/assets/images/icons/collapse_relation_icon.svg');
      });

    el.append('svg:image')
      .attr('id', imageId)
      .attr('x', -7)
      .attr('y', -1)
      .attr('width', 14)
      .attr('height', 2)
      .on('mouseover', function () {
        // Change the image source when hovering over
        d3.select(this).attr('xlink:href', '/assets/images/icons/collapse_relation_hover_icon.svg');
      })
      .on('mouseout', function () {
        // Revert the image source when mouse leaves the associated circle
        d3.select(this).attr('xlink:href', '/assets/images/icons/collapse_relation_icon.svg');
      })
      .attr('xlink:href', '/assets/images/icons/collapse_relation_icon.svg')
      .attr('data-associated-image-id', imageId);
  }

  public static renderRelationExpand(
    direction: TreeDirection,
    el: SVGSelection,
    dataNode: TreeNode,
    r: number,
    callback: (data, direction) => void,
  ) {
    const { data, x, y } = dataNode;
    el.attr('data-testid', 'tree--element__arrow-container')
      .classed('tree--element__arrow-container', true)
      .on('click', () => callback(data, direction));

    const startAngleFactor = direction === TreeDirection.UP ? 1.8 : 0.8;
    const endAngleFactor = direction === TreeDirection.UP ? 2.2 : 1.2;

    const arc = d3
      .arc<HierarchyNode<TreeStructure>>()
      .innerRadius(r + 0.083 * r)
      .outerRadius(r + 0.25 * r)
      .startAngle(({ data }) => (data.children?.length ? 0 : startAngleFactor) * Math.PI)
      .endAngle(({ data }) => (data.children?.length ? 0 : endAngleFactor) * Math.PI);

    el.append('path')
      .attr('transform', () => `translate(${x},${D3RenderHelper.modifyByDirection(direction, y)})`)
      .attr('d', () => arc(dataNode))
      .attr('data-testid', 'tree--element__arrow')
      .classed('tree--element__arrow', true);

    // These values are based on the radius of the circle
    const downArrow = [
      { y: r + 0.1 * r, x: -r + 0.416 * r },
      { y: r + 0.433 * r, x: 0 },
      { y: r + 0.1 * r, x: r - 0.416 * r },
    ];

    const upArrow = [
      { y: -r - 0.1 * r, x: -r + 0.416 * r },
      { y: -r - 0.433 * r, x: 0 },
      { y: -r - 0.1 * r, x: r - 0.416 * r },
    ];

    const arrow = direction === TreeDirection.DOWN ? downArrow : upArrow;

    const curveFunc = d3
      .area<{ x: number; y: number }>()
      .y(({ y }) => y)
      .x1(({ x }) => x)
      .x0(r / 2);

    el.append('path')
      .attr('transform', () => `translate(${x},${D3RenderHelper.modifyByDirection(direction, y)})`)
      .attr('d', () => curveFunc(data.children?.length ? [] : arrow))
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
      nodes = svg.append('g').attr('id', `${id}--nodes`).attr('data-testid', `${id}--nodes`);
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

  private static modifyByDirection(direction: TreeDirection, x: number): number {
    if (direction === TreeDirection.UP) {
      return -1 * x;
    }

    return x;
  }
}
