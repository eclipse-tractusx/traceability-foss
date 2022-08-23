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

import { Part, QualityType } from '@page/parts/model/parts.model';
import { TreeElement, TreeStructure } from '@shared/modules/relations/model/relations.model';

export class RelationsAssembler {
  public static assemblePartForRelation({ id, name, serialNumber, children, qualityType }: Part): TreeElement {
    const mapQualityTypeToState = (type: QualityType) => (type === QualityType.Ok ? 'done' : type || 'error');
    const state = !!children ? mapQualityTypeToState(qualityType) || 'done' : 'loading';

    return { id, text: name, title: `${name || '--'} | ${serialNumber || id}`, state, children };
  }

  public static elementToTreeStructure(element: TreeElement): TreeStructure {
    if (!element) {
      return null;
    }

    const children: TreeStructure[] = element.children
      ? element.children.map(childId => ({
          id: childId,
          title: childId,
          state: 'loading',
          children: null,
        }))
      : null;

    return { ...element, state: element.state || 'done', children };
  }

  public static createLoadingElement(id: string): TreeElement {
    return { id, title: id, state: 'loading', children: null };
  }
}
