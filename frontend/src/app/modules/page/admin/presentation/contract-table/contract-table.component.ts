import {Component} from '@angular/core';
import {Pagination} from '@core/model/pagination.model';
import {AdminFacade} from '@page/admin/core/admin.facade';
import {Contract} from '@page/admin/core/admin.model';
import {TableType} from '@shared/components/multi-select-autocomplete/table-type.model';
import {CreateHeaderFromColumns, TableConfig, TableEventConfig} from '@shared/components/table/table.model';
import {View} from '@shared/model/view.model';
import {NotificationAction} from '@shared/modules/notification/notification-action.enum';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-contract-table',
  templateUrl: './contract-table.component.html',
  styleUrls: [],
})
export class ContractTableComponent {
  contracts: Observable<Pagination<Contract>>;
  contractsView$: Observable<View<Pagination<Contract>>>;
  tableConfig: TableConfig;
  selectedContracts: Contract[];
  contractFilter: any;
  pagination: TableEventConfig;

  constructor(public readonly adminFacade: AdminFacade) {
    this.pagination = { page: 0, pageSize: 10, sorting: [ '', null ] };
    this.tableConfig = {
      displayedColumns: [ 'select', 'contractId', 'counterpartyAddress', 'creationDate', 'endDate', 'state' ],
      header: CreateHeaderFromColumns([ 'contractId', 'counterpartyAddress', 'creationDate', 'endDate', 'state' ], 'pageAdmin.contracts'),
      menuActionsConfig: [],
      sortableColumns: {
        select: false,
        contractId: true,
        counterpartyAddress: false,
        creationDate: false,
        endDate: false,
        state: false,
      },
      hasPagination: true,
    };

  }

  public ngOnInit() {
    this.contracts = this.adminFacade.getContracts(0, 50, null, null);
    this.contractsView$ = this.contracts.pipe(map(pagination => {
      return { data: pagination };
    }));
  }

  filterActivated(contractFilter: any): void {
    this.contractFilter = contractFilter;
    this.contracts = this.adminFacade.getContracts(this.pagination.page, this.pagination.pageSize, [ this.pagination.sorting ], contractFilter);
    this.contractsView$ = this.contracts.pipe(map(pagination => {
      return { data: pagination };
    }));
  }

  public onTableConfigChange(pagination: TableEventConfig): void {
    this.pagination = pagination;
    this.contracts = this.adminFacade.getContracts(pagination.page, pagination.pageSize, [ pagination.sorting ], this.contractFilter);
    this.contractsView$ = this.contracts.pipe(map(pagination => {
      return { data: pagination };
    }));

  }

  multiSelection(selectedContracts: Contract[]) {
    this.selectedContracts = selectedContracts;
  }

  exportContractsAsCSV() {
    const csvContent = this.convertArrayOfObjectsToCSV(this.selectedContracts);
    this.downloadCSV(csvContent, 'Exported_Contracts.csv');
  }

  convertArrayOfObjectsToCSV(data: any[]) {
    const csvArray = [];
    const headers = Object.keys(data[0]);
    csvArray.push(headers.join(','));

    data.forEach(item => {
      const values = headers.map(header => item[header]);
      csvArray.push(values.join(','));
    });

    return csvArray.join('\n');
  }

  downloadCSV(csvContent: string, fileName: string) {
    const blob = new Blob([ csvContent ], { type: 'text/csv;charset=utf-8;' });

    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', fileName);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);


  }


  protected readonly NotificationAction = NotificationAction;
  protected readonly TableType = TableType;
}
