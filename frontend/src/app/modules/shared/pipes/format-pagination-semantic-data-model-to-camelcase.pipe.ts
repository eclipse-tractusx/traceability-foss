import { Pipe, PipeTransform } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { SemanticDataModelInCamelCase } from '@page/parts/model/parts.model';

@Pipe({
  name: 'formatPaginationSemanticDataModelToCamelCase'
})
export class FormatPaginationSemanticDataModelToCamelCasePipe implements PipeTransform {

  transform(value: Pagination<any> | any): Pagination<any> | any {

      const transformedContent = value.content.map(part => {
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
          default: {
            part.semanticDataModel = SemanticDataModelInCamelCase.UNKNOWN
            break;
          }

        }
        return {
          ...part,
          semanticDataModel: part.semanticDataModel
        };
      });
      return {
        ...value,
        content: transformedContent
      };

  }
}
