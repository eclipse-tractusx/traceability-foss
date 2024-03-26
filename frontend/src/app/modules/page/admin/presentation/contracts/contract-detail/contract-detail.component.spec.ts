import { AdminModule } from '@page/admin/admin.module';
import { ContractsFacade } from '@page/admin/presentation/contracts/contracts.facade';
import { renderComponent } from '@tests/test-render.utils';

import { ContractDetailComponent } from './contract-detail.component';

describe('ContractDetailComponent', () => {

  const renderContractDetailedComponent = () => renderComponent(ContractDetailComponent, {imports: [AdminModule], providers: [ContractsFacade]})

  it('should create', async() => {
    const {fixture} = await renderContractDetailedComponent();
    const {componentInstance} = fixture;
    expect(componentInstance).toBeTruthy();
  });
});
