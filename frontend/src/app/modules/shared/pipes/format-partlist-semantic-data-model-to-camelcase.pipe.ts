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
import { Pipe, PipeTransform } from '@angular/core';
import { Part, SemanticDataModelInCamelCase } from '@page/parts/model/parts.model';

@Pipe({
  name: 'formatPartlistSemanticDataModelToCamelCase',
})
export class FormatPartlistSemanticDataModelToCamelCasePipe implements PipeTransform {

  transform(partList: Part[] | any[]): Part[] | any[] {

    partList.forEach(part => {
      switch (part.semanticDataModel.toString().toLowerCase()) {

        case 'batch': {
          part.semanticDataModel = SemanticDataModelInCamelCase.BATCH;
          break;
        }
        case 'serialpart': {
          part.semanticDataModel = SemanticDataModelInCamelCase.SERIALPART;
          break;
        }
        case 'partasplanned': {
          part.semanticDataModel = SemanticDataModelInCamelCase.PARTASPLANNED;
          break;
        }
        case 'justinsequence': {
          part.semanticDataModel = SemanticDataModelInCamelCase.JUSTINSEQUENCE;
          break;
        }
        case 'tombstoneasbuilt': {
          part.semanticDataModel = SemanticDataModelInCamelCase.TOMBSTONEASBUILT;
          break;
        }
        case 'tombstoneasplanned': {
          part.semanticDataModel = SemanticDataModelInCamelCase.TOMBSTONEASPLANNED;
          break;
        }
        default: {
          part.semanticDataModel = SemanticDataModelInCamelCase.UNKNOWN;
          break;
        }

      }
      return {
        ...part,
        semanticDataModel: part.semanticDataModel,
      };
    });
    return partList;

  }
}
