import { Owner } from '@page/parts/model/owner.enum';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { QuickFilterComponent } from './quick-filter.component';

describe('BomLifecycleActivatorComponent', () => {

  const renderQuickFilter = () => {
    return renderComponent(QuickFilterComponent, {
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
    componentInstance.owner = null;
    componentInstance.emitQuickFilter(Owner.OWN);
    expect(componentInstance.owner).toBe(Owner.OWN);
  });

  it('should unset state when called again', async () => {
    const { fixture } = await renderQuickFilter();
    const { componentInstance } = fixture;
    componentInstance.owner = Owner.OWN;
    componentInstance.emitQuickFilter(Owner.OWN);
    expect(componentInstance.owner).toBe(Owner.UNKNOWN);
  });
});
