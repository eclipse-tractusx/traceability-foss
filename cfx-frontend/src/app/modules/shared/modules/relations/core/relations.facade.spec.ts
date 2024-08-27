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

import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { RelationComponentState } from '@shared/modules/relations/core/component.state';
import { LoadedElementsFacade } from '@shared/modules/relations/core/loaded-elements.facade';
import { LoadedElementsState } from '@shared/modules/relations/core/loaded-elements.state';
import { RelationsFacade } from '@shared/modules/relations/core/relations.facade';
import { TreeElement, TreeStructure } from '@shared/modules/relations/model/relations.model';
import { PartsService } from '@shared/service/parts.service';
import { waitFor } from '@testing-library/angular';
import { firstValueFrom, of, Subscription } from 'rxjs';
import { debounceTime, map } from 'rxjs/operators';
import {
  MOCK_part_1,
  MOCK_part_2,
  mockAssetList,
} from '../../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';

describe('Relations facade', () => {
  const childDescriptionsToChild = children => children.map(({ id }) => id);
  let sub: Subscription;
  let relationsFacade: RelationsFacade,
    loadedElementsFacade: LoadedElementsFacade,
    componentStateMock: RelationComponentState;

  beforeEach(() => {
    const partsServiceMok = {
      getPart: (id, type) => of(mockAssetList[id]).pipe(map(part => PartsAssembler.assemblePart(part, MainAspectType.AS_BUILT))),
      getPartDetailOfIds: (assetIds, type) =>
        of(assetIds.map(id => mockAssetList[id])).pipe(map(parts => PartsAssembler.assemblePartList(parts, MainAspectType.AS_BUILT))),
    } as PartsService;

    loadedElementsFacade = new LoadedElementsFacade(new LoadedElementsState());
    componentStateMock = new RelationComponentState();
    relationsFacade = new RelationsFacade(partsServiceMok, loadedElementsFacade, componentStateMock);

    relationsFacade.isParentRelationTree = false;
    sub?.unsubscribe();
    sub = relationsFacade.initRequestPartDetailQueue().subscribe();
  });

  const getOpenElements = async () => await firstValueFrom(componentStateMock.openElements$.pipe(debounceTime(700)));
  describe('openElementWithChildren', () => {
    it('should set open elements state to new one', async () => {
      const { id, childRelations } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childRelations) } as TreeElement;
      const expected = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childRelations),
        [MOCK_part_2.id]: childDescriptionsToChild(MOCK_part_2.childRelations),
      };
      relationsFacade.openElementWithChildren(mockTreeElement);
      expect(await getOpenElements()).toEqual(expected);
    });
  });

  describe('updateOpenElement', () => {
    it('should not update open elements if element is not already open', async () => {
      const { id, childRelations } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childRelations) } as TreeElement;
      const expected = {};

      relationsFacade.updateOpenElement(mockTreeElement);
      expect(await getOpenElements()).toEqual(expected);
    });
  });

  describe('deleteOpenElement', () => {
    it('should cancel opened element', async () => {
      const { id, childRelations } = MOCK_part_1;
      const children = childDescriptionsToChild(childRelations);
      const mockTreeElement = { id, children } as TreeElement;
      const expected = { MOCK_part_1: ['MOCK_part_2'] };

      relationsFacade.openElementWithChildren(mockTreeElement);
      relationsFacade.deleteOpenElement(children[0]);

      expect(await getOpenElements()).toEqual(expected);
    });

    it('should cancel open element', async () => {
      const { id, childRelations } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childRelations) } as TreeElement;
      const expected_all = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childRelations),
        [MOCK_part_2.id]: childDescriptionsToChild(MOCK_part_2.childRelations)
      };

      relationsFacade.openElementWithChildren(mockTreeElement);
      const allOpenElements = await getOpenElements();
      await waitFor(() => expect(allOpenElements).toEqual(expected_all));

      relationsFacade.deleteOpenElement(MOCK_part_2.id);

      const expected_deleted = {
        [MOCK_part_1.id]: childDescriptionsToChild(MOCK_part_1.childRelations),
      };

      const deletedOpenElements = await getOpenElements();
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
                id: "MOCK_part_4",
                title: "MOCK_part_4",
                state: "loading",
                children: null,
              }
            ],
            state: 'BATCH',
            text: 'MyAsBuiltPartName',
            title: 'MyAsBuiltPartName | MOCK_part_2',
          },
        ],
        relations: [
          {
            children: null,
            id: 'MOCK_part_2',
            state: 'loading',
            title: 'MOCK_part_2',
          },
        ],
      } as TreeStructure;

      const { id, childRelations } = MOCK_part_1;
      const mockTreeElement = { id, children: childDescriptionsToChild(childRelations) } as TreeElement;

      loadedElementsFacade.addLoadedElement(mockTreeElement);
      relationsFacade.openElementWithChildren(mockTreeElement);
      expect(relationsFacade.formatOpenElementsToTreeData(await getOpenElements())).toEqual(expected);
    });
  });
});
