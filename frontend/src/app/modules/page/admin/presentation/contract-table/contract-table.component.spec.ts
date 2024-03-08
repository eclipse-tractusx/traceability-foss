import { TestBed } from '@angular/core/testing';
import { AdminModule } from '@page/admin/admin.module';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { AdminService } from '@page/admin/core/admin.service';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
import { getContracts } from '../../../../../mocks/services/admin-mock/admin.model';

import { ContractTableComponent } from './contract-table.component';

describe('ContractTableComponent', () => {

  const mockAdminFacade = {
    getContracts: jasmine.createSpy().and.returnValue(of(getContracts))
  };

  const renderContractTableComponent = () => renderComponent(ContractTableComponent, { imports: [AdminModule], providers: [{provide: AdminFacade, useValue: mockAdminFacade}]})

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContractTableComponent],
      providers: [AdminFacade, AdminService]
    });

  });

  it('should create', async() => {
    const {fixture} = await renderContractTableComponent();
    const {componentInstance} = fixture;
    expect(componentInstance).toBeTruthy();
  });

  it('should filter', async() => {
    const {fixture} = await renderContractTableComponent();
    const {componentInstance} = fixture;

    const mockFilter = {
      contractId: ["hello"],
      counterpartyAddress: [],
      creationDate: [],
      endDate: [],
      state: []
    }
    const myPagination = {page: 0, pageSize: 10, sorting: ['', null] as TableHeaderSort}
    componentInstance.onTableConfigChange(myPagination)
    expect(componentInstance.pagination.pageSize).toEqual(10);

    componentInstance.filterActivated(mockFilter);
    expect(componentInstance.adminFacade.getContracts).toHaveBeenCalledWith(myPagination.page, myPagination.pageSize, [myPagination.sorting], mockFilter);


    expect(JSON.stringify(componentInstance.contractFilter)).toContain("hello");



  });
});
