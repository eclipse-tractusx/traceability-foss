import { Component } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { PoliciesFacade } from '@page/admin/presentation/policy-management/policies/policies.facade';
import { OperatorTypesAsSelectModel } from '@page/admin/presentation/policy-management/policy-editor/policy-data';
import { Policy, PolicyAction } from '@page/policies/model/policy.model';
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

  constructor(private readonly router: Router, public readonly policyFacade: PoliciesFacade, private fb: FormBuilder) {
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
      accessType: new FormControl<string>(PolicyAction.ACCESS.toUpperCase()),
      constraints: this.fb.array([]),
    });

    if (this.viewMode !== ViewMode.CREATE) {
      this.setSelectedPolicy();
      this.selectedPolicySubscription = this.policyFacade.selectedPolicy$.subscribe(next => {
        this.selectedPolicy = next?.data;
        if (next?.data) {
          this.policyForm.patchValue({
            policyName: this.selectedPolicy?.policyName,
            validUntil: this.selectedPolicy?.validUntil,
            bpns: this.selectedPolicy?.bpn,
            accessType: this.selectedPolicy?.accessType[0].toUpperCase(),
          });


          let permissionList = this.selectedPolicy.permissions[0].constraint.and.length ? this.selectedPolicy.permissions[0].constraint.and : this.selectedPolicy.permissions[0].constraint.or;
          let constraintsList = permissionList.map((constraint) => this.fb.group({
            leftOperand: this.fb.control<string>(constraint.leftOperand),
            operator: this.fb.control<string>(constraint.operator['@id'].toUpperCase()),
            rightOperand: this.fb.control<string>(constraint['odrl:rightOperand']),
          }));

          this.policyForm.setControl('constraints', this.fb.array(constraintsList));
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
    const policyIdFromUrl = this.router.url.split('/').pop();
    this.policyFacade.setSelectedPolicyById(this.router.url.split('/').pop());
  }

  addConstraintFormGroup() {
    this.constraints.push(this.fb.group({
      leftOperand: new FormControl(''),
      operator: new FormControl<string>(null),
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

  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.templateFile = input.files[0];
      this.templateFileName = this.templateFile.name;
    }
  }

  selectTemplateFile() {

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

  protected readonly ViewMode = ViewMode;

  protected readonly PolicyAction = PolicyAction;

  protected readonly OperatorTypesAsSelectModel = OperatorTypesAsSelectModel;


}
