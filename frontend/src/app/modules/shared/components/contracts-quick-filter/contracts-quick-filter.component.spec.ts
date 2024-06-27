import { ContractType } from '@page/admin/core/admin.model';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { ContractsQuickFilterComponent } from './contracts-quick-filter.component';

describe('BomLifecycleActivatorComponent', () => {

  const renderQuickFilter = () => {
    return renderComponent(ContractsQuickFilterComponent, {
      imports: [ SharedModule ],
      providers: [],
      componentProperties: {},
    });
  };

  it('should create the component', async () => {
    const { fixture } = await renderQuickFilter();
    const { componentInstance } = fixture;
    expect(componentInstance).toBeTruthy();
  });


  it('should set state when initially called', async () => {
    const { fixture } = await renderQuickFilter();
    const { componentInstance } = fixture;
    componentInstance.activeContractTypes = [];
    componentInstance.emitQuickFilter(ContractType.ASSET_AS_BUILT);
    expect(componentInstance.activeContractTypes).toEqual([ ContractType.ASSET_AS_BUILT ]);
  });

  it('should unset state when called again', async () => {
    const { fixture } = await renderQuickFilter();
    const { componentInstance } = fixture;
    componentInstance.activeContractTypes = [ ContractType.ASSET_AS_PLANNED ];
    componentInstance.emitQuickFilter(ContractType.ASSET_AS_PLANNED);
    expect(componentInstance.activeContractTypes).toEqual([]);
  });
});
