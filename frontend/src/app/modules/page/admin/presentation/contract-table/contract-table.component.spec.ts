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

  it('should filter and change table config', async() => {
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

  it('select a contract', async() => {
    const {fixture} = await renderContractTableComponent();
    const {componentInstance} = fixture;
    let mockSelectedContract = getContracts().content[0];
    componentInstance.multiSelection([mockSelectedContract]);
    expect(componentInstance.selectedContracts.length).toEqual(1);
    expect(componentInstance.selectedContracts[0].contractId).toEqual(mockSelectedContract.contractId)
  });

  it('should export contracts as csv', async() => {
    const {fixture} = await renderContractTableComponent();
    const {componentInstance} = fixture;

    let mockSelectedContract = getContracts().content[0];
    componentInstance.multiSelection([mockSelectedContract]);

    let convertSpy = spyOn(componentInstance, 'convertArrayOfObjectsToCSV');
    let downloadSpy = spyOn(componentInstance,'downloadCSV')
    componentInstance.exportContractsAsCSV();
    expect(convertSpy).toHaveBeenCalledWith([getContracts().content[0]]);
    expect(downloadSpy).toHaveBeenCalled();

  });

  it('should convert data to csv', async() => {
    const {fixture} = await renderContractTableComponent();
    const {componentInstance} = fixture;

    let result = componentInstance.convertArrayOfObjectsToCSV([getContracts().content[0]])

    expect(result).toEqual("contractId,counterpartyAddress,creationDate,endDate,state\n" +
      "abc1,https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp,1708951.087,,Finalized");

  });
/*
  it('should download csv', async() => {
    const {fixture} = await renderContractTableComponent();
    const {componentInstance} = fixture;
    const csvContent = 'ID,Name\n1,abc\n2,def\n';
    const fileName = 'export.csv';

    // Create a spy object with a click() method
    const spyObj = jasmine.createSpyObj('a', ['click', 'setAttribute'], ['style'] );
    //TODOspyObj.style = {visibility: ''};

    // Spy on document.createElement() and return the spy object
    spyOn(document, 'createElement').and.returnValue(spyObj);

    // Spy on URL.createObjectURL() to avoid actual Blob creation
    spyOn(URL, 'createObjectURL').and.returnValue('blob:url');

    // Call the downloadCSV method
    componentInstance.downloadCSV(csvContent, fileName);

    expect(document.createElement).toHaveBeenCalledTimes(1);
    expect(document.createElement).toHaveBeenCalledWith('a');

    expect(spyObj.href).toBe('blob:url');
    expect(spyObj.download).toBe(fileName);
    expect(spyObj.click).toHaveBeenCalledTimes(1);

  });
*/
});
