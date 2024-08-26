import { Location } from '@angular/common';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Contract } from '@page/admin/core/admin.model';
import { ContractsFacade } from '@page/admin/presentation/contracts/contracts.facade';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-contract-detail',
  templateUrl: './contract-detail.component.html',
  styleUrls: ['./contract-detail.component.scss']
})
export class ContractDetailComponent {
  selectedContractDetails$: Observable<View<Contract>>;
  policyJson: Object;
  defaultViewIsActivated: boolean = true;
  isFullscreenMode: boolean = false;

  constructor(private readonly contractsFacade: ContractsFacade, private readonly route: ActivatedRoute, private readonly location: Location) {
  }
  ngOnInit(): void {
    if(!this.contractsFacade.selectedContract) {
      this.route.params.subscribe(params => {
        this.contractsFacade.setSelectedContractById(params['contractId']);
      })
    }

    this.selectedContractDetails$ = this.contractsFacade.selectedContract$;
    this.selectedContractDetails$.subscribe(next => {
      if(!next?.data?.policy) {
        return;
      }
      this.policyJson = JSON.parse(next?.data?.policy)
    })

  }

  navigateBack() {
    this.location.back();
  }

  protected readonly TableType = TableType;
  protected readonly JSON = JSON;

}
