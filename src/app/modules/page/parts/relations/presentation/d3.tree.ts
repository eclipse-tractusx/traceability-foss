/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { TreeData } from '@page/parts/relations/model/relations.model';
import * as d3 from 'd3';

const RelationTree = (data, treeData: TreeData) => {
  const { label, title, width = 640, r = 60, mainElement, onClick } = treeData;
  const padding = 1;
  const fill = '#999';
  const stroke = '#555';
  const strokeWidth = 1.5;
  const strokeOpacity = 0.4;
  const halo = '#fff';
  const haloWidth = 3;
  const root = d3.hierarchy(data);

  // Compute labels and titles.
  const descendants = root.descendants();
  const L = label == null ? null : descendants.map(d => label(d.data));

  // Compute the layout.
  const dx = r * 3;
  const dy = width / (root.height + padding);
  // @ts-ignore
  d3.tree(null).nodeSize([dx, dy])(root);

  // Center the tree.
  let x0 = Infinity;
  let x1 = -x0;
  root.each((d: any) => {
    if (d.x > x1) x1 = d.x;
    if (d.x < x0) x0 = d.x;
  });

  // Compute the default height.
  const height = x1 - x0 + dx * 2;

  const svg = mainElement
    .append('svg')
    .attr('viewBox', [(-dy * padding) / 2, x0 - dx, width, height])
    .attr('width', width)
    .attr('height', height)
    .attr('style', 'max-width: 100%; height: auto; height: intrinsic;')
    .attr('font-family', 'sans-serif')
    .attr('font-size', 10);

  svg
    .append('g')
    .attr('fill', 'none')
    .attr('stroke', stroke)
    .attr('stroke-opacity', strokeOpacity)
    .attr('stroke-width', strokeWidth)
    .selectAll('path')
    .data(root.links())
    .join('path')
    .attr(
      'd',
      d3
        .linkHorizontal()
        .x(({ y }: any) => y)
        .y(({ x }: any) => x) as any,
    );

  const node = svg
    .append('g')
    .selectAll('a')
    .data(root.descendants())
    .join('a')
    .attr('transform', ({ x, y }: any) => `translate(${y},${x})`)
    .on('click', (event, { data }) => onClick(data));

  node
    .append('circle')
    .attr('fill', d => (d.children ? stroke : fill))
    .attr('r', r);

  if (title != null) node.append('title').text(d => title(d.data));

  if (L)
    node
      .append('text')
      .attr('dy', '0.32em')
      .attr('text-anchor', 'middle')
      .attr('paint-order', 'stroke')
      .attr('stroke', halo)
      .attr('stroke-width', haloWidth)
      .text((d, i) => L[i]);

  return node;
};

export default RelationTree;
