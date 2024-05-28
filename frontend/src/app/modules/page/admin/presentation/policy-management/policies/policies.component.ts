import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Pagination } from '@core/model/pagination.model';
import { KnownAdminRoutes } from '@page/admin/core/admin.model';
import { PoliciesFacade } from '@page/admin/presentation/policy-management/policies/policies.facade';
import { Policy } from '@page/policies/model/policy.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import {
  CreateHeaderFromColumns,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-policies',
  templateUrl: './policies.component.html',
  styleUrls: [ './policies.component.scss' ],
})
export class PoliciesComponent {
  policiesView$: Observable<View<Pagination<Policy>>>;
  tableConfig: TableConfig;
  selectedPolicies: Policy[];
  policyFilter: any;
  pagination: TableEventConfig;
  multiSortList: TableHeaderSort[] = [];

  constructor(public readonly policyFacade: PoliciesFacade, private readonly router: Router) {
  }

  ngOnInit() {

    this.pagination = { page: 0, pageSize: 10, sorting: [ '', null ] };
    this.tableConfig = {
      displayedColumns: [ 'select', 'policyId', 'validUntil', 'menu' ],
      header: CreateHeaderFromColumns([ 'policyId', 'validUntil', 'menu' ], 'pageAdmin.policies'),
      menuActionsConfig: [],
      sortableColumns: {
        select: false,
        policyId: true,
        validUntil: false,
      },
      hasPagination: true,
    };

    this.policiesView$ = this.policyFacade.policies$;
    this.policiesView$.pipe(take(2)).subscribe(data => {
      console.log(data);
      if (data?.data?.content.length) {
        return;
      } else {
        this.policyFacade.setPolicies(0, 10, [ null, null ]);
      }
    });


  }

  filterActivated(policyFilter: any): void {
    this.policyFilter = policyFilter;
  }

  onTableConfigChange(pagination: TableEventConfig): void {
    this.pagination = pagination;
    this.policyFacade.setPolicies(pagination.page, pagination.pageSize, [ pagination.sorting ]);
  }

  multiSelection(selectedPolicies: Policy[]) {
    this.selectedPolicies = selectedPolicies;
  }

  openDetailedView(selectedPolicy: Record<string, unknown>) {
    this.policyFacade.selectedPolicy = selectedPolicy as unknown as Policy;
    this.router.navigate([ 'admin/' + KnownAdminRoutes.POLICY_MANAGEMENT + '/' + this.policyFacade.selectedPolicy.policyId ]);
  }

  openEditView(event: any) {
    return;
  }


  protected readonly TableType = TableType;
}
