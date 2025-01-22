import { TestBed } from '@angular/core/testing';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';

import { FilterService } from './filter.service';

describe('FilterService', () => {
  let service: FilterService;


  beforeEach(() => {
    let store = {};

    spyOn(localStorage, 'getItem').and.callFake( (key:string):string => {
      return store[key] || null;
    });
    spyOn(localStorage, 'removeItem').and.callFake((key:string):void =>  {
      delete store[key];
    });
    spyOn(localStorage, 'setItem').and.callFake((key:string, value:string):string =>  {
      return store[key] = <string>value;
    });
    spyOn(localStorage, 'clear').and.callFake(() =>  {
      store = {};
    });
    TestBed.configureTestingModule({
      providers: [FilterService],
    });
    service = TestBed.inject(FilterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should load default state if localStorage is empty', () => {
    // localStorage.getItem was already set to returnValue(null)
    // So the service should initialize with empty asBuilt, asPlanned
    const initial = service.getFilter(TableType.AS_BUILT_OWN);
    expect(initial).toEqual({});
    const initial2 = service.getFilter(TableType.AS_PLANNED_OWN);
    expect(initial2).toEqual({});
  });

  it('should load from localStorage if present', () => {

    service.setFilter(TableType.AS_BUILT_OWN, { owner: 'OWN' });
    service.setFilter(TableType.AS_PLANNED_OWN, { id: 'someId' });

    const newService = TestBed.inject(FilterService);

    expect(newService.getFilter(TableType.AS_BUILT_OWN)).toEqual({ owner: 'OWN' });
    expect(newService.getFilter(TableType.AS_PLANNED_OWN)).toEqual({ id: 'someId' });
  });

  it('should set and persist a filter for AS_PLANNED_OWN', () => {
    service.setFilter(TableType.AS_PLANNED_OWN, { id: 'myId' });

    const asPlannedFilter = service.getFilter(TableType.AS_PLANNED_OWN);
    expect(asPlannedFilter).toEqual({ id: 'myId' });

    expect(localStorage.setItem).toHaveBeenCalled();
  });

  it('should overwrite existing filter for the same table', () => {
    service.setFilter(TableType.AS_BUILT_OWN, { owner: 'OWN' });
    service.setFilter(TableType.AS_BUILT_OWN, { classification: 'myClass' });

    const asBuiltFilter = service.getFilter(TableType.AS_BUILT_OWN);
    expect(asBuiltFilter).toEqual({ classification: 'myClass' });
  });

  it('should return false if no filter is set', () => {
    expect(service.isFilterSet(TableType.AS_BUILT_OWN, 'owner')).toBeFalse();
  });

  it('should return true if filter key is a non-empty string', () => {
    service.setFilter(TableType.AS_PLANNED_OWN, { id: 'myId' });
    expect(service.isFilterSet(TableType.AS_PLANNED_OWN, 'id')).toBeTrue();
  });

  it('should return false if filter key is not available', () => {
    service.setFilter(TableType.AS_PLANNED_OWN, { id: 'test' });
    expect(service.isFilterSet(TableType.AS_PLANNED_OWN, 'owner')).toBeFalse();
  });
});
