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

      return {
        ...part,
        semanticDataModel: this.titleCasePipe.transform(part.semanticDataModel)
      };
    });

    return {
      ...value,
      content: transformedContent
    };
  }
}
