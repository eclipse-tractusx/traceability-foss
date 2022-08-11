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

import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { RelationComponentState } from '@shared/modules/relations/core/component.state';
import { LoadedElementsFacade } from '@shared/modules/relations/core/loaded-elements.facade';
import { LoadedElementsState } from '@shared/modules/relations/core/loaded-elements.state';
import { RelationsFacade } from '@shared/modules/relations/core/relations.facade';
import { TreeElement } from '@shared/modules/relations/model/relations.model';
import { PartsService } from '@shared/service/parts.service';
import { waitFor } from '@testing-library/angular';
import { firstValueFrom, of } from 'rxjs';
import { map, skip, tap } from 'rxjs/operators';
import {
  MOCK_part_1,
  MOCK_part_2,
  MOCK_part_3,
  mockAssetList,
} from '../../../../../mocks/services/parts-mock/parts.model';

describe('Relations facade', () => {
  const childDescriptionsToChild = children => children.map(({ id }) => id);
  let relationsFacade: RelationsFacade,
    loadedElementsFacade: LoadedElementsFacade,
    componentStateMock: RelationComponentState;
  beforeEach(() => {
    const partsServiceMok = {
      getRelation: (_partId, childId) =>
        of(mockAssetList[childId]).pipe(map(part => PartsAssembler.assemblePart(part))),
      getPart: id => of(mockAssetList[id]).pipe(map(part => PartsAssembler.assemblePart(part))),
    } as PartsService;

    loadedElementsFacade = new LoadedElementsFacade(new LoadedElementsState());
    componentStateMock = new RelationComponentState();
    relationsFacade = new RelationsFacade(partsServiceMok, loadedElementsFacade, componentStateMock);
  });

  describe('openElementWithChildren', () => {
    it('should set open elements state to new one', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_2.id]: childDescriptionsToChild(MOCK_part_2.childDescriptions),
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      relationsFacade.openElementWithChildren(mockTreeElement);

      const openElements = await firstValueFrom(componentStateMock.openElements$);
      expect(openElements).toEqual(expected);
    });
  });

  describe('updateOpenElement', () => {
    it('should not update open elements if element is not already open', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected = {};

      relationsFacade.updateOpenElement(mockTreeElement);
      const openElements = await firstValueFrom(componentStateMock.openElements$);
      expect(openElements).toEqual(expected);
    });

    it('should update open elements if it is already open', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const mockUpdateTreeElement = { id: MOCK_part_2.id, children: [id] } as TreeElement;
      const expected = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_2.id]: [mockTreeElement.id],
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      relationsFacade.openElementWithChildren(mockTreeElement);
      let isImplemented = false;
      const openElements = await firstValueFrom(
        componentStateMock.openElements$.pipe(
          tap(_ => {
            if (!isImplemented) {
              isImplemented = true;
              relationsFacade.updateOpenElement(mockUpdateTreeElement);
            }
          }),
          skip(1),
        ),
      );
      expect(openElements).toEqual(expected);
    });
  });

  describe('deleteOpenElement', () => {
    it('should delete opened element', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const children = childDescriptionsToChild(childDescriptions);
      const mockTreeElement = { id, children } as TreeElement;
      const expected = { MOCK_part_1: ['MOCK_part_2', 'MOCK_part_3'], MOCK_part_3: ['MOCK_part_5'] };

      relationsFacade.openElementWithChildren(mockTreeElement);
      relationsFacade.deleteOpenElement(children[0]);

      const openElements = await firstValueFrom(componentStateMock.openElements$);
      expect(openElements).toEqual(expected);
    });

    it('should delete open element', async () => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected_all = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_2.id]: childDescriptionsToChild(MOCK_part_2.childDescriptions),
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      relationsFacade.openElementWithChildren(mockTreeElement);
      const allOpenElements = await firstValueFrom(componentStateMock.openElements$);
      await waitFor(() => expect(allOpenElements).toEqual(expected_all));

      relationsFacade.deleteOpenElement(MOCK_part_2.id);

      const expected_deleted = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      const deletedOpenElements = await firstValueFrom(componentStateMock.openElements$);
      await waitFor(() => expect(deletedOpenElements).toEqual(expected_deleted));
    });
  });

  describe('formatOpenElementsToTreeData', () => {
    it('should format data', async () => {
      const expected = {
        id: 'MOCK_part_1',
        state: 'done',
        children: [
          {
            children: [],
            id: 'MOCK_part_2',
            relations: [
              {
                children: null,
                id: 'MOCK_part_4',
                state: 'loading',
                title: 'MOCK_part_4',
              },
            ],
            state: 'Minor',
            text: 'BMW 520d Touring',
            title: 'BMW 520d Touring | 3N1CE2CPXFL392065',
          },
          {
            children: [],
            id: 'MOCK_part_3',
            relations: [
              {
                children: null,
                id: 'MOCK_part_5',
                state: 'loading',
                title: 'MOCK_part_5',
              },
            ],
            state: 'Major',
            text: 'A 180 Limousine',
            title: 'A 180 Limousine | JF1ZNAA12E8706066',
          },
        ],
        relations: [
          {
            children: null,
            id: 'MOCK_part_2',
            state: 'loading',
            title: 'MOCK_part_2',
          },
          {
            children: null,
            id: 'MOCK_part_3',
            state: 'loading',
            title: 'MOCK_part_3',
          },
        ],
      };

      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;

      loadedElementsFacade.addLoadedElement(mockTreeElement);
      relationsFacade.openElementWithChildren(mockTreeElement);
      const openElements = await firstValueFrom(componentStateMock.openElements$);
      expect(relationsFacade.formatOpenElementsToTreeData(openElements)).toEqual(expected);
    });
  });
});
