import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'flattenObject' })
export class FlattenObjectPipe implements PipeTransform {
  transform(inputObject: any): any {
    if (typeof inputObject !== 'object' || inputObject === null) {
      return inputObject;
    }

    const result: { [key: string]: any } = {};

    function recurse(current: any, property: string) {
      if (current === undefined) {
        return; // Skip properties with undefined values
      }

      if (property === 'children' || property === 'parents') {
        // Preserve 'children' and 'parents' properties as they are
        result[property] = current;
        return;
      }

      if (Object(current) !== current) {
        result[property] = current;
      } else if (Array.isArray(current)) {
        result[property] = current.map((item, index) => ({
          [`${ property }[${ index }]`]: item,
        }));
      } else {
        for (const p in current) {
          recurse(current[p], property ? property + '.' + p : p); // Remove dot for root-level properties
        }
      }
    }

    recurse(inputObject, '');
    return result;
  }
}
