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

  constructor(private readonly contractsFacade: ContractsFacade, private readonly route: ActivatedRoute) {
  }
  ngOnInit(): void {
    if(!this.contractsFacade.selectedContract) {
      this.route.params.subscribe(params => {
        console.log(params['contractId']);
      })

      return;
    } else {
      this.selectedContractDetails$ = this.contractsFacade.selectedContract$;
    }
  }

  protected readonly TableType = TableType;
}
