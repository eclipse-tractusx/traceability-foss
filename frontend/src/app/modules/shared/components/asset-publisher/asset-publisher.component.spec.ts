import { Policy } from '@page/policies/model/policy.model';
import { PolicyService } from '@shared/service/policy.service';
import { renderComponent } from '@tests/test-render.utils';
import { BehaviorSubject, of } from 'rxjs';

import { AssetPublisherComponent } from './asset-publisher.component';

fdescribe('AssetPublisherComponent', () => {
  const policyServiceSpy = jasmine.createSpyObj('PolicyService', ['getPolicies', 'publishAssets']);
  const isOpenSubject = new BehaviorSubject<boolean>(true);

  const renderAssetPublisherComponent = (input?) => {
    return renderComponent(AssetPublisherComponent, {
      providers: [ { provide: PolicyService, useValue: policyServiceSpy }],
      componentInputs: {
        isOpen: isOpenSubject.asObservable(),
      }
    })
  }

  it('should create', async () => {
    const {fixture} = await renderAssetPublisherComponent()
    expect(fixture).toBeTruthy();
  });

  it('should publish assets and emit submitted event', async function() {
    const { fixture } = await renderAssetPublisherComponent();
    const { componentInstance } = fixture;

    const dummyPolicy: Policy = { policyId: 'id-1', createdOn: 'testdate', validUntil: 'testdate' };

    policyServiceSpy.publishAssets.and.returnValue(of({}));
    policyServiceSpy.getPolicies.and.returnValue(of([dummyPolicy]))

    const submittedSpy = spyOn(componentInstance.submitted, 'emit');

    componentInstance.policyFormControl.setValue(dummyPolicy.policyId);
    fixture.detectChanges();

    componentInstance.publish();

    fixture.whenStable().then(() => {
      expect(policyServiceSpy.publishAssets).toHaveBeenCalledWith([], dummyPolicy.policyId);
      expect(componentInstance.policyFormControl.value).toBeNull();
      expect(submittedSpy).toHaveBeenCalled();
    });
  });

});
