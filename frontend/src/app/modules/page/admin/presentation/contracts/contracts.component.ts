import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Pagination } from '@core/model/pagination.model';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { Contract, KnownAdminRoutes } from '@page/admin/core/admin.model';
import { ContractsFacade } from '@page/admin/presentation/contracts/contracts.facade';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-contracts',
  templateUrl: './contracts.component.html',
  styleUrls: [],
})
export class ContractsComponent {
  contractsView$: Observable<View<Pagination<Contract>>>;
  tableConfig: TableConfig;
  selectedContracts: Contract[];
  contractFilter: any;
  pagination: TableEventConfig;

  constructor(public readonly adminFacade: AdminFacade, private readonly contractsFacade: ContractsFacade, private readonly router: Router) {}

  public ngOnInit() {
    this.contractsView$ = this.contractsFacade.contracts$;
    this.contractsView$.pipe(take(1)).subscribe(data => {
      if(data?.data?.content.length) {
        return;
      } else {
        this.contractsFacade.setContracts(0,10,[null,null]);
      }
    })

    this.pagination = { page: 0, pageSize: 10, sorting: [ '', null ] };
    this.tableConfig = {
      displayedColumns: [ 'select', 'contractId', 'counterpartyAddress', 'creationDate', 'endDate', 'state', 'menu' ],
      header: CreateHeaderFromColumns([ 'contractId', 'counterpartyAddress', 'creationDate', 'endDate', 'state', 'menu' ], 'pageAdmin.contracts'),
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

  filterActivated(contractFilter: any): void {
    this.contractFilter = contractFilter;
  }

  public onTableConfigChange(pagination: TableEventConfig): void {
    this.pagination = pagination;
    this.contractsFacade.setContracts(pagination.page, pagination.pageSize,[pagination.sorting]);
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

  openDetailedView(selectedContract: Record<string, unknown>) {
    this.contractsFacade.selectedContract = selectedContract as unknown as Contract;
    this.router.navigate(['admin/'+KnownAdminRoutes.CONTRACT+'/'+this.contractsFacade.selectedContract.contractId]);
  }

  ngOnDestroy() {
    this.contractsFacade.unsubscribeContracts();
  }


  protected readonly NotificationAction = NotificationAction;
  protected readonly TableType = TableType;


}
