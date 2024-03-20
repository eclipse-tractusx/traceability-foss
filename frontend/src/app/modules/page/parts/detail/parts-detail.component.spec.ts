import { LayoutModule } from '@layout/layout.module';
import { PartsState } from '@page/parts/core/parts.state';
import { PartsDetailModule } from '@page/parts/detail/parts-detail.module';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { PartDetailsState } from '@shared/modules/part-details/core/partDetails.state';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { MOCK_part_1 } from '../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';

import { PartsDetailComponent } from './parts-detail.component';

let PartsStateMock: PartsState;
let PartDetailsStateMock: PartDetailsState;

const part = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);
describe('PartsDetailComponent', () => {
  beforeEach(() => {
    PartDetailsStateMock = new PartDetailsState();
    PartDetailsStateMock.selectedPart = { data: part };

    PartsStateMock = new PartsState();
  });

  const renderPartsDetailComponent = async ({ roles = [] } = {}) => {
    return await renderComponent(PartsDetailComponent, {
      declarations: [ PartsDetailComponent ],
      imports: [ PartsDetailModule, LayoutModule ],
      providers: [
        PartDetailsFacade,
        { provide: PartsState, useFactory: () => PartsStateMock },
        { provide: PartDetailsState, useFactory: () => PartDetailsStateMock },
      ],
      roles,
    });
  };

  it('should render an open sidenav with part details', async () => {
    const {fixture} = await renderPartsDetailComponent();
    const {componentInstance} = fixture;
    const spy = spyOn(componentInstance.partDetailsFacade, 'setPartById')
    const nameElement = await screen.findByText('BMW AG');
    const productionDateElement = await screen.findByText('2022-02-04T13:48:54');

    expect(nameElement).toBeInTheDocument();
    expect(productionDateElement).toBeInTheDocument();
  });

  it('should render child-component table', async () => {
    const {fixture} = await renderPartsDetailComponent({roles: ['user']});
    const {componentInstance} = fixture;

    const childTableHeadline = await screen.findByText('partDetail.investigation.headline');
    expect(childTableHeadline).toBeInTheDocument();
    expect(await screen.findByText('partDetail.investigation.noSelection.header')).toBeInTheDocument();
  });
});
