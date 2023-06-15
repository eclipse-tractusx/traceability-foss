import { Pipe, PipeTransform } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';

@Pipe({
  name: 'formatSemanticDataModelToProperCase'
})
export class FormatSemanticDataModelToProperCasePipe implements PipeTransform {
  transform(value: Pagination<any> | any): Pagination<any> | any {
    if (!value || !value.content || !Array.isArray(value.content)) {
      console.error('Invalid input. Expected a valid Pagination object.');
      return value;
    }

    const transformedContent = value.content.map(part => {
      if (!part.semanticDataModel || typeof part.semanticDataModel !== 'string') {
        console.error(`Invalid input for property 'semanticDataModel'. Expected a string.`);
        return part;
      }

      if (part.semanticDataModel.length === 0) {
        return part;
      }

      const firstLetter = part.semanticDataModel.charAt(0).toUpperCase();
      const restOfString = part.semanticDataModel.slice(1).toLowerCase();
      return {
        ...part,
        semanticDataModel: firstLetter + restOfString
      };
    });

    return {
      ...value,
      content: transformedContent
    };
  }
}
