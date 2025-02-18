import { Injectable } from '@angular/core';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { BehaviorSubject, Observable } from 'rxjs';


export type TableFilter = Record<string, any>;

export interface FilterState {
  asBuilt: TableFilter;
  asPlanned: TableFilter;
}

@Injectable({
  providedIn: 'root'
})
export class FilterService {
  private static readonly STORAGE_KEY = 'tableFilterState';

  // BehaviorSubject holds the current filter state
  private filterStateSubject: BehaviorSubject<FilterState>;

  // Public observable to subscribe if needed
  public filterState$: Observable<FilterState>;

  constructor() {
    // 1) Load from LocalStorage or use defaults
    const loadedState = this.loadFromLocalStorage();
    const initialState: FilterState = loadedState ?? {
      asBuilt: {},
      asPlanned: {},
    };

    // 2) Create BehaviorSubject and expose as observable
    this.filterStateSubject = new BehaviorSubject<FilterState>(initialState);
    this.filterState$ = this.filterStateSubject.asObservable();
  }

  /**
   * Set (replace) the filter for a given tableType.
   * e.g. setFilter(TableType.AS_BUILT_OWN, { owner: 'OWN' })
   */
  public setFilter(tableType: TableType, filter: TableFilter): void {
    const current = this.filterStateSubject.value;

    if (tableType === TableType.AS_BUILT_OWN) {
      current.asBuilt = {
        ...current.asBuilt,
        ...filter
      };
    } else if (tableType === TableType.AS_PLANNED_OWN) {
      current.asPlanned = {
        ...current.asPlanned,
        ...filter
      };
    }

    this.filterStateSubject.next({ ...current });
    this.saveToLocalStorage(this.filterStateSubject.value);
  }

  /**
   * Retrieve the current filter for a given tableType.
   */
  public getFilter(tableType: TableType): TableFilter {
    const current = this.filterStateSubject.value;
    return (tableType === TableType.AS_BUILT_OWN)
      ? current.asBuilt
      : current.asPlanned;
  }

  /**
   * Clears the filter for the specified tableType (sets it to `{}`).
   */
  public clearFilter(tableType: TableType): void {
    const current = this.filterStateSubject.value;

    if (tableType === TableType.AS_BUILT_OWN) {
      current.asBuilt = {
      };
    } else if (tableType === TableType.AS_PLANNED_OWN) {
      current.asPlanned = {
      };
    }

    this.filterStateSubject.next({ ...current });
    this.saveToLocalStorage(this.filterStateSubject.value);
  }

  /**
   * Removes a specific key from the filter for the given tableType.
   */
  public removeFilterKey(tableType: TableType, key: string): void {
    const current = this.filterStateSubject.value;

    if (tableType === TableType.AS_BUILT_OWN) {
      delete current.asBuilt[key];
    } else if (tableType === TableType.AS_PLANNED_OWN) {
      delete current.asPlanned[key];
    }

    // Emit updated state and save
    this.filterStateSubject.next({ ...current });
    this.saveToLocalStorage(this.filterStateSubject.value);
  }

  /**
   * Returns true if there's a non-empty filter for the given tableType.
   */
  public isFilterSet(tableType: TableType, filterName: string): boolean {
    const filter = this.getFilter(tableType);
    if (!filter) return false;

    const value = filter[filterName];
    if (value == null) return false;

    if (Array.isArray(value)) {
      return value.length > 0;
    } else if (typeof value === 'string') {
      return value.trim().length > 0;
    }

    return true;
  }

  // -- Helper methods to handle LocalStorage --

  private loadFromLocalStorage(): FilterState | null {
    try {
      const raw = localStorage.getItem(FilterService.STORAGE_KEY);
      if (!raw) return null;
      return JSON.parse(raw) as FilterState;
    } catch {
      return null;
    }
  }

  private saveToLocalStorage(state: FilterState): void {
    localStorage.setItem(FilterService.STORAGE_KEY, JSON.stringify(state));
  }
}
