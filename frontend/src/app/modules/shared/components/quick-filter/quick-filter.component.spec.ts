import { Owner } from '@page/parts/model/owner.enum';
import { QuickfilterService } from '@shared/service/quickfilter.service';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
import { QuickFilterComponent } from './quick-filter.component';

describe('BomLifecycleActivatorComponent', () => {
  let quickFilterServiceSpy: jasmine.SpyObj<QuickfilterService>;

  const renderQuickFilter = async(initialOwner = Owner.UNKNOWN) => {
    quickFilterServiceSpy = jasmine.createSpyObj<QuickfilterService>(
      'QuickfilterService',
      [ 'getOwner', 'setOwner', 'isQuickFilterSet' ],
      { owner$: of(initialOwner) } // if the component subscribes in the future
    );
    quickFilterServiceSpy.getOwner.and.returnValue(initialOwner);
    quickFilterServiceSpy.isQuickFilterSet.and.returnValue(initialOwner !== Owner.UNKNOWN);

    return renderComponent(QuickFilterComponent, {
      imports: [ SharedModule ],
      providers: [{ provide: QuickfilterService, useValue: quickFilterServiceSpy }],
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

  it('should set state when initially called with a new owner', async () => {
    const { fixture } = await renderQuickFilter();
    const { componentInstance } = fixture;

    expect(componentInstance.owner).toBe(Owner.UNKNOWN);
    componentInstance.emitQuickFilter(Owner.OWN);
    fixture.detectChanges();

    expect(componentInstance.owner).toBe(Owner.OWN);
    expect(quickFilterServiceSpy.setOwner).toHaveBeenCalledWith(Owner.OWN);
  });

  it('should unset state when toggled again to the same owner', async () => {
    const { fixture } = await renderQuickFilter(Owner.OWN);
    const { componentInstance } = fixture;

    expect(componentInstance.owner).toBe(Owner.OWN);

    componentInstance.emitQuickFilter(Owner.OWN);
    fixture.detectChanges();

    expect(componentInstance.owner).toBe(Owner.UNKNOWN);
    expect(quickFilterServiceSpy.setOwner).toHaveBeenCalledWith(Owner.UNKNOWN);
  });

  it('should emit buttonClickEvent when emitQuickFilter is called', async () => {
    const { fixture } = await renderQuickFilter();
    const { componentInstance } = fixture;
    spyOn(componentInstance.buttonClickEvent, 'emit');

    componentInstance.emitQuickFilter(Owner.SUPPLIER);

    expect(componentInstance.buttonClickEvent.emit).toHaveBeenCalledWith(Owner.SUPPLIER);
  });
});
