import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { I18NextModule } from 'angular-i18next';

import { AssetPublisherComponent } from './asset-publisher.component';

fdescribe('AssetPublisherComponent', () => {
  let component: AssetPublisherComponent;
  let fixture: ComponentFixture<AssetPublisherComponent>;

  const renderAssetPublisherComponent = () => {
    return renderComponent(AssetPublisherComponent, {

    })
  }

  it('should create', async () => {
    const {fixture} = await renderAssetPublisherComponent()
    expect(fixture).toBeTruthy();
  });
});
