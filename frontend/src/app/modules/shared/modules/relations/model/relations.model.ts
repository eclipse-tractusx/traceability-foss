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

import { QualityType, SemanticDataModel } from '@page/parts/model/parts.model';

//ToDo: Move to model.d3.ts!!
export type LoadedElements = Record<string, TreeElement>;
export type OpenElements = Record<string, string[]>;
type State = 'done' | 'loading' | 'error' | QualityType | SemanticDataModel;

export interface TreeElement {
  id: string;
  title: string;
  text?: string;

  state?: State | string;
  children?: string[];
  parents?: string[];
}

export interface TreeStructure {
  id: string;
  title: string;
  text?: string;

  state: State | string;
  children?: TreeStructure[];
  relations?: TreeStructure[];
}

export interface TreeData {
  id: string;
  mainId: string;
  treeId?: string;
  r?: number;
  defaultZoom?: number;
  openDetails?: (data: TreeStructure) => void;
  updateChildren?: (data: TreeStructure, direction: TreeDirection) => void;
}

export enum TreeDirection {
  LEFT = 'LEFT',
  RIGHT = 'RIGHT',
}
