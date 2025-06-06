/********************************************************************************
 * Copyright (c) 2023, 2025 Contributors to the Eclipse Foundation
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


import { SemanticDataModelInCamelCase } from '@page/parts/model/parts.model';
import { Pagination } from '@core/model/pagination.model';
import { FormatPaginationSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-pagination-semantic-data-model-to-camelcase.pipe';

describe('FormatPaginationSemanticDataModelToCamelCasePipe', () => {
  let pipe: FormatPaginationSemanticDataModelToCamelCasePipe;

  beforeEach(() => {
    pipe = new FormatPaginationSemanticDataModelToCamelCasePipe();
  });

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should transform all known semanticDataModel values to camel case', () => {
    const input: Pagination<any> = {
      content: [
        { semanticDataModel: 'batch' },
        { semanticDataModel: 'SerialPart' },
        { semanticDataModel: 'partAsPlanned' },
        { semanticDataModel: 'JUSTINSEQUENCE' }
      ],
      page: 0,
      pageCount: 1,
      pageSize: 10,
      totalItems: 4
    };

    const result = pipe.transform(input);

    expect(result.content[0].semanticDataModel).toBe(SemanticDataModelInCamelCase.BATCH);
    expect(result.content[1].semanticDataModel).toBe(SemanticDataModelInCamelCase.SERIALPART);
    expect(result.content[2].semanticDataModel).toBe(SemanticDataModelInCamelCase.PARTASPLANNED);
    expect(result.content[3].semanticDataModel).toBe(SemanticDataModelInCamelCase.JUSTINSEQUENCE);
  });

  it('should convert unknown semanticDataModel values to UNKNOWN', () => {
    const input: Pagination<any> = {
      content: [{ semanticDataModel: 'undefinedModel' }],
      page: 1,
      pageCount: 1,
      pageSize: 5,
      totalItems: 1
    };

    const result = pipe.transform(input);

    expect(result.content[0].semanticDataModel).toBe(SemanticDataModelInCamelCase.UNKNOWN);
  });

  it('should preserve non-semanticDataModel properties', () => {
    const input: Pagination<any> = {
      content: [{ semanticDataModel: 'batch', id: '123', name: 'TestPart' }],
      page: 0,
      pageCount: 1,
      pageSize: 1,
      totalItems: 1
    };

    const result = pipe.transform(input);

    expect(result.content[0].id).toBe('123');
    expect(result.content[0].name).toBe('TestPart');
    expect(result.content[0].semanticDataModel).toBe(SemanticDataModelInCamelCase.BATCH);
  });

  it('should return unchanged pagination metadata', () => {
    const input: Pagination<any> = {
      content: [{ semanticDataModel: 'serialpart' }],
      page: 2,
      pageCount: 5,
      pageSize: 10,
      totalItems: 50
    };

    const result = pipe.transform(input);

    expect(result.page).toBe(2);
    expect(result.pageCount).toBe(5);
    expect(result.pageSize).toBe(10);
    expect(result.totalItems).toBe(50);
  });

  it('should handle empty content arrays gracefully', () => {
    const input: Pagination<any> = {
      content: [],
      page: 0,
      pageCount: 0,
      pageSize: 10,
      totalItems: 0
    };

    const result = pipe.transform(input);
    expect(result.content).toEqual([]);
  });
});
