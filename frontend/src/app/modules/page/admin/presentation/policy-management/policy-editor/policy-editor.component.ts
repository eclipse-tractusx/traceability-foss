import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { PoliciesFacade } from '@page/admin/presentation/policy-management/policies/policies.facade';
import { PoliciesAssembler } from '@page/admin/presentation/policy-management/policies/policy.assembler';
import {
  ConstraintLogicTypeAsSelectOptionsList,
  OperatorTypesAsSelectOptionsList,
} from '@page/admin/presentation/policy-management/policy-editor/policy-data';
import {
  ConstraintLogicType,
  getOperatorType,
  Policy,
  PolicyAction,
  PolicyConstraint,
  PolicyEntry,
} from '@page/policies/model/policy.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { ViewMode } from '@shared/model/view.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-policy-editor',
  templateUrl: './policy-editor.component.html',
  styleUrls: [ './policy-editor.component.scss' ],
})
export class PolicyEditorComponent {

  selectedPolicy: Policy;
  selectedPolicySubscription: Subscription;
  newPolicy: Policy;
  viewMode: ViewMode;

  templateFile: File | null = null;
  templateFileName: string = '';
  policyForm: FormGroup;
  minDate: Date = new Date();

  constructor(private readonly router: Router, public readonly policyFacade: PoliciesFacade, private fb: FormBuilder, private readonly toastService: ToastService) {
  }

  get constraints() {
    return this.policyForm.get('constraints') as FormArray;
  }

  ngOnInit() {
    this.viewMode = this.initializeViewMode();


    this.policyForm = this.fb.group({
      policyName: new FormControl(''),
      validUntil: new FormControl(''),
      bpns: new FormControl(''),
      accessType: new FormControl<string>(PolicyAction.ACCESS),
      constraints: this.fb.array([]),
      constraintLogicType: new FormControl(ConstraintLogicType.AND),
    });

    if (this.viewMode !== ViewMode.CREATE) {
      this.setSelectedPolicy();
      this.selectedPolicySubscription = this.policyFacade.selectedPolicy$.subscribe(next => {
        this.selectedPolicy = next?.data;
        if (next?.data) {
          this.updatePolicyForm(this.selectedPolicy);
        }


      });
    }

  }

  private initializeViewMode(): ViewMode {
    const url = this.router.url;
    if (url.includes('create')) {
      return ViewMode.CREATE;
    } else if (url.includes('edit')) {
      return ViewMode.EDIT;
    } else {
      return ViewMode.VIEW;
    }
  }

  private setSelectedPolicy(): void {
    this.policyFacade.setSelectedPolicyById(this.router.url.split('/').pop());
  }

  addConstraintFormGroup() {
    this.constraints.push(this.fb.group({
      leftOperand: new FormControl(''),
      operator: new FormControl<string>('='),
      rightOperand: new FormControl(''),
    }));
  }

  removeConstraintFormGroup(index: number) {
    this.constraints.removeAt(index);
  }

  moveConstraintUp(index: number): void {
    if (index <= 0) {
      return;
    }
    const constraints = this.policyForm.get('constraints') as FormArray;
    const constraint = constraints.at(index);
    constraints.removeAt(index);
    constraints.insert(index - 1, constraint);
  }

  moveConstraintDown(index: number): void {
    const constraints = this.policyForm.get('constraints') as FormArray;
    if (index >= constraints.length - 1) {
      return;
    }
    const constraint = constraints.at(index);
    constraints.removeAt(index);
    constraints.insert(index + 1, constraint);
  }


  navigateBack() {
    this.router.navigate([ 'admin/policies' ]);
  }

  savePolicy() {
    const policyEntry = this.mapPolicyFormToPolicyEntry();
    this.policyFacade.createPolicy(policyEntry).subscribe({
      next: next => {
        this.toastService.success('pageAdmin.policyManagement.successMessage');
      },
      error: error => {
        this.toastService.error('pageAdmin.policyManagement.successMessage');
      },
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.templateFile = input.files[0];
      this.templateFileName = this.templateFile.name;
    }
  }

  navigateToEditView() {
    this.router.navigate([ 'admin/policies/', 'edit', this.selectedPolicy.policyId ]);
  }

  downloadTemplateAsJsonFile() {
    if (this.templateFile) {
      const url = URL.createObjectURL(this.templateFile);
      const a = document.createElement('a');
      a.href = url;
      a.download = this.templateFile.name;
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    }
  }

  applyTemplate() {
    if (!this.templateFile) {
      return;
    }
    const reader = new FileReader();

    reader.onload = () => {
      const fileContent = reader.result;
      if (typeof fileContent === 'string') {
        let policyEntry = PoliciesAssembler.mapToPolicyEntryList(JSON.parse(fileContent));
        let policy = PoliciesAssembler.assemblePolicy(policyEntry[0].payload.policy);
        this.updatePolicyForm(policy);
      }
    };

    reader.onerror = () => {
      this.toastService.error(reader.error?.message);
    };

    reader.readAsText(this.templateFile);

  }

  updatePolicyForm(policy: Policy) {
    this.policyForm.patchValue({
      policyName: policy?.policyName,
      validUntil: policy?.validUntil,
      bpns: policy?.bpn,
      accessType: policy?.accessType,
    });


    let permissionList = policy.permissions[0].constraint.and.length ? policy.permissions[0].constraint.and : policy.permissions[0].constraint.or;
    let constraintsList = permissionList.map((constraint) => this.fb.group({
      leftOperand: this.fb.control<string>(constraint.leftOperand),
      operator: this.fb.control<string>('='),
      rightOperand: this.fb.control<string>(constraint['odrl:rightOperand']),
    }));

    this.policyForm.setControl('constraints', this.fb.array(constraintsList));
    if (this.viewMode === ViewMode.VIEW) {
      this.policyForm.disable();
    }
  }

  mapPolicyFormToPolicyEntry(): PolicyEntry {
    let policyEntry: PolicyEntry;
    let policyConstraints: PolicyConstraint[];

    policyConstraints = this.policyForm.get('constraints').getRawValue().map((constraint) => {
      return {
        'odrl:leftOperand': constraint.leftOperand,
        'odrl:operator': {
          '@id': 'odrl:' + getOperatorType(constraint.operator).toLowerCase(),
        },
        'odrl:rightOperand': constraint.rightOperand,
      };
    });

    policyEntry = {
      validUntil: this.policyForm.get('validUntil').getRawValue() + '.000000000Z',
      businessPartnerNumber: this.policyForm.get('bpns').getRawValue(),
      payload: {
        '@context': {
          odrl: 'http://www.w3.org/ns/odrl/2/',
        },
        '@id': this.policyForm.get('policyName').getRawValue(),
        policy: {
          policyId: this.policyForm.get('policyName').getRawValue(),
          createdOn: new Date(Date.now()).toISOString().replace('Z', '000000Z'),
          validUntil: this.policyForm.get('validUntil').getRawValue() + '.000000000Z',
          permissions: [
            {
              action: this.policyForm.get('accessType').getRawValue().toLowerCase(),
              constraint: {
                and: this.policyForm.get('constraintLogicType').getRawValue() === ConstraintLogicType.AND ? policyConstraints : null,
                or: this.policyForm.get('constraintLogicType').getRawValue() === ConstraintLogicType.OR ? policyConstraints : null,
              },
            },
          ],
        },
      },
    };

    if (policyEntry.payload.policy.permissions[0].constraint?.and.length) {
      delete policyEntry.payload.policy.permissions[0].constraint?.or;
    } else {
      delete policyEntry.payload.policy.permissions[0].constraint?.and;
    }

    return policyEntry;
  }

  protected readonly ViewMode = ViewMode;
  protected readonly OperatorTypesAsSelectOptionsList = OperatorTypesAsSelectOptionsList;
  protected readonly ConstraintLogicTypeAsSelectOptionsList = ConstraintLogicTypeAsSelectOptionsList;
}
