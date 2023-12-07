import { FormControl } from '@angular/forms';

export class PartsTableConfigUtils {

  public static createFormGroup(displayedColumns: any): Record<string, FormControl> {
    const formGroup: Record<string, FormControl> = {};

    for (const column of displayedColumns) {
      if (column !== 'select' && column !== 'menu') {
        formGroup[column] = new FormControl([]);
      }

    }
    return formGroup;
  }

  public static createFilterColumns(displayedColumns: string[]): string[] {
    const array = displayedColumns.filter((column: string) => 'select' !== column && 'menu' !== column).map((column: string) => 'filter' + column);

    return [ 'Filter', ...array, 'Menu' ];


  }

  public static generateFilterColumnsMapping(sortableColumns: any, dateFields?: string[], singleSearchFields?: string[]): any[] {
    const filterColumnsMapping: any[] = [];
    console.log(sortableColumns);
    const firstElement = { filterKey: 'Filter', headerKey: 'Filter' };
    const lastElement = {filterKey: 'Menu', headerKey: 'Menu'};

    const excludedFields = [ 'select', 'menu' ];
    for (const key in sortableColumns) {
      if (sortableColumns.hasOwnProperty(key) && !excludedFields.includes(key)) {
        // This key goes to the backend rest api
        const filterKey = key;
        const headerKey = 'filter' + key;

        let columnMapping: { filterKey: string; headerKey: string; isDate?: boolean; singleSearch?: boolean; };
        if (dateFields?.includes(filterKey)) {
          columnMapping = { filterKey, headerKey, isDate: true };
        } else if (singleSearchFields?.includes(filterKey)) {
          columnMapping = { filterKey, headerKey, singleSearch: true };
        } else {
          columnMapping = { filterKey, headerKey };
        }

        filterColumnsMapping.push(columnMapping);
      }
    }
    console.log(filterColumnsMapping);
    return [ firstElement, ...filterColumnsMapping, lastElement ];

  }
}
