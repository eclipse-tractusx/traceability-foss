import { fakeAsync, tick } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { RoleService } from '@core/user/role.service';
import { AdminModule } from '@page/admin/admin.module';
import { PoliciesFacade } from '@page/admin/presentation/policy-management/policies/policies.facade';
import { Policy } from '@page/policies/model/policy.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { RenderResult } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of, throwError } from 'rxjs';
import { getPolicies } from '../../../../../../mocks/services/policy-mock/policy.model';
import { PoliciesComponent } from './policies.component';

describe('PoliciesComponent', () => {
  const policyFacadeMock = {
    policies$: of(getPolicies()),
    setPolicies: jasmine.createSpy('setPolicies'),
    deletePolicies: jasmine.createSpy('deletePolicies').and.returnValue(of({})),
  };

  const toastServiceMock = {
    success: jasmine.createSpy('success'),
    error: jasmine.createSpy('error'),
  };

  const roleServiceMock = {
    isAdmin: jasmine.createSpy('isAdmin').and.returnValue(true),
  };

  const routerMock = {
    navigate: jasmine.createSpy('navigate'),
  };

  const matDialogMock = {
    open: jasmine.createSpy('open').and.returnValue({
      afterClosed: () => of(true),
    }),
  };

  const renderPoliciesComponent = async (): Promise<RenderResult<PoliciesComponent, PoliciesComponent>> => await renderComponent(PoliciesComponent, {
    imports: [
      AdminModule,
      MatDialogModule,
      RouterTestingModule,
    ],
    providers: [
      { provide: PoliciesFacade, useValue: policyFacadeMock },
      { provide: ToastService, useValue: toastServiceMock },
      { provide: RoleService, useValue: roleServiceMock },
      { provide: Router, useValue: routerMock },
      { provide: MatDialog, useValue: matDialogMock },
    ],
  });

  it('should create', async () => {
    const { fixture } = await renderPoliciesComponent();
    const componentInstance = fixture.componentInstance;

    expect(componentInstance).toBeTruthy();
  });

  it('should update selectedPolicies on multiSelection', async () => {
    const { fixture } = await renderPoliciesComponent();
    const componentInstance = fixture.componentInstance;
    const mockPolicies = [ { policyId: '1' }, { policyId: '2' } ] as Policy[];

    componentInstance.multiSelection(mockPolicies);
    expect(componentInstance.selectedPolicies).toEqual(mockPolicies);
  });

  it('should navigate to detailed view on openDetailedView', async () => {
    const { fixture } = await renderPoliciesComponent();
    const componentInstance = fixture.componentInstance;
    const mockPolicy = { policyId: '1' };

    componentInstance.openDetailedView(mockPolicy);
    expect(routerMock.navigate).toHaveBeenCalledWith([ 'admin/policies/1' ]);
  });

  it('should navigate to edit view on openEditView', async () => {
    const { fixture } = await renderPoliciesComponent();
    const componentInstance = fixture.componentInstance;
    const mockPolicy = { policyId: '1' };

    componentInstance.openEditView(mockPolicy);
    expect(routerMock.navigate).toHaveBeenCalledWith([ 'admin/policies/edit/1' ]);
  });

  it('should open deletion dialog and delete policies on confirmation', fakeAsync(async () => {
    const { fixture } = await renderPoliciesComponent();
    const componentInstance = fixture.componentInstance;
    componentInstance.selectedPolicies = [ { policyId: '1' }, { policyId: '2' } ] as Policy[];

    componentInstance.openDeletionDialog();
    expect(matDialogMock.open).toHaveBeenCalled();


    tick(); // Simulate passage of time until afterClosed completes

    expect(policyFacadeMock.deletePolicies).toHaveBeenCalledWith(componentInstance.selectedPolicies);
    expect(policyFacadeMock.setPolicies).toHaveBeenCalled();
  }));

  it('should call deletePolicies and handle success', fakeAsync(async () => {
    const { fixture } = await renderPoliciesComponent();
    const componentInstance = fixture.componentInstance;
    componentInstance.selectedPolicies = [ { policyId: '1' }, { policyId: '2' } ] as Policy[];

    componentInstance.deletePolicies();

    tick(); // Simulate passage of time until the observable completes

    expect(policyFacadeMock.deletePolicies).toHaveBeenCalledWith(componentInstance.selectedPolicies);
    expect(policyFacadeMock.setPolicies).toHaveBeenCalled();
  }));

  it('should call deletePolicies and handle success', fakeAsync(async () => {
    const { fixture } = await renderPoliciesComponent();
    const componentInstance = fixture.componentInstance;
    componentInstance.selectedPolicies = [ { policyId: '1' }, { policyId: '2' } ] as Policy[];

    componentInstance.multiSelection(componentInstance.selectedPolicies);
    expect(componentInstance.selectedPoliciesInfoLabel).toEqual('pageAdmin.policyManagement.selectedPolicies');

    componentInstance.selectedPolicies = [ { policyId: '1' } ] as Policy[];

    componentInstance.multiSelection(componentInstance.selectedPolicies);
    expect(componentInstance.selectedPoliciesInfoLabel).toEqual('pageAdmin.policyManagement.selectedPolicy');

  }));

  it('should call deletePolicies and handle error', fakeAsync(async () => {
    policyFacadeMock.deletePolicies.and.returnValue(throwError('error'));

    const { fixture } = await renderPoliciesComponent();
    const componentInstance = fixture.componentInstance;
    componentInstance.selectedPolicies = [ { policyId: '1' }, { policyId: '2' } ] as Policy[];

    componentInstance.deletePolicies();

    tick(); // Simulate passage of time until the observable completes

    expect(policyFacadeMock.deletePolicies).toHaveBeenCalledWith(componentInstance.selectedPolicies);
    expect(toastServiceMock.error).toHaveBeenCalledWith('pageAdmin.policyManagement.deleteError');
  }));
});
