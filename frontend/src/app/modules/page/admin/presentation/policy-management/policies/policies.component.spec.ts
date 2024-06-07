import { MatDialogModule } from '@angular/material/dialog';
import { RouterTestingModule } from '@angular/router/testing';
import { RoleService } from '@core/user/role.service';
import { AdminModule } from '@page/admin/admin.module';
import { PoliciesFacade } from '@page/admin/presentation/policy-management/policies/policies.facade';
import { ToastService } from '@shared/components/toasts/toast.service';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
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

  const renderPoliciesComponent = () => renderComponent(PoliciesComponent, {
    imports: [
      AdminModule,
      MatDialogModule,
      RouterTestingModule,
    ],
    providers: [
      { provide: PoliciesFacade, useValue: policyFacadeMock },
      { provide: ToastService, useValue: toastServiceMock },
      { provide: RoleService, useValue: roleServiceMock },
    ],
  });

  it('should create', async () => {
    const { fixture } = await renderPoliciesComponent();
    const { componentInstance } = fixture;

    expect(componentInstance).toBeTruthy();
  });
});
