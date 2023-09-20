/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import { TestBed } from '@angular/core/testing';
import { Pagination } from '@core/model/pagination.model';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { SemanticDataModel } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { FormatPaginationSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-pagination-semantic-data-model-to-camelcase.pipe';
import { MOCK_part_1, MOCK_part_2 } from '../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';

describe('FormatPaginationSemanticDataModelToCamelCasePipe', () => {
  let formatPaginationSemanticDataModelToCamelCasePipe: FormatPaginationSemanticDataModelToCamelCasePipe;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        FormatPaginationSemanticDataModelToCamelCasePipe
      ]
    });
    formatPaginationSemanticDataModelToCamelCasePipe = TestBed.inject(FormatPaginationSemanticDataModelToCamelCasePipe)
  });

  [
    {
      option: SemanticDataModel.BATCH,
      expected: 'Batch',
    },
    {
      option: SemanticDataModel.SERIALPART,
      expected: 'SerialPart',
    },
    {
      option: SemanticDataModel.PARTASPLANNED,
      expected: 'PartAsPlanned',
    },
  ].forEach(object => {

    it(`should transform semanticDataModel from ${object.option} to ${object.expected}`, function()  {

      let partList = [PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT), PartsAssembler.assemblePart(MOCK_part_2, MainAspectType.AS_BUILT)];

      let paginationData: Pagination<any> = {
        page: 0,
        pageCount: 1,
        pageSize: 50,
        totalItems: 1,
        content: partList
      }

      paginationData.content.forEach(part => {
        part.semanticDataModel = object.option
      })

      partList.map(part => {
        expect(part.semanticDataModel).toEqual(object.option)
      })


      let transformedPartData = formatPaginationSemanticDataModelToCamelCasePipe.transform(paginationData)

      transformedPartData.content.map(part => {
        expect(part.semanticDataModel).toEqual(object.expected);
      })

    })
  })
})
