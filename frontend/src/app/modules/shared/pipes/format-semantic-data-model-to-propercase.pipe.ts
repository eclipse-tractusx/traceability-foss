import { Pipe, PipeTransform } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { SemanticDataModelInCamelCase } from '@page/parts/model/parts.model';

@Pipe({
  name: 'formatSemanticDataModelToCamelCase'
})
export class FormatSemanticDataModelToCamelCasePipe implements PipeTransform {

  transform(value: Pagination<any> | any): Pagination<any> | any {

    const transformedContent = value.content.map(part => {
      let camelCaseSemanticDataModel;
      switch (part.semanticDataModel.toLowerCase()) {

        case 'batch': {
          camelCaseSemanticDataModel = SemanticDataModelInCamelCase.BATCH;
          break;
        }
        case 'serialpart': {
          camelCaseSemanticDataModel = SemanticDataModelInCamelCase.SERIALPART;
          break;
        }
        case 'partasplanned': {
          camelCaseSemanticDataModel = SemanticDataModelInCamelCase.PARTASPLANNED;
          break;
        }
        default: {
          camelCaseSemanticDataModel = SemanticDataModelInCamelCase.UNKNOWN
          break;
        }

      }
      return {
        ...part,
        semanticDataModel: camelCaseSemanticDataModel
      };
    });

    return {
      ...value,
      content: transformedContent
    };
  }
}
