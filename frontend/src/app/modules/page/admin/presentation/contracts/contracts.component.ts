import { Component, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { Pagination } from '@core/model/pagination.model';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { Contract, ContractType, KnownAdminRoutes } from '@page/admin/core/admin.model';
import { ContractsFacade } from '@page/admin/presentation/contracts/contracts.facade';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { View } from '@shared/model/view.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';
import { NotificationService } from '@shared/service/notification.service';
import { PartsService } from '@shared/service/parts.service';
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
  viewItemsClicked: EventEmitter<any> = new EventEmitter;

  constructor(public readonly adminFacade: AdminFacade, private readonly contractsFacade: ContractsFacade, private readonly router: Router, private readonly partsService: PartsService, private readonly notificationsService: NotificationService, private readonly toastService: ToastService) {
  }

  public ngOnInit() {
    this.contractsView$ = this.contractsFacade.contracts$;
    this.contractsView$.pipe(take(1)).subscribe(data => {
      if (data?.data?.content.length) {
        return;
      } else {
        this.contractsFacade.setContracts(0, 10, [ null, null ]);
      }

    });

    this.viewItemsClicked.subscribe((data) => {
      const contractId = data?.contractId;
      if (data?.contractType === ContractType.NOTIFICATION) {
        this.notificationsService.getNotifications(0, 1, [], undefined, undefined, { contractAgreementId: contractId }).subscribe({
            next: data => {
              data?.content?.length > 0 ? this.router.navigate([ 'inbox', data?.content[0]?.id ]) : this.toastService.error('pageAdmin.contracts.noItemsFoundError');
            },
            error: error => this.toastService.error(error),
          },
        );
      } else {
        this.partsService.getPartsByFilter({ contractAgreementId: contractId }, data?.contractType === ContractType.ASSET_AS_BUILT).subscribe({
          next: data => {
            data?.content?.length > 0 ? this.router.navigate([ 'parts', data?.content[0]?.id ], { queryParams: { isAsBuilt: data?.content[0]?.partId !== undefined } }) : this.toastService.error('pageAdmin.contracts.noItemsFoundError');
          },
          error: error => this.toastService.error(error),

        });
      }
    });

    this.pagination = { page: 0, pageSize: 10, sorting: [ '', null ] };


    this.tableConfig = {
      displayedColumns: [ 'select', 'contractId', 'contractType', 'counterpartyAddress', 'creationDate', 'endDate', 'state', 'menu' ],
      header: CreateHeaderFromColumns([ 'contractId', 'contractType', 'counterpartyAddress', 'creationDate', 'endDate', 'state', 'menu' ], 'pageAdmin.contracts'),
      menuActionsConfig: [
        {
          label: 'actions.viewParts',
          icon: 'build',
          action: (data: Record<string, unknown>) => {
            this.viewItemsClicked.emit(data);
          },
          condition: (data: Contract) => {
            return (data?.contractType === ContractType.ASSET_AS_BUILT || data?.contractType === ContractType.ASSET_AS_PLANNED);
          },
        },
        {
          label: 'actions.viewNotifications',
          icon: 'announcement',
          action: (data: Record<string, unknown>) => {
            this.viewItemsClicked.emit(data);
          },
          condition: (data: Contract) => {
            return (data?.contractType === ContractType.NOTIFICATION);
          },
        },
      ],
      sortableColumns: {
        select: false,
        contractType: false,
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
    this.contractsFacade.setContracts(pagination.page, pagination.pageSize, [ pagination.sorting ], this.contractFilter);
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
    this.router.navigate([ 'admin/' + KnownAdminRoutes.CONTRACT + '/' + this.contractsFacade.selectedContract.contractId ]);
  }

  filterContractType(filterList: any) {
    this.contractFilter = {
      ...this.contractFilter,
      contractType: filterList,
    };
    this.contractsFacade.setContracts(this.pagination.page, this.pagination.pageSize, [ this.pagination.sorting ], this.contractFilter);
  }

  ngOnDestroy() {
    this.contractsFacade.unsubscribeContracts();
  }


  protected readonly NotificationAction = NotificationAction;
  protected readonly TableType = TableType;


}
