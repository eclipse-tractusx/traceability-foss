import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { bpnListRegex, bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { PoliciesFacade } from '@page/admin/presentation/policy-management/policies/policies.facade';
import {
  ConstraintLogicTypeAsSelectOptionsList,
  OperatorTypesAsSelectOptionsList,
} from '@page/admin/presentation/policy-management/policy-editor/policy-data';
import {
  ConstraintLogicType,
  getOperatorType,
  getOperatorTypeSign,
  OperatorType,
  Policy,
  PolicyAction,
  PolicyConstraint,
  PolicyEntry,
} from '@page/policies/model/policy.model';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
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
  viewMode: ViewMode;

  templateFile: File | null = null;
  templateFileName: string = '';
  policyForm: FormGroup;
  minDate: Date = new Date();
  templateError: string = '';

  constructor(private router: Router, private route: ActivatedRoute, public policyFacade: PoliciesFacade, public fb: FormBuilder, private toastService: ToastService) {
  }

  get constraints() {
    return this.policyForm.get('constraints') as FormArray;
  }

  ngOnInit() {
    this.viewMode = this.initializeViewMode();



    this.policyForm = this.fb.group({
      policyName: new FormControl('', [ Validators.required, Validators.minLength(8), Validators.maxLength(40) ]),
      validUntil: new FormControl('', [ Validators.required, this.futureDateValidator ]),
      bpns: new FormControl('', [ Validators.required, this.viewMode === ViewMode.CREATE ? BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn') : BaseInputHelper.getCustomPatternValidator(bpnListRegex, 'bpn') ]),
      accessType: new FormControl<string>(PolicyAction.ACCESS),
      constraints: this.fb.array([]),
      constraintLogicType: new FormControl(ConstraintLogicType.AND),
    });

    if (this.viewMode !== ViewMode.CREATE) {
      this.setSelectedPolicy();
      this.selectedPolicySubscription = this.policyFacade.selectedPolicy$.subscribe(next => {
        this.selectedPolicy = next?.data;
        if (next?.data) {
          console.log(next.data);
          this.updatePolicyForm(this.selectedPolicy);
        }
      });

    } else {
      this.addConstraintFormGroup();
    }

  }

  initializeViewMode(): ViewMode {
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
    this.policyFacade.setSelectedPolicyById(this.route.snapshot.paramMap.get('policyId'));
  }

  addConstraintFormGroup() {
    this.constraints.push(this.fb.group({
      leftOperand: new FormControl('', [ Validators.required ]),
      operator: new FormControl<string>('='),
      rightOperand: new FormControl('', [ Validators.required ]),
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
    const request = this.viewMode === ViewMode.EDIT ? this.policyFacade.updatePolicy(policyEntry) : this.policyFacade.createPolicy(policyEntry);
    request.subscribe({
      next: () => {
        this.toastService.success('pageAdmin.policyManagement.successMessage');
        this.router.navigate([ 'admin', 'policies', policyEntry.payload.policy.policyId ]);
      },
      error: () => this.toastService.error('pageAdmin.policyManagement.errorMessage'),
    });
  }

  /**
   * This Feature is commented out for now, because uploading/downloading Templates/Policies is
   * currently not a requirement but could be one in the future.
   */
  /*

    onFileSelected(event: Event) {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files.length > 0) {
        this.templateFile = input.files[0];
        this.templateFileName = this.templateFile.name;
        this.templateError = '';
      }
    }

   */

  navigateToEditView() {
    this.router.navigate([ 'admin/policies/', 'edit', this.selectedPolicy.policyId ]);
  }

  /**
   * This Feature is commented out for now, because uploading/downloading Templates/Policies is
   * currently not a requirement but could be one in the future.
   */
  /*
    downloadTemplateAsJsonFile() {
      const policy = this.mapPolicyFormToPolicyEntry();
      const data = JSON.stringify(policy, null, 2);
      const blob = new Blob([ data ], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = policy.payload.policy.policyId.length ? 'policy-template-' + policy.payload.policy.policyId : 'policy-template';
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);

    }



    applyTemplate() {
      if (!this.templateFile) {
        return;
      }
      const reader = new FileReader();

      reader.onload = () => {
        const fileContent = reader.result;
        if (typeof fileContent === 'string') {
          if (!PoliciesAssembler.validatePoliciesTemplate(JSON.parse(fileContent))) {
            this.templateError = 'pageAdmin.policyManagement.templateErrorMessage';
            return;
          }
          let policyEntry = PoliciesAssembler.mapToPolicyEntryList(JSON.parse(fileContent));
          let policy = PoliciesAssembler.assemblePolicy(policyEntry[0].payload.policy);
          this.toastService.success('pageAdmin.policyManagement.changeSuccessMessage');
          this.updatePolicyForm(policy);
        }
      };

      reader.onerror = () => {
        this.toastService.error(reader.error?.message);
      };

      reader.readAsText(this.templateFile);

    }
  */
  updatePolicyForm(policy: Policy) {

    const isFromTemplate = !policy?.permissions[0]?.constraints;
    this.policyForm.patchValue({
      policyName: policy?.policyName,
      validUntil: policy?.validUntil,
      bpns: policy?.bpn ?? policy?.businessPartnerNumber,
      accessType: policy?.accessType,
      constraintLogicType: policy?.permissions[0]?.constraints?.and?.length ? ConstraintLogicType.AND : ConstraintLogicType.OR,
    });
    if (isFromTemplate) {
      this.policyForm.patchValue({ constraintLogicType: policy?.permissions[0]?.constraint?.and?.length ? ConstraintLogicType.AND : ConstraintLogicType.OR });
    }

    let permissionList = policy?.permissions[0]?.constraints?.and?.length ? policy?.permissions[0]?.constraints?.and : policy?.permissions[0]?.constraints?.or;
    if (!permissionList) {
      permissionList = policy?.permissions[0]?.constraint?.and?.length ? policy?.permissions[0]?.constraint?.and : policy?.permissions[0]?.constraint?.or;
    }

    let constraintsList = permissionList.map((constraint) => this.fb.group({
      leftOperand: this.fb.control<string>(constraint.leftOperand, [ Validators.required ]),
      operator: this.fb.control<string>(constraint?.operator?.['@id'] ? getOperatorTypeSign(OperatorType[constraint?.operator?.['@id'].toUpperCase()]) : getOperatorTypeSign(constraint?.operatorTypeResponse)),
      rightOperand: this.fb.control<string>(constraint['odrl:rightOperand'] ?? constraint.rightOperand, [ Validators.required ]),
    }));


    this.policyForm.setControl('constraints', this.fb.array(constraintsList));


    if (this.viewMode === ViewMode.VIEW) {
      this.policyForm.disable();
    }

    if (this.viewMode === ViewMode.EDIT) {
      this.policyForm.disable();
      this.constraints.controls.forEach(control => {
        control.disable();
      });
      this.policyForm.get('validUntil').enable();
      this.policyForm.get('bpns').enable();
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
      validUntil: this.policyForm.get('validUntil').getRawValue() + ':00.000000000Z',
      businessPartnerNumber: this.viewMode === ViewMode.CREATE ? this.policyForm.get('bpns').getRawValue() : this.policyForm.get('bpns').getRawValue()?.trim()?.split(','),
      payload: {
        '@context': {
          odrl: 'http://www.w3.org/ns/odrl/2/',
        },
        '@id': this.policyForm.get('policyName').getRawValue(),
        policy: {
          policyId: this.policyForm.get('policyName').getRawValue(),
          createdOn: new Date(Date.now()).toISOString().replace('Z', '000000Z'),
          validUntil: this.policyForm.get('validUntil').getRawValue() + ':00.000000000Z',
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

    if (policyEntry.payload.policy.permissions[0].constraint?.and?.length) {
      delete policyEntry.payload.policy.permissions[0].constraint?.or;
    } else {
      delete policyEntry.payload.policy.permissions[0].constraint?.and;
    }

    return policyEntry;
  }

  private futureDateValidator = (control: FormControl): ValidationErrors | null => {
    if (!control.value) {
      return null;
    }

    const currentDate = new Date();
    const inputDate = new Date(control.value);
    if (inputDate < currentDate) {
      return { pastDate: true };
    }
    return null;
  };


  protected readonly ViewMode = ViewMode;
  protected readonly OperatorTypesAsSelectOptionsList = OperatorTypesAsSelectOptionsList;
  protected readonly ConstraintLogicTypeAsSelectOptionsList = ConstraintLogicTypeAsSelectOptionsList;
}

