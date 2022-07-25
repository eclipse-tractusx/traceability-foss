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

import { TreeStructure } from '@shared/modules/relations/model/relations.model';
import { HierarchyCircularNode } from 'd3-hierarchy';
import { Selection } from 'd3-selection';

export type TreeSvg = Selection<SVGElement, TreeStructure, HTMLElement, TreeStructure>;
export type TreeNode = HierarchyCircularNode<TreeStructure>;

export interface MinimapConnector {
  onZoom: (zoom: number) => void;
  onDrag: (x: number, y: number) => void;
}
