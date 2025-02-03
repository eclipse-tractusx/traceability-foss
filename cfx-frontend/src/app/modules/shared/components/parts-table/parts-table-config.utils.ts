import { FormControl } from '@angular/forms';
import { FilterConfigOptions } from '@shared/model/filter-config';
import { TranslationContext } from '@shared/model/translation-context.model';

export class PartsTableConfigUtils {
  private static excludedColumns = ['select', 'menu', 'hasAlerts'];

  private static filterConfigOptions = new FilterConfigOptions();


  public static createFormGroup(displayedColumns: any): Record<string, FormControl> {
    const formGroup: Record<string, FormControl> = {};

    for (const column of displayedColumns) {

      if (this.excludedColumns.includes(column) === false) {
        formGroup[column] = new FormControl([]);
      }

    }
    return formGroup;
  }

  public static createFilterColumns(displayedColumns: string[], hasFilterCol = true, hasMenuCol = true): string[] {
    const array = displayedColumns.filter((column: string) => this.excludedColumns.includes(column) === false).map((column: string) => 'filter' + column);
    let filter = null;
    let menu = null;
    if (hasFilterCol) {
      filter = 'Filter';
    }
    if (hasMenuCol) {
      menu = 'Menu';
    }
    return [filter, ...array, menu].filter(value => value !== null);
  }

  public static generateFilterColumnsMapping(sortableColumns: any, dateFields?: string[], singleSearchFields?: string[], hasFilterCol = true, hasMenuCol = true, isReceivedTable = true, translationContext?: TranslationContext): any[] {
    const { status, severity } = this.filterConfigOptions.filterKeyOptionsNotifications;
    const { semanticDataModel } = this.filterConfigOptions.filterKeyOptionsAssets;

    const filterColumnsMapping: any[] = [];
    for (const key in sortableColumns) {
      // eslint-disable-next-line no-prototype-builtins
      if (sortableColumns.hasOwnProperty(key) && !this.excludedColumns.includes(key)) {
        // This key goes to the backend rest api
        const filterKey = key;
        const headerKey = 'filter' + key;
        let option: any;
        if (key === 'status') {
          option = status(translationContext, isReceivedTable).option;
        } else if (key === 'severity') {
          option = severity.option;
        } else if (key === 'semanticDataModel') {
          option = semanticDataModel.option;
        }

        let columnMapping: { filterKey: string; headerKey: string; isDate?: boolean; singleSearch?: boolean, option?: any };
        if (dateFields?.includes(filterKey)) {
          columnMapping = { filterKey, headerKey, isDate: true };
        } else if (singleSearchFields?.includes(filterKey)) {
          columnMapping = { filterKey, headerKey, singleSearch: true };
        } else {
          columnMapping = { filterKey, headerKey, option };
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
    return [first, ...filterColumnsMapping, last].filter(value => value !== null);

  }
}
