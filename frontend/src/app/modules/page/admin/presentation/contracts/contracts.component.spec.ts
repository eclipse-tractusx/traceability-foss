import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AdminModule } from '@page/admin/admin.module';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { assembleContract, ContractType } from '@page/admin/core/admin.model';
import { AdminService } from '@page/admin/core/admin.service';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { NotificationService } from '@shared/service/notification.service';
import { PartsService } from '@shared/service/parts.service';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
import { getContracts } from '../../../../../mocks/services/admin-mock/admin.model';

import { ContractsComponent } from './contracts.component';

describe('ContractTableComponent', () => {

  const mockAdminFacade = {
    getContracts: jasmine.createSpy().and.returnValue(of(getContracts())),
    contracts$: of({ data: { content: getContracts().content } }),
    setContracts: jasmine.createSpy(),
    unsubscribeContracts: jasmine.createSpy(),
    selectedContract: null,
  };

  const routerMock = {
    navigate: jasmine.createSpy('navigate'),
  };

  const notificationServiceMock = {
    getNotifications: jasmine.createSpy().and.returnValue(of({ content: [ { id: 'notification-id' } ] })),
  };

  const partsServiceMock = {
    getPartsByFilter: jasmine.createSpy().and.returnValue(of({
      content: [ {
        id: 'part-id',
        partId: 'part-unique-id',
      } ],
    })),
  };

  const toastServiceMock = {
    error: jasmine.createSpy('error'),
  };

  const renderContractTableComponent = () => renderComponent(ContractsComponent, {
    imports: [ AdminModule ],
    providers: [
      { provide: AdminFacade, useValue: mockAdminFacade },
      { provide: Router, useValue: routerMock },
      { provide: NotificationService, useValue: notificationServiceMock },
      { provide: PartsService, useValue: partsServiceMock },
      { provide: ToastService, useValue: toastServiceMock },
    ],
  });

  let createElementSpy: jasmine.Spy;
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ ContractsComponent ],
      providers: [ AdminFacade, AdminService, { provide: Router, useValue: routerMock } ],
    });
    createElementSpy = spyOn(document, 'createElement').and.callThrough();

  });

  it('should create', async () => {
    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;
    expect(componentInstance).toBeTruthy();
  });

  it('should filter and change table config', async () => {
    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;

    const mockFilter = {
      contractId: [ 'hello' ],
      counterpartyAddress: [],
      creationDate: [],
      endDate: [],
      state: [],
    };
    const myPagination = { page: 0, pageSize: 10, sorting: [ '', null ] as TableHeaderSort };
    componentInstance.onTableConfigChange(myPagination);
    expect(componentInstance.pagination.pageSize).toEqual(10);

    componentInstance.filterActivated(mockFilter);

    expect(JSON.stringify(componentInstance.contractFilter)).toContain('hello');

  });

  it('select a contract', async () => {
    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;
    let mockSelectedContract = assembleContract(getContracts().content[0]);
    componentInstance.multiSelection([ mockSelectedContract ]);
    expect(componentInstance.selectedContracts.length).toEqual(1);
    expect(componentInstance.selectedContracts[0].contractId).toEqual(mockSelectedContract.contractId);
  });

  it('should export contracts as csv', async () => {
    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;

    let mockSelectedContract = assembleContract(getContracts().content[0]);
    componentInstance.multiSelection([ mockSelectedContract ]);

    let convertSpy = spyOn(componentInstance, 'convertArrayOfObjectsToCSV');
    let downloadSpy = spyOn(componentInstance, 'downloadCSV');
    componentInstance.exportContractsAsCSV();
    expect(convertSpy).toHaveBeenCalledWith([ assembleContract(getContracts().content[0]) ]);
    expect(downloadSpy).toHaveBeenCalled();

  });

  it('should emit viewAssetsClicked', async () => {
    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;
    let spy = spyOn(componentInstance.viewItemsClicked, 'emit');
    const viewAssetsAction = componentInstance.tableConfig.menuActionsConfig.filter(action => action.label === 'actions.viewParts')[0];
    viewAssetsAction.action(null);
    expect(spy).toHaveBeenCalled();

  });

  it('should convert data to csv', async () => {
    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;

    let result = componentInstance.convertArrayOfObjectsToCSV([ getContracts().content[0] ]);

    expect(result).toEqual('contractId,contractType,counterpartyAddress,creationDate,endDate,state,policy\n' +
      'abc1,ASSET_AS_BUILT,https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp,2024-02-26T13:38:07+01:00,,Finalized,jsontextaspolicy');

  });
  it('should download CSV file', async () => {
    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;
    const csvContent = 'header1,header2\nvalue1,value2\nvalue3,value4'; // Sample CSV content
    const fileName = 'test.csv';


    // Mock the required browser APIs
    const link = document.createElement('a');
    spyOn(link, 'setAttribute');
    spyOn(link, 'click');
    spyOn(document.body, 'appendChild').and.callThrough();
    spyOn(document.body, 'removeChild').and.callThrough();

    createElementSpy.and.returnValue(link);

    componentInstance.downloadCSV(csvContent, fileName);

    // Check if a link was created with correct attributes
    expect(createElementSpy).toHaveBeenCalledWith('a');
    expect(link.setAttribute).toHaveBeenCalledWith('href', jasmine.any(String));
    expect(link.setAttribute).toHaveBeenCalledWith('download', fileName);
    expect(link.style.visibility).toBe('hidden');

    // Check if the link was appended to the document body
    expect(document.body.appendChild).toHaveBeenCalledWith(link);

    // Check if the link was clicked
    expect(link.click).toHaveBeenCalled();

    // Ensure that the link is removed from the document body after being clicked
    expect(document.body.removeChild).toHaveBeenCalledWith(link);
  });


  it('should show error when contractType is NOTIFICATION and no notifications are found', async () => {
    notificationServiceMock.getNotifications.and.returnValue(of({ content: [] }));

    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;
    const data = { contractId: 'contract-id', contractType: ContractType.NOTIFICATION };

    componentInstance.viewItemsClicked.emit(data);
    fixture.detectChanges();

    expect(notificationServiceMock.getNotifications).toHaveBeenCalledWith(0, 1, [], undefined, undefined, { contractAgreementId: 'contract-id' });
    expect(toastServiceMock.error).toHaveBeenCalledWith('pageAdmin.contracts.noItemsFoundError');
  });


  it('should show error when contractType is not NOTIFICATION and no parts are found', async () => {
    partsServiceMock.getPartsByFilter.and.returnValue(of({ content: [] }));

    const { fixture } = await renderContractTableComponent();
    const { componentInstance } = fixture;
    const data = { contractId: 'contract-id', contractType: ContractType.ASSET_AS_BUILT };

    componentInstance.viewItemsClicked.emit(data);
    fixture.detectChanges();

    expect(partsServiceMock.getPartsByFilter).toHaveBeenCalledWith({ contractAgreementId: 'contract-id' }, true);
    expect(toastServiceMock.error).toHaveBeenCalledWith('pageAdmin.contracts.noItemsFoundError');
  });

});
