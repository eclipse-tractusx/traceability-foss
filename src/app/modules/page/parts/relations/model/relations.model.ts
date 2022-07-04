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

import { QualityType } from '@page/parts/model/parts.model';
import { Selection } from 'd3-selection';

export type LoadedElements = Record<string, TreeElement>;
export type OpenElements = Record<string, string[]>;
type State = 'done' | 'loading' | QualityType;

export interface TreeElement {
  id: string;
  title: string;
  text?: string;

  state?: State;
  children?: string[];
}

export interface TreeStructure {
  id: string;
  title: string;
  text?: string;

  state: State;
  children?: TreeStructure[];
  relations?: TreeStructure[];
}

export interface TreeData {
  id: string;
  zoom?: number;
  r?: number;
  mainElement?: Selection<Element, TreeStructure, HTMLElement, TreeStructure>;
  openDetails?: (data: TreeStructure) => void;
  updateChildren?: (data: TreeStructure) => void;
}
