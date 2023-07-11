import { TitleCasePipe } from '@angular/common';
import { Pipe, PipeTransform } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';

@Pipe({
  name: 'formatSemanticDataModelToProperCase'
})
export class FormatSemanticDataModelToProperCasePipe implements PipeTransform {

  private titleCasePipe: TitleCasePipe;
  constructor(titleCasePipe: TitleCasePipe) {
    this.titleCasePipe = titleCasePipe;
  }

  transform(value: Pagination<any> | any): Pagination<any> | any {

    const transformedContent = value.content.map(part => {
      let camelCaseSemanticDataModel;
      console.log(part.semanticDataModel);
      switch (part.semanticDataModel.toLowerCase()) {

        case 'batch': {
          camelCaseSemanticDataModel = 'Batch';
          break;
        }
        case 'serialpart': {
          camelCaseSemanticDataModel = 'SerialPart';
          break;
        }
        case 'partasplanned': {
          camelCaseSemanticDataModel = 'PartAsPlanned';
          break;
        }
        default: {
          camelCaseSemanticDataModel = "Unknown"
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
