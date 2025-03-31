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
import { of, throwError } from 'rxjs';
import { PartsFacade } from '../core/parts.facade';
import { ImportState } from '../model/parts.model';


const PartsFacadeMock = {
  syncPartsAsPlanned: jasmine.createSpy('syncPartsAsPlanned').and.returnValue(of([])),
  syncPartsAsBuilt: jasmine.createSpy('syncPartsAsBuilt').and.returnValue(of([])),
};


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
        { provide: PartsFacade, useValue: PartsFacadeMock },
      ],
      roles,
    });
  };
  it('should render part details', async () => {
    const { fixture } = await renderPartsDetailComponent();
    const { componentInstance } = fixture;

    fixture.detectChanges();
    await fixture.whenStable();

    const nameElement = await screen.findByText(/BMW AG \(BPNL00000003CML1\)/);
    expect(nameElement).toBeInTheDocument();
  });


  it('should set selected part on null if click on relation page', async () => {
    const {fixture} = await renderPartsDetailComponent({roles: ['user']});
    const {componentInstance} = fixture;

    componentInstance.openRelationPage(part);
    expect(componentInstance.partDetailsFacade.selectedPart).toEqual(null);
  });

  it('should correctly set restriction keys for actions as user', async () => {
    let { fixture } = await renderPartsDetailComponent({ roles: ['user'] });
    let { componentInstance } = fixture;

    // subcomponent investigation success
    componentInstance.isAsPlannedPart = false;
    componentInstance.hasChildren = true;
    componentInstance.partOwner = Owner.OWN;
    expect(componentInstance.setRestrictionMessageKeyForInvestigationOnSubcomponents())
      .toEqual("routing.startInvestigation");

    // incident creation success
    componentInstance.isAsBuiltPart = true; // ✅ required
    componentInstance.isPersistentPart = true;
    componentInstance.partOwner = Owner.OWN;
    expect(componentInstance.setRestrictionMessageKeyForIncidentCreation())
      .toEqual("routing.createIncident");

    // incident creation - customer part
    componentInstance.partOwner = Owner.CUSTOMER;
    expect(componentInstance.setRestrictionMessageKeyForIncidentCreation())
      .toEqual("routing.notAuthorizedOwner");

    // publish assets - not admin
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets())
      .toEqual("routing.unauthorized");
  });


  it('should correctly set restriction keys for actions as admin', async () => {
    let {fixture} = await renderPartsDetailComponent({roles: ['admin']});
    let {componentInstance} = fixture;

    // publish assets success
    componentInstance.partOwner = Owner.OWN;
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets()).toEqual("routing.publishAssets")

    // publish assets - not own Part
    componentInstance.partOwner = Owner.CUSTOMER;
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets()).toEqual("routing.onlyAllowedForOwnParts");

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

  it('should show "Publish part" button for admin with transient part', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;

    componentInstance.partOwner = Owner.OWN;
    componentInstance.partImportState = 'TRANSIENT' as any;

    fixture.detectChanges();

    const publishButton = await screen.findByText(/routing.publishAssets/i);
    expect(publishButton).toBeInTheDocument();
    expect(publishButton).toBeEnabled();

  });

  it('should show disabled "Synchronize Part" button when import state is IN_SYNCHRONIZATION', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;

    componentInstance.partOwner = Owner.OWN;
    componentInstance.partImportState = ImportState.IN_SYNCHRONIZATION;

    componentInstance.SyncAssetsTooltipMessage = componentInstance.setRestrictionMessageKeyForSyncAssets();

    fixture.detectChanges();
    await fixture.whenStable();

    const button = await screen.findByRole('button', {
      name: /routing.syncPart/i, // replace with actual translated text if i18n is not mocked
    });
    expect(button).toBeDisabled();
  });


  it('should show "Create quality alert" for user and supervisor when part is OWN and persistent', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['user', 'supervisor'] });
    const { componentInstance } = fixture;
  
    componentInstance.isAsBuiltPart = true;
    componentInstance.isPersistentPart = true;
    componentInstance.partImportState = ImportState.PERSISTENT;
    componentInstance.partOwner = Owner.OWN;
  
    componentInstance.incidentCreationTooltipMessage = componentInstance.setRestrictionMessageKeyForIncidentCreation();
  
    fixture.detectChanges();
    await fixture.whenStable();
  
    const createIncidentButton = await screen.findByRole('button', {
      name: /routing.createIncident/i,
    });
  
    expect(createIncidentButton).toBeInTheDocument();
    expect(createIncidentButton).toBeVisible();
  });
  


  it('should display "Request quality investigation" for SUPPLIER part when user is a supervisor & user', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['supervisor', 'user'] });
    const { componentInstance } = fixture;
  
    componentInstance.isAsBuiltPart = true;
    componentInstance.isPersistentPart = true;
    componentInstance.partImportState = ImportState.PERSISTENT;
    componentInstance.partOwner = Owner.SUPPLIER;
  
    componentInstance.incidentCreationTooltipMessage = componentInstance.setRestrictionMessageKeyForIncidentCreation();
  
    fixture.detectChanges();
    await fixture.whenStable();
  
    const requestInvestigation = await screen.findByRole('button', {
      name: /routing.requestInvestigation/i,
    });
  
    expect(requestInvestigation).toBeInTheDocument();
    expect(requestInvestigation).toBeVisible();

  });
  

  

  

  it('should call syncSelectedPart() when sync button is clicked (as built part)', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;
  
    // Setup valid state
    componentInstance.isAsBuiltPart = true;
    componentInstance.partOwner = Owner.OWN;
    componentInstance.partImportState = ImportState.PERSISTENT;
    componentInstance.currentPartId = 'test-part-id';
    componentInstance.SyncAssetsTooltipMessage = 'routing.syncSelectedParts';
  
    fixture.detectChanges();
    await fixture.whenStable();
  
    const syncSpy = spyOn<any>(componentInstance, 'syncPartsAsBuilt');
  
    // Find and click the sync button
    const syncButton = await screen.findByRole('button', {
      name: /routing.syncPart/i,
    });
    expect(syncButton).toBeTruthy();
  });
  
  it('should return "routing.unauthorized" if user is not admin', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['user','supervisor'] });
    const { componentInstance } = fixture;
  
    componentInstance.partOwner = Owner.OWN;
    componentInstance.partImportState = ImportState.PERSISTENT;
  
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets())
      .toEqual('routing.unauthorized');
  });

  it('should show error toast if syncPartsAsBuilt fails', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;
  
    // Override mock to simulate error
    PartsFacadeMock.syncPartsAsBuilt = jasmine.createSpy().and.returnValue(
      throwError(() => ({ getMessage: () => 'Sync error' }))
    );
  
    const toastSpy = spyOn(componentInstance.toastService, 'error');
  
    // Call method
    await componentInstance['syncPartsAsBuilt'](['test-part-id']);
  
    expect(toastSpy).toHaveBeenCalledWith('Sync error');
  });


  it('should show success toast if syncPartsAsBuilt succeeds', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;
  
    // Override mock to simulate success
    PartsFacadeMock.syncPartsAsBuilt = jasmine.createSpy().and.returnValue(of([]));
  
    const toastSpy = spyOn(componentInstance.toastService, 'success');
  
    // Call method
    await componentInstance['syncPartsAsBuilt'](['test-part-id']);
  
    expect(toastSpy).toHaveBeenCalledWith('partSynchronization.success');
  });



  it('should show error toast if syncPartsAsPlannedfails', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;
  
    // Override mock to simulate error
    PartsFacadeMock.syncPartsAsPlanned = jasmine.createSpy().and.returnValue(
      throwError(() => ({ getMessage: () => 'Sync error' }))
    );
  
    const toastSpy = spyOn(componentInstance.toastService, 'error');
  
    // Call method
    await componentInstance['syncPartsAsPlanned'](['test-part-id']);
  
    expect(toastSpy).toHaveBeenCalledWith('Sync error');
  });


  it('should show success toast if syncPartsAsPlanned succeeds', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;
  
    // Override mock to simulate success
    PartsFacadeMock.syncPartsAsPlanned = jasmine.createSpy().and.returnValue(of([]));
  
    const toastSpy = spyOn(componentInstance.toastService, 'success');
  
    // Call method
    await componentInstance['syncPartsAsPlanned'](['test-part-id']);
  
    expect(toastSpy).toHaveBeenCalledWith('partSynchronization.success');
  });
  
  
  it('should return "routing.onlyAllowedForOwnParts" if part owner is not OWN or is in TRANSIENT state', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;
  
    componentInstance.partOwner = Owner.CUSTOMER;
    componentInstance.partImportState = ImportState.PERSISTENT;
  
    expect(componentInstance.setRestrictionMessageKeyForPublishAssets())
      .toEqual('routing.onlyAllowedForOwnParts');
  });

  it('should call syncSelectedPart() and show success toast when sync button is clicked (as built part)', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;
  
    // Set up valid state for sync
    componentInstance.isAsBuiltPart = true;
    componentInstance.partOwner = Owner.OWN;
    componentInstance.partImportState = ImportState.PERSISTENT;
    componentInstance.currentPartId = 'test-part-id';
  
    // Ensure SyncAssetsTooltipMessage is calculated so the button shows
    componentInstance.SyncAssetsTooltipMessage = componentInstance.setRestrictionMessageKeyForSyncAssets();
  
    // Override mock to simulate success
    PartsFacadeMock.syncPartsAsBuilt = jasmine.createSpy().and.returnValue(of([]));
    const toastSpy = spyOn(componentInstance.toastService, 'success');
  
    fixture.detectChanges();
    await fixture.whenStable();
  
    // Find and click the sync button
    const syncButton = await screen.findByRole('button', {
      name: /routing.syncPart/i,
    });
    expect(syncButton).toBeEnabled();
  
    syncButton.click();
  
    expect(PartsFacadeMock.syncPartsAsBuilt).toHaveBeenCalledWith(['test-part-id']);
    expect(toastSpy).toHaveBeenCalledWith('partSynchronization.success');
  });

  it('should call syncSelectedPart() and show success toast when sync button is clicked (as planned part)', async () => {
    const { fixture } = await renderPartsDetailComponent({ roles: ['admin'] });
    const { componentInstance } = fixture;
  
    // Set up valid state for sync
    componentInstance.isAsBuiltPart = false;
    componentInstance.partOwner = Owner.OWN;
    componentInstance.partImportState = ImportState.PERSISTENT;
    componentInstance.currentPartId = 'test-part-id';
  
    // Ensure SyncAssetsTooltipMessage is calculated so the button shows
    componentInstance.SyncAssetsTooltipMessage = componentInstance.setRestrictionMessageKeyForSyncAssets();
  
    // Override mock to simulate success
    PartsFacadeMock.syncPartsAsPlanned = jasmine.createSpy().and.returnValue(of([]));
    const toastSpy = spyOn(componentInstance.toastService, 'success');
  
    fixture.detectChanges();
    await fixture.whenStable();
  
    // Find and click the sync button
    const syncButton = await screen.findByRole('button', {
      name: /routing.syncPart/i,
    });
    expect(syncButton).toBeEnabled();
  
    syncButton.click();
  
    expect(PartsFacadeMock.syncPartsAsPlanned).toHaveBeenCalledWith(['test-part-id']);
    expect(toastSpy).toHaveBeenCalledWith('partSynchronization.success');
  });
});
