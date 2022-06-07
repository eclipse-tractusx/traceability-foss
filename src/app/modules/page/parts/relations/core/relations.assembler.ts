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

import { Part } from '@page/parts/model/parts.model';
import { TreeElement, TreeStructure } from '@page/parts/relations/model/relations.model';
import { View } from '@shared';
import { OperatorFunction } from 'rxjs';
import { map } from 'rxjs/operators';

export class RelationsAssembler {
  public static assemblePartForRelation({ id, name, children }: Part): TreeElement {
    const state = !!children ? 'done' : 'loading';
    return { id, name, state, children };
  }

  public static elementToTreeStructure(element: TreeElement): TreeStructure {
    if (!element) {
      return null;
    }

    const children: TreeStructure[] = element.children
      ? element.children.map(childId => ({
          id: childId,
          state: 'loading',
          children: null,
        }))
      : null;

    return { ...element, state: element.state || 'done', children };
  }

  public static createLoadingElement(id: string): TreeElement {
    return { id, state: 'loading', children: null };
  }
}
