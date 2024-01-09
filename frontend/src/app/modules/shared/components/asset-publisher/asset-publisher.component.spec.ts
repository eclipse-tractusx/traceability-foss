import { Policy } from '@shared/components/asset-publisher/policy.model';
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
    const publishSpy = spyOn(componentInstance.assetPublisherService, 'publishAssets');
    const submittedSpy = spyOn(componentInstance.submitted, 'emit');

    const dummyPolicy: Policy = { id: 'id-1', name: 'myPolicy' };
    componentInstance.policyFormControl.setValue(dummyPolicy.id);

    componentInstance.publish();

    expect(publishSpy).toHaveBeenCalledWith(dummyPolicy.id);

    expect(componentInstance.policyFormControl.value).toBeNull();
    expect(submittedSpy).toHaveBeenCalled();
  });

});
