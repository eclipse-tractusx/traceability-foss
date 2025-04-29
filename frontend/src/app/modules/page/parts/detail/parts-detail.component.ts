import { Location } from '@angular/common';
import { AfterViewInit, Component, Input, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RoleService } from '@core/user/role.service';
import { SharedPartService } from '@page/notifications/detail/edit/shared-part.service';
import { TractionBatteryCode } from '@page/parts/model/aspectModels.model';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Owner } from '@page/parts/model/owner.enum';

import { ImportState, Part, QualityType } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { SelectOption } from '@shared/components/select/select.component';
import { NotificationType } from '@shared/model/notification.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';

import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { EMPTY, Observable, of, Subject, Subscription } from 'rxjs';
import { filter, tap, map, switchMap } from 'rxjs/operators';
import { MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { ToastService } from '@shared/index';
import { PartsFacade } from '../core/parts.facade';

@Component({
  selector: 'app-parts-detail',
  templateUrl: './parts-detail.component.html',
  styleUrls: ['./parts-detail.component.scss'],
})
export class PartsDetailComponent implements OnDestroy, AfterViewInit {
  @Input() showRelation = true;
  @Input() showStartInvestigationOnChildParts = true;

  public breadcrumbData$: Observable<{
    variant: 'As built' | 'As planned';
    partName: string;
    uuid: string;
  }>;
  public shortenPartDetails$: Observable<View<Part>>;
  public selectedPartDetails$: Observable<View<Part>>;
  public manufacturerDetails$: Observable<View<Part>>;
  public customerOrPartSiteDetails$: Observable<View<Part>>;
  public tractionBatteryDetails$: Observable<View<Part>>;
  public recordInformation$: Observable<View<Part>>;
  public tractionBatterySubcomponents$: Observable<View<TractionBatteryCode>>;

  public displayedColumns: string[];

  public isAsPlannedPart: boolean = false;
  public isPersistentPart: boolean = false;
  public partOwner: Owner | undefined = Owner.UNKNOWN;
  public hasChildren: boolean = false;

  public investigationOnSubcomponentsTooltipMessage: string;
  public incidentCreationTooltipMessage: string;
  public publishAssetsTooltipMessage: string;
  public SyncAssetsTooltipMessage: string;

  public customerOrPartSiteDetailsHeader$: Subscription;
  public customerOrPartSiteHeader: string;

  public showQualityTypeDropdown = false;
  public qualityTypeOptions: SelectOption[];

  public qualityTypeControl = new FormControl<QualityType>(null);

  public readonly isPublisherOpen$ = new Subject<boolean>();
  public currentPartId: string;
  public pageIndexHistory: { AS_BUILT_PAGE: string; AS_PLANNED_PAGE: string };
  public isAsBuiltPart: boolean;
  public svgIconName: string;
  protected readonly NotificationAction = NotificationAction;
  protected readonly Owner = Owner;
  protected readonly NotificationType = NotificationType;
  private readonly isStartInvestigationOpen: State<boolean> = new State<boolean>(false);
  public partImportState: ImportState;
  public showDetailTable: boolean = true;

  constructor(
    public readonly partDetailsFacade: PartDetailsFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly partsFacade: PartsFacade,
    public roleService: RoleService,
    private readonly location: Location,
    private readonly sharedPartService: SharedPartService,
    private readonly iconRegistry: MatIconRegistry,
    private readonly sanitizer: DomSanitizer,
    public toastService: ToastService,
  ) {
    this.iconRegistry.addSvgIcon(
      'as-built',
      this.sanitizer.bypassSecurityTrustResourceUrl('/assets/images/as-built.svg'),
    );
    this.iconRegistry.addSvgIcon(
      'as-planned',
      this.sanitizer.bypassSecurityTrustResourceUrl('/assets/images/as-planned.svg'),
    );
    this.route.queryParams.subscribe((params: { AS_BUILT_PAGE: string; AS_PLANNED_PAGE: string }) => {
      this.pageIndexHistory = params;
      this.isAsBuiltPart = params['isAsBuilt'] === 'true';
      this.svgIconName = this.isAsBuiltPart ? 'as-built' : 'as-planned';
      this.renderPartDetails();
    });
  }

  public ngOnDestroy(): void {
    this.partDetailsFacade.selectedPart = null;
  }

  public ngAfterViewInit(): void {
    this.partDetailsFacade.selectedPart$.pipe(filter(({ data }) => !!data)).subscribe(_ => this.setIsOpen(true));
  }

  public setIsOpen(openState: boolean) {
    this.isStartInvestigationOpen.update(openState);

    if (!openState) {
      this.partDetailsFacade.selectedPart = null;
    }
  }

  // valid publish assets on asset (allowance of state is handled in publish assets view)
  // - part owner is own

  public openRelationPage(part: Part): void {
    this.partDetailsFacade.selectedPart = null;
    this.router
      .navigate([`parts/relations/${part.id}`], {
        queryParams: { isAsBuilt: part.mainAspectType === MainAspectType.AS_BUILT },
      })
      .then(_ => window.location.reload());
  }
  // valid investigation for subcomponent:
  // - is supplier part
  // - is as built part
  // - has child parts
  // - role is not admin
  setRestrictionMessageKeyForInvestigationOnSubcomponents(): string {
    if (this.isAsPlannedPart) {
      return 'routing.notAllowedForAsPlanned';
    } else if (!this.hasChildren) {
      return 'routing.noChildPartsForInvestigation';
    } else if (this.roleService.isAdmin()) {
      return 'routing.unauthorized';
    } else {
      return 'routing.startInvestigation';
    }
  }
  // valid create quality incident for part (alert/investigation will be handled in create incident view):
  // - is not as planned part
  // - part owner is own
  // - is persistent
  // - is not admin role
  setRestrictionMessageKeyForIncidentCreation(): string {
    if (this.isAsPlannedPart) {
      return 'routing.notAllowedForAsPlanned';
    }
    if (!this.isPersistentPart) {
      return 'routing.notAllowedForNonPersistentPart';
    }
    if (this.partOwner === Owner.CUSTOMER || this.partOwner === Owner.UNKNOWN) {
      return 'routing.notAuthorizedOwner';
    }
    if (this.roleService.isAdmin()) {
      return 'routing.unauthorized';
    }
    if ( (this.roleService.isUser() ||this.roleService.isSupervisor() ) && this.isAsBuiltPart && this.partOwner === Owner.OWN) {
      return 'routing.createIncident';
    }
    if (this.isAsBuiltPart && this.partOwner === Owner.SUPPLIER) {
      return 'routing.requestInvestigation';
    }
    return 'routing.noActionAvailable';
  }


  // valid publish assets on asset (allowance of state is handled in publish assets view)
  // - part owner is own and in state of transient
  // - is admin role
  setRestrictionMessageKeyForPublishAssets(): string {
    if (!this.roleService.isAdmin()) {
      return 'routing.unauthorized';
    }
    if (this.partOwner == Owner.OWN && this.partImportState == ImportState.TRANSIENT) {
      return 'routing.publishAssets';
    }
    return 'routing.onlyAllowedForOwnParts';
  }

  navigateToParentPath() {
    this.location.back();
  }

  navigateToNotificationCreationView() {
    this.router.navigate(['inbox/create'], {
      queryParams: { initialType: this.partOwner === Owner.OWN ? 'Alert' : 'Investigation' },
    });
    this.sharedPartService.affectedParts = [this.partDetailsFacade.selectedPart];
  }

  private renderPartDetails() {
    this.currentPartId = this.route.snapshot.params['partId'];
    this.partDetailsFacade.setPartById(this.currentPartId, this.isAsBuiltPart);
    this.selectedPartDetails$ = this.partDetailsFacade.selectedPart$;
    this.breadcrumbData$ = this.selectedPartDetails$.pipe(
      map(part => ({
        variant: this.isAsBuiltPart ? 'As built' : 'As planned',
        partName: part?.data?.nameAtManufacturer || 'Unknown Part',
        uuid: part?.data?.id || '',
      })),
    );
    this.shortenPartDetails$ = this.partDetailsFacade.selectedPart$;

    this.shortenPartDetails$ = this.partDetailsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForView(),
      tap(({ data }) => {
        this.qualityTypeControl.patchValue(data?.qualityType, { emitEvent: false, onlySelf: true });
      }),
    );

    this.manufacturerDetails$ = this.partDetailsFacade.selectedPart$.pipe(
      switchMap(part => {
        return this.isAsBuiltPart
          ? of(part).pipe(PartsAssembler.mapPartForManufacturerView())
          : EMPTY; // or of(null)
      })
    );

    this.customerOrPartSiteDetails$ = this.partDetailsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForCustomerOrPartSiteView(),
    );
    this.tractionBatteryDetails$ = this.partDetailsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForTractionBatteryCodeDetailsView(),
    );
    this.tractionBatterySubcomponents$ = this.partDetailsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForTractionBatteryCodeSubComponentsView(),
    ) as unknown as Observable<View<TractionBatteryCode>>;
    this.recordInformation$ = this.partDetailsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForAssetStateDetailsView(),
    );

    this.customerOrPartSiteDetailsHeader$ = this.customerOrPartSiteDetails$?.subscribe(data => {
      if (data?.data?.functionValidFrom) {
        this.customerOrPartSiteHeader = 'partDetail.manufacturerInformation';
      } else {
        this.customerOrPartSiteHeader = 'partDetail.customerData';
      }
    });

    this.qualityTypeOptions = Object.values(QualityType).map(value => ({
      label: value,
      value: value,
    }));

    this.selectedPartDetails$
      .pipe(filter(part => !!part?.data))
      .subscribe(part => {
        const loweredSemanticDataModel = part?.data?.semanticDataModel?.toString()?.toLowerCase();
        if (part?.data?.semanticDataModel) {
          this.isAsPlannedPart =
            loweredSemanticDataModel === 'partasplanned' ||
            loweredSemanticDataModel === 'tombstoneasplanned' ||
            loweredSemanticDataModel === 'tombstoneasbuilt' ||
            loweredSemanticDataModel === 'unknown';
        }
        this.partImportState = part?.data?.importState;

        if (part?.data?.importState === ImportState.PERSISTENT) {
          this.isPersistentPart = true;
        }

        this.partOwner = part?.data?.owner;

        if (part?.data?.children?.length > 0) {
          this.hasChildren = true;
        }
        this.incidentCreationTooltipMessage = this.setRestrictionMessageKeyForIncidentCreation();
        this.SyncAssetsTooltipMessage = this.setRestrictionMessageKeyForSyncAssets();
        this.publishAssetsTooltipMessage = this.setRestrictionMessageKeyForPublishAssets();
        this.investigationOnSubcomponentsTooltipMessage = this.setRestrictionMessageKeyForInvestigationOnSubcomponents();
        this.partDetailsFacade.mainAspectType = part?.data?.mainAspectType;
      });

    this.displayedColumns = ['position', 'productType', 'tractionBatteryCode'];
  }

  getManufacturerName(details: { key: string; value: string }[]): string {
    return details?.find(item => item.key === 'nameAtManufacturer')?.value ?? '';
  }

  public shouldShowPublishButton(): boolean {
    return this.roleService.isAdmin() && this.partImportState === ImportState.TRANSIENT;
  }

  public shouldShowIncidentButton(): boolean {
    return (this.roleService.isSupervisor() || this.roleService.isUser) && this.partImportState === ImportState.PERSISTENT && !this.isIncidentActionAllowed();
  }

  public setRestrictionMessageKeyForSyncAssets(): string | null {
    if (!this.roleService.isAdmin() || this.partOwner != Owner.OWN) return null;
    switch (this.partImportState) {
      case ImportState.TRANSIENT:
        return 'routing.publishPart';

      case ImportState.PERSISTENT:
      case ImportState.ERROR:
      case ImportState.PUBLISHED_TO_CORE_SERVICES:
        return 'routing.syncSelectedParts';
      case ImportState.IN_SYNCHRONIZATION:
        return 'routing.synchronizePartInProgress';

      default:
        return null;
    }
  }
  public shouldShowSyncButton(): boolean {
    return (
      this.SyncAssetsTooltipMessage === 'routing.syncSelectedParts' ||
      this.SyncAssetsTooltipMessage === 'routing.synchronizePartInProgress'
    );
  }

  public isSyncButtonDisabled(): boolean {
    return this.SyncAssetsTooltipMessage === 'routing.synchronizePartInProgress';
  }

  private syncPartsAsBuilt(assetIds: string[]) {
    this.partsFacade.syncPartsAsBuilt(assetIds).subscribe({
      next: data => {
        this.toastService.success('partSynchronization.success');
      },
      error: error => {
        this.toastService.error(error?.getMessage());
      },
    });
  }

  public shouldRenderRelatedParts(view: View<Part>): boolean {
    const children = view?.data?.children?.length || 0;
    const parents = view?.data?.parents?.length || 0;
    return children + parents > 0;
  }

  private syncPartsAsPlanned(assetIds: string[]) {
    this.partsFacade.syncPartsAsPlanned(assetIds).subscribe({
      next: data => {
        this.toastService.success('partSynchronization.success');
      },
      error: error => {
        this.toastService.error(error?.getMessage());
      },
    });
  }
  public syncSelectedPart() {
    if (this.isAsBuiltPart) this.syncPartsAsBuilt([this.currentPartId]);
    else {
      this.syncPartsAsPlanned([this.currentPartId]);
    }
  }
  public isIncidentActionAllowed(): boolean {
    return !(['routing.createIncident', 'routing.requestInvestigation'].includes(this.incidentCreationTooltipMessage));
  }
}
