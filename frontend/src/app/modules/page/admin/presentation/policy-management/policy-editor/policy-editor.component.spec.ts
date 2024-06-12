import { ActivatedRoute, Router } from '@angular/router';
import { AdminModule } from '@page/admin/admin.module';
import { PoliciesFacade } from '@page/admin/presentation/policy-management/policies/policies.facade';
import { Policy } from '@page/policies/model/policy.model';
import { View } from '@shared/model/view.model';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

import { PolicyEditorComponent } from './policy-editor.component';

describe('PolicyEditorComponent', () => {


  const renderPolicyEditorComponent = async (policyFacadeMock: any) => {

    return await renderComponent(PolicyEditorComponent, {
      imports: [ AdminModule ],
      providers: [ {
        provide: PoliciesFacade,
        useValue: policyFacadeMock,
      },
        {
          provide: Router,
          useValue: {
            url: 'https://test.net/admin/policies/edit/default',
          },
        },
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ id: 'default' }),
            queryParams: of({}),
            snapshot: {
              paramMap: {
                get: (key: string) => 'default',
              },
            },
          },
        },
      ],
    });
  }

  it('should create', async function() {
    const dummyPolicy: View<Policy> = {
      data: {
        policyId: 'default',
        createdOn: new Date().toISOString(),
        validUntil: new Date().toISOString(),
        permissions: [],
      },
    };

    const policyFacadeMock = jasmine.createSpyObj('policyFacade', [ 'setSelectedPolicyById' ]);
    policyFacadeMock.setSelectedPolicyById.and.returnValue(undefined);

    Object.defineProperty(policyFacadeMock, 'selectedPolicy$', {
      get: () => of(dummyPolicy),
    });


    const component = await renderPolicyEditorComponent(policyFacadeMock);




    expect(component).toBeTruthy();
  });
});
