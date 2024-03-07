import { TestBed } from '@angular/core/testing';
import { AdminModule } from '@page/admin/admin.module';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { AdminService } from '@page/admin/core/admin.service';
import { renderComponent } from '@tests/test-render.utils';

import { ContractTableComponent } from './contract-table.component';

describe('ContractTableComponent', () => {
  const renderContractTableComponent = () => renderComponent(ContractTableComponent, { imports: [AdminModule]})

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContractTableComponent],
      providers: [AdminFacade, AdminService]
    });

  });

  it('should create', async() => {
    const {fixture} = await renderContractTableComponent();
    const {componentInstance} = fixture;
    expect(componentInstance).toBeTruthy();
  });
});
