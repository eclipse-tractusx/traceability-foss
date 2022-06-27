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

import { PartsAssembler } from '@page/parts/core/parts.assembler';
import { PartsService } from '@page/parts/core/parts.service';
import { RelationComponentState } from '@page/parts/relations/core/component.state';
import { LoadedElementsFacade } from '@page/parts/relations/core/loaded-elements.facade';
import { RelationsFacade } from '@page/parts/relations/core/relations.facade';
import { LoadedElementsState } from '@page/parts/relations/core/loaded-elements.state';
import { TreeElement } from '@page/parts/relations/model/relations.model';
import { of, Subscription } from 'rxjs';
import { map, skip, tap } from 'rxjs/operators';
import {
  MOCK_part_1,
  MOCK_part_2,
  MOCK_part_3,
  mockAssetList,
} from '../../../../../mocks/services/parts-mock/parts.model';

describe('Relations facade', () => {
  const childDescriptionsToChild = children => children.map(({ id }) => id);
  const subscriptions = new Subscription();
  let relationsFacade: RelationsFacade,
    loadedElementsFacade: LoadedElementsFacade,
    componentStateMock: RelationComponentState;
  beforeEach(() => {
    const partsServiceMok = {
      getRelation: (partId, childId) => of(mockAssetList[childId]).pipe(map(part => PartsAssembler.assemblePart(part))),
      getPart: id => of(mockAssetList[id]).pipe(map(part => PartsAssembler.assemblePart(part))),
    } as PartsService;

    loadedElementsFacade = new LoadedElementsFacade(new LoadedElementsState());
    componentStateMock = new RelationComponentState();
    relationsFacade = new RelationsFacade(partsServiceMok, loadedElementsFacade, componentStateMock);
  });
  afterEach(() => subscriptions.unsubscribe());

  describe('openElementWithChildren', () => {
    it('should set open elements state to new one', done => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_2.id]: childDescriptionsToChild(MOCK_part_2.childDescriptions),
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      relationsFacade.openElementWithChildren(mockTreeElement);
      const sub = componentStateMock.openElements$.subscribe(openElements => {
        expect(openElements).toEqual(expected);
        done();
      });
      subscriptions.add(sub);
    });
  });

  describe('updateOpenElement', () => {
    it('should not update open elements if element is not already open', done => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected = {};

      relationsFacade.updateOpenElement(mockTreeElement);
      const sub = componentStateMock.openElements$.subscribe(openElements => {
        expect(openElements).toEqual(expected);
        done();
      });
      subscriptions.add(sub);
    });

    it('should update open elements if it is already open', done => {
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
      const sub = componentStateMock.openElements$
        .pipe(
          tap(_ => {
            if (!isImplemented) {
              isImplemented = true;
              relationsFacade.updateOpenElement(mockUpdateTreeElement);
            }
          }),
          skip(1),
        )
        .subscribe(openElements => {
          expect(openElements).toEqual(expected);
          done();
        });
      subscriptions.add(sub);
    });
  });

  describe('deleteOpenElement', () => {
    it('should delete opened element', done => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected = {};

      relationsFacade.updateOpenElement(mockTreeElement);
      const sub = componentStateMock.openElements$.subscribe(openElements => {
        expect(openElements).toEqual(expected);
        done();
      });
      subscriptions.add(sub);
    });

    it('should delete open element', done => {
      const { id, childDescriptions } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childDescriptions) } as TreeElement;
      const expected = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childDescriptions),
        [MOCK_part_3.id]: childDescriptionsToChild(MOCK_part_3.childDescriptions),
      };

      relationsFacade.openElementWithChildren(mockTreeElement);
      let isImplemented = false;
      const sub = componentStateMock.openElements$
        .pipe(
          tap(_ => {
            if (!isImplemented) {
              isImplemented = true;
              relationsFacade.deleteOpenElement(MOCK_part_2.id);
            }
          }),
        )
        .subscribe(openElements => {
          expect(openElements).toEqual(expected);
          done();
        });
      subscriptions.add(sub);
    });
  });

  describe('formatOpenElementsToTreeData', () => {
    it('should format data', done => {
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
            state: 'done',
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
            state: 'done',
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
      const sub = componentStateMock.openElements$.subscribe(openElements => {
        expect(relationsFacade.formatOpenElementsToTreeData(openElements)).toEqual(expected);
        done();
      });

      subscriptions.add(sub);
    });
  });
});
