import { FormControl } from '@angular/forms';

export class PartsTableConfigUtils {

  public static createFormGroup(displayedColumns: any): Record<string, FormControl> {
    if (!displayedColumns) {
      return;
    }
    const formGroup: Record<string, FormControl> = {};

    for (const column of displayedColumns) {
      if (column !== 'select' && column !== 'menu') {
        formGroup[column] = new FormControl([]);
      }

    }
    return formGroup;
  }

  public static createFilterColumns(displayedColumns: string[], hasFilterCol: boolean = true, hasMenuCol: boolean = true): string[] {
    if (!displayedColumns) {
      return;
    }
    const array = displayedColumns.filter((column: string) => 'select' !== column && 'menu' !== column).map((column: string) => 'filter' + column);
    let filter = null;
    let menu = null;
    if (hasFilterCol) {
      filter = 'Filter';
    }
    if (hasMenuCol) {
      menu = 'Menu';
    }
    return [ filter, ...array, menu ].filter(value => value !== null);
  }

  public static generateFilterColumnsMapping(sortableColumns: any, dateFields?: string[], singleSearchFields?: string[], hasFilterCol: boolean = true, hasMenuCol: boolean = true): any[] {

    const filterColumnsMapping: any[] = [];
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
    let first = null;
    if (hasFilterCol) {
      first = { filterKey: 'Filter', headerKey: 'Filter' };
    }
    let last = null;
    if (hasMenuCol) {
      last = { filterKey: 'Menu', headerKey: 'Menu' };
    }
    return [ first, ...filterColumnsMapping, last ].filter(value => value !== null);

  }

  public static getDefaultColumnVisibilityMap(displayedColumns: string[]): Map<string, boolean> {
    const initialColumnMap = new Map<string, boolean>();
    for (const column of displayedColumns) {
      initialColumnMap.set(column, true);
    }
    return initialColumnMap;
  }

}
