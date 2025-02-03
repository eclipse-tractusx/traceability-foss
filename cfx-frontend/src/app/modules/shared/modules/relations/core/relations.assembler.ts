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

import { Part, SemanticDataModel } from '@page/parts/model/parts.model';
import { TreeElement, TreeStructure } from '@shared/modules/relations/model/relations.model';
import _deepClone from 'lodash-es/cloneDeep';

export class RelationsAssembler {
  public static assemblePartForRelation(part: Part, idFallback?: string): TreeElement {
    const { id, nameAtManufacturer: text = idFallback, semanticDataModel, children, parents } = part || {};

    const mapBatchToState = (type: SemanticDataModel): SemanticDataModel | string => {
      if (type && type.toUpperCase() in SemanticDataModel) {
        return type;
      }
      return 'done';
    };
    const loadingOrErrorStatus = id ? 'loading' : 'error';

    const mappedOrFallbackStatus = mapBatchToState(semanticDataModel);
    const state = children ? mappedOrFallbackStatus : loadingOrErrorStatus;

    const title = `${text || '--'} | ${/*semanticModelId ||*/ id}`;

    return { id: id || idFallback, text, title, state, children, parents };
  }

  public static elementToTreeStructure(element: TreeElement, isParentDirection = false): TreeStructure {
    if (!element) return null;
    const clonedElement = _deepClone(element);
    const nodes = isParentDirection ? clonedElement.parents : clonedElement.children;
    delete clonedElement.parents;

    const children: TreeStructure[] = nodes
      ? nodes.map(childId => ({
        id: childId,
        title: childId,
        state: 'loading',
        children: null,
      }))
      : null;

    return { ...clonedElement, state: clonedElement.state || 'done', children };
  }

  public static createLoadingElement(id: string): TreeElement {
    return { id, title: id, state: 'loading', children: null };
  }
}
