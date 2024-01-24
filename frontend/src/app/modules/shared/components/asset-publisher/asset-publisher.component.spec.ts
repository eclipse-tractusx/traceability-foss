import { Policy } from '@page/policies/model/policy.model';
import { renderComponent } from '@tests/test-render.utils';

import { AssetPublisherComponent } from './asset-publisher.component';

describe('AssetPublisherComponent', () => {


  const renderAssetPublisherComponent = () => {
    return renderComponent(AssetPublisherComponent, {

    })
  }

  it('should create', async () => {
    const {fixture} = await renderAssetPublisherComponent()
    expect(fixture).toBeTruthy();
  });

  it('should publish assets and emit submitted event', async () => {
    const {fixture} = await renderAssetPublisherComponent();
    const {componentInstance} = fixture
    const publishSpy = spyOn(componentInstance.policyService, 'publishAssets');
    const submittedSpy = spyOn(componentInstance.submitted, 'emit');

    const dummyPolicy: Policy = { policyId: 'id-1', createdOn: 'testdate', validUntil: 'testdate' };
    componentInstance.policyFormControl.setValue(dummyPolicy.policyId);
    componentInstance.selectedAssets = [];

    componentInstance.publish();

    expect(publishSpy).toHaveBeenCalledWith([],dummyPolicy.policyId);

    expect(componentInstance.policyFormControl.value).toBeNull();
    expect(submittedSpy).toHaveBeenCalled();
  });

});
