import { Pipe, PipeTransform } from '@angular/core';
import { Part, SemanticDataModelInCamelCase } from '@page/parts/model/parts.model';

@Pipe({
  name: 'formatPartlistSemanticDataModelToCamelCase'
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
      return partList;

  }
}
