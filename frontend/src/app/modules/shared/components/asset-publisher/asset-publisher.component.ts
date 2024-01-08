import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { Part } from '@page/parts/model/parts.model';
import { Policy } from '@shared/components/asset-publisher/policy.model';
import { AssetPublisherService } from '@shared/service/asset-publisher.service';

@Component({
  selector: 'app-asset-publisher',
  templateUrl: './asset-publisher.component.html',
  styleUrls: ['./asset-publisher.component.scss']
})
export class AssetPublisherComponent {

  @Input() selectedAssets: Part[] = [];

  @Output() submitted = new EventEmitter<void>();

  policies: Policy[];
  policyFormControl = new FormControl('', [Validators.required])

  constructor(private readonly assetPublisherService: AssetPublisherService) {}

  ngOnInit(): void {
    this.policies = this.assetPublisherService.getPolicies();
  }


  publish() {
    this.assetPublisherService.publishAssets(this.policyFormControl.value)
    this.policyFormControl.reset();
    this.submitted.emit();
    console.log("IMPORT")
  }
}
