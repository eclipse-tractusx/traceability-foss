import { Policy, PolicyAction } from '@page/policies/model/policy.model';
import { PolicyService } from '@shared/service/policy.service';
import { renderComponent } from '@tests/test-render.utils';
import { BehaviorSubject, of } from 'rxjs';

import { AssetPublisherComponent } from './asset-publisher.component';

describe('AssetPublisherComponent', () => {
  const policyServiceSpy = jasmine.createSpyObj('PolicyService', ['getPolicies', 'publishAssets']);
  const isOpenSubject = new BehaviorSubject<boolean>(true);

  const renderAssetPublisherComponent = (policyResponseMap: Record<string, any[]> = {}) => {
    policyServiceSpy.getPolicies.and.returnValue(of(policyResponseMap));
    policyServiceSpy.publishAssets.and.returnValue(of({}));

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
    const dummyPolicy: Policy = {
      policyId: 'id-1',
      createdOn: '2024-05-29T06:18:40Z',
      validUntil: '2029-05-29T06:18:40Z',
      permissions: [ { action: PolicyAction.ACCESS, constraint: { and: [], or: null } } ],
    };
    const policyResponseMap = {
      'bpn-1': [
        {
          validUntil: '2029-05-29T06:18:40Z',
          payload: { '@context': { odrl: 'odrl' }, '@id': 'id-1', policy: dummyPolicy },
        },
      ],
    };
    const { fixture } = await renderAssetPublisherComponent(policyResponseMap);
    const { componentInstance } = fixture;

    const submittedSpy = spyOn(componentInstance.submitted, 'emit');

    componentInstance.policyFormControl.setValue(dummyPolicy.policyId);
    fixture.detectChanges();

    componentInstance.publish();

    await fixture.whenStable();
    expect(policyServiceSpy.publishAssets).toHaveBeenCalledWith([], dummyPolicy.policyId);
    expect(componentInstance.policyFormControl.value).toBeNull();
    expect(submittedSpy).toHaveBeenCalled();
  });

  it('should set policies when requesting policies', async function() {
    const dummyPolicy: Policy = {
      policyId: 'id-1',
      createdOn: '2024-05-29T06:18:40Z',
      validUntil: '2029-05-29T06:18:40Z',
      permissions: [ { action: PolicyAction.ACCESS, constraint: { and: [], or: null } } ],
    };
    const policyResponseMap = {
      'bpn-1': [
        {
          validUntil: '2029-05-29T06:18:40Z',
          payload: { '@context': { odrl: 'odrl' }, '@id': 'id-1', policy: dummyPolicy },
        },
      ],
    };
    const { fixture } = await renderAssetPublisherComponent(policyResponseMap);
    const { componentInstance } = fixture;
    const submittedSpy = spyOn(componentInstance.submitted, 'emit');


    componentInstance.policyFormControl.setValue(dummyPolicy.policyId);
    fixture.detectChanges();

    componentInstance.publish();

    await fixture.whenStable();
    expect(policyServiceSpy.publishAssets).toHaveBeenCalledWith([], dummyPolicy.policyId);
    expect(policyServiceSpy.getPolicies).toHaveBeenCalled();
    expect(componentInstance.policiesList.length).toBe(1);
    expect(componentInstance.policiesList[0].policyId).toBe('id-1');
    expect(componentInstance.policyFormControl.value).toBeNull();
    expect(submittedSpy).toHaveBeenCalled();
  });

});
