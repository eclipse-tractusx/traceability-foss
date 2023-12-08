import { FormControl } from '@angular/forms';
import { PartTableType } from '@shared/components/table/table.model';
import { NotificationType } from '@shared/model/notification.model';

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

  public static generateFilterColumnsMapping(sortableColumns: any, dateFields?: string[], singleSearchFields?: string[], hasFilterCol: boolean = true, hasMenuCol: boolean = true): any[] {

    const filterColumnsMapping: any[] = [];
    console.log(sortableColumns);


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

    console.log(filterColumnsMapping);
    return [ first, ...filterColumnsMapping, last ];

  }
  public static getNotificationTableType(notificationType: NotificationType, displayedColumns: any): PartTableType {

    console.log(notificationType, displayedColumns);
    if(notificationType === NotificationType.INVESTIGATION) {
      if(displayedColumns.includes('createdBy')) {
        return PartTableType.RECEIVED_INVESTIGATION;
      } else {
        console.log("RECEIVED INVS!")
        return PartTableType.CREATED_INVESTIGATION;
      }
    } else {
      if(displayedColumns.includes('createdBy')) {
        return PartTableType.RECEIVED_ALERT;
      } else {
        return PartTableType.CREATED_ALERT;
      }
    }
  }
}
