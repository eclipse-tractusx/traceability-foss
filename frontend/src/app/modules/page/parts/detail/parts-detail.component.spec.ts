import { LayoutModule } from '@layout/layout.module';
import { PartsState } from '@page/parts/core/parts.state';
import { PartsDetailModule } from '@page/parts/detail/parts-detail.module';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Owner } from '@page/parts/model/owner.enum';
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

  it('should render part details', async () => {
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
  });


  it('should set selected part on null if click on relation page', async () => {
    const {fixture} = await renderPartsDetailComponent({roles: ['user']});
    const {componentInstance} = fixture;

    componentInstance.openRelationPage(part);
    expect(componentInstance.partDetailsFacade.selectedPart).toEqual(null);
  });

  it('should correctly set restriction keys for actions as user', async () => {
    let {fixture} = await renderPartsDetailComponent({roles: ['user']});
    let {componentInstance} = fixture;

    // subcomponent investigation success
    componentInstance.isAsPlannedPart = false;
    componentInstance.hasChildren = true;
    componentInstance.partOwner = Owner.OWN

    expect(componentInstance.setRestrictionMessageKeyForInvestigationOnSubcomponents()).toEqual("routing.startInvestigation");

    // incident creation success
    componentInstance.isPersistentPart = true;
    expect(componentInstance.setRestrictionMessageKeyForIncidentCreation()).toEqual("routing.createIncident")

    // incident creation - customer part
    componentInstance.partOwner = Owner.CUSTOMER;
    expect(componentInstance.setRestrictionMessageKeyForIncidentCreation()).toEqual("routing.notAuthorizedOwner");

    // publish assets - not admin
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets()).toEqual("routing.unauthorized");


  });

  it('should correctly set restriction keys for actions as admin', async () => {
    let {fixture} = await renderPartsDetailComponent({roles: ['admin']});
    let {componentInstance} = fixture;

    // publish assets success
    componentInstance.partOwner = Owner.OWN;
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets()).toEqual("routing.publishAssets");

    componentInstance.partOwner = Owner.SUPPLIER;
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets()).toEqual("routing.publishAssets")

    // publish assets - not own Part
    componentInstance.partOwner = Owner.CUSTOMER;
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets()).toEqual("routing.publishAssets");

    componentInstance.partOwner = Owner.OWN
    // sucomponent investigation - not as built
    componentInstance.isAsPlannedPart = true;
    expect(componentInstance.setRestrictionMessageKeyForInvestigationOnSubcomponents()).toEqual("routing.notAllowedForAsPlanned")

    // subcomponent investigation - no child parts
    componentInstance.isAsPlannedPart = false;
    componentInstance.hasChildren = false;
    expect(componentInstance.setRestrictionMessageKeyForInvestigationOnSubcomponents()).toEqual("routing.noChildPartsForInvestigation");

    // subcomponent investigation - not user role
    componentInstance.hasChildren = true;
    expect(componentInstance.setRestrictionMessageKeyForInvestigationOnSubcomponents()).toEqual("routing.unauthorized");

    // incident creation - not as built
    componentInstance.isAsPlannedPart = true;
    expect(componentInstance.setRestrictionMessageKeyForIncidentCreation()).toEqual("routing.notAllowedForAsPlanned");

    // incident creation - not persistent part
    componentInstance.isAsPlannedPart = false;
    componentInstance.isPersistentPart = false;
    expect(componentInstance.setRestrictionMessageKeyForIncidentCreation()).toEqual("routing.notAllowedForNonPersistentPart");

    // incident creation - not user role
    componentInstance.isPersistentPart = true;
    expect(componentInstance.setRestrictionMessageKeyForIncidentCreation()).toEqual("routing.unauthorized")

  });

});
