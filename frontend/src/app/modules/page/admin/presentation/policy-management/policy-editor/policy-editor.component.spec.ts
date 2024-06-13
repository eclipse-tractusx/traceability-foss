import { APP_INITIALIZER } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { PoliciesFacade } from '@page/admin/presentation/policy-management/policies/policies.facade';
import { Policy } from '@page/policies/model/policy.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { ViewMode } from '@shared/model/view.model';
import { renderComponent } from '@tests/test-render.utils';
import { I18NEXT_SERVICE, ITranslationService } from 'angular-i18next';
import { of, Subject } from 'rxjs';
import { PolicyEditorComponent } from './policy-editor.component';

describe('PolicyEditorComponent', () => {
  let mockPoliciesFacade: Partial<PoliciesFacade>;
  let mockToastService: Partial<ToastService>;
  let mockRouter: Partial<Router>;
  let mockRoute: Partial<ActivatedRoute>;
  let selectedPolicySubject: Subject<{ data: Policy }>;

  beforeEach(() => {
    selectedPolicySubject = new Subject();
    mockPoliciesFacade = {
      selectedPolicy$: selectedPolicySubject.asObservable(),
      setSelectedPolicyById: jasmine.createSpy(),
      createPolicy: jasmine.createSpy().and.returnValue(of({})),
      updatePolicy: jasmine.createSpy().and.returnValue(of({})),
    };
    mockToastService = {
      success: jasmine.createSpy(),
      error: jasmine.createSpy(),
    };
    mockRouter = {
      navigate: jasmine.createSpy(),
      url: 'admin/policies/create',
    };


    mockRoute = {
      snapshot: {
        paramMap: convertToParamMap({ policyId: '1' }),
        url: [],
        params: {},
        queryParams: {},
        fragment: null,
        data: {},
        outlet: 'primary',
        component: PolicyEditorComponent,
        root: null,
        parent: null,
        firstChild: null,
        children: [],
        pathFromRoot: [],
        toString: () => '',
        routeConfig: null,
        title: '',
        queryParamMap: convertToParamMap({}),
      },
    };
  });

  const renderPolicyEditorComponent = () =>
    renderComponent(PolicyEditorComponent, {
      imports: [ RouterTestingModule ],
      providers: [
        { provide: PoliciesFacade, useValue: mockPoliciesFacade },
        { provide: ToastService, useValue: mockToastService },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockRoute },
        FormBuilder,
        {
          provide: APP_INITIALIZER,
          useFactory: (i18next: ITranslationService) => {
            return () =>
              i18next.init({
                lng: 'en',
                supportedLngs: [ 'en', 'de' ],
                resources: {},
              });
          },
          deps: [ I18NEXT_SERVICE ],
          multi: true,
        },
      ],
    });

  it('should create', async () => {
    const { fixture } = await renderPolicyEditorComponent();
    const { componentInstance } = fixture;
    expect(componentInstance).toBeTruthy();
  });

  it('should initialize the form in create mode', async () => {
    const { fixture } = await renderPolicyEditorComponent();
    const { componentInstance } = fixture;
    expect(componentInstance.viewMode).toBe(ViewMode.CREATE);
    expect(componentInstance.policyForm).toBeTruthy();
    expect(componentInstance.policyForm.get('policyName').valid).toBeFalsy(); // Validators required
    expect(componentInstance.policyForm.get('bpns').valid).toBeFalsy(); // Validators required
    expect(componentInstance.policyForm.get('validUntil').valid).toBeFalsy(); // Validators required
  });

  it('should add and remove constraints', async () => {
    const { fixture } = await renderPolicyEditorComponent();
    const { componentInstance } = fixture;
    expect(componentInstance.constraints.length).toBe(1);

    componentInstance.addConstraintFormGroup();
    expect(componentInstance.constraints.length).toBe(2);

    componentInstance.removeConstraintFormGroup(0);
    expect(componentInstance.constraints.length).toBe(1);
  });

  it('should move constraints up and down', async () => {
    const { fixture } = await renderPolicyEditorComponent();
    const { componentInstance } = fixture;
    componentInstance.addConstraintFormGroup();
    componentInstance.addConstraintFormGroup();

    componentInstance.constraints.at(0).get('leftOperand').setValue('constraint 1');
    componentInstance.constraints.at(1).get('leftOperand').setValue('constraint 2');

    componentInstance.moveConstraintDown(0);
    expect(componentInstance.constraints.at(0).get('leftOperand').value).toBe('constraint 2');
    expect(componentInstance.constraints.at(1).get('leftOperand').value).toBe('constraint 1');

    componentInstance.moveConstraintUp(1);
    expect(componentInstance.constraints.at(0).get('leftOperand').value).toBe('constraint 1');
    expect(componentInstance.constraints.at(1).get('leftOperand').value).toBe('constraint 2');
  });

  it('should navigate back', async () => {
    const { fixture } = await renderPolicyEditorComponent();
    const { componentInstance } = fixture;
    componentInstance.navigateBack();
    expect(mockRouter.navigate).toHaveBeenCalledWith([ 'admin/policies' ]);
  });

  it('should save policy in create mode', async () => {
    const { fixture } = await renderPolicyEditorComponent();
    const { componentInstance } = fixture;
    componentInstance.policyForm.patchValue({
      policyName: 'Test Policy',
      validUntil: new Date().toISOString(),
      bpns: 'BPN0001',
      accessType: 'access',
      constraintLogicType: 'AND',
    });
    componentInstance.addConstraintFormGroup();
    componentInstance.constraints.at(0).patchValue({
      leftOperand: 'leftOperand',
      operator: '=',
      rightOperand: 'rightOperand',
    });

    componentInstance.savePolicy();
    expect(mockPoliciesFacade.createPolicy).toHaveBeenCalled();
    expect(mockToastService.success).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalled();
  });
});
