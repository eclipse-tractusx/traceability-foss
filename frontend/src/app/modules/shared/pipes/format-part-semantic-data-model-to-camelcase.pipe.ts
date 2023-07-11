import { Pipe, PipeTransform } from '@angular/core';
import { Part, SemanticDataModelInCamelCase } from '@page/parts/model/parts.model';

@Pipe({
  name: 'formatPartSemanticDataModelToCamelCase'
})
export class FormatPartSemanticDataModelToCamelCasePipe implements PipeTransform {

  transform(part: Part): Part {
    let camelCase;
        switch (part.semanticDataModel.toString().toLowerCase()) {
          case 'batch': {
            camelCase = SemanticDataModelInCamelCase.BATCH;
            break;
          }
          case 'serialpart': {
            camelCase = SemanticDataModelInCamelCase.SERIALPART;
            break;
          }
          case 'partasplanned': {
            camelCase = SemanticDataModelInCamelCase.PARTASPLANNED;
            break;
          }
          default: {
            camelCase = SemanticDataModelInCamelCase.UNKNOWN
            break;
          }

        }

      return {
        ...part,
        semanticDataModel: camelCase
      };

  }
}
