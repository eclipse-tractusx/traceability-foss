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

import { Part, QualityType } from '@page/parts/model/parts.model';
import { TreeElement, TreeStructure } from '@page/parts/relations/model/relations.model';

export class RelationsAssembler {
  public static assemblePartForRelation({ id, name, serialNumber, children, qualityType }: Part): TreeElement {
    const mapQualityTypeToState = (type: QualityType) => (type === QualityType.Ok ? 'done' : type);
    const state = !!children ? mapQualityTypeToState(qualityType) || 'done' : 'loading';
    return { id, text: name, title: `${name} | ${serialNumber}`, state, children };
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
