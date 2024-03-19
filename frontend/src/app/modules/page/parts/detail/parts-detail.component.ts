import { Location } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RoleService } from '@core/user/role.service';
import { TractionBatteryCode } from '@page/parts/model/aspectModels.model';
import { Owner } from '@page/parts/model/owner.enum';

import { ImportState, Part, QualityType } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { SelectOption } from '@shared/components/select/select.component';
import { NotificationType } from '@shared/model/notification.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';

import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { filter, tap } from 'rxjs/operators';

@Component({
  selector: 'app-parts-detail',
  templateUrl: './parts-detail.component.html',
  styleUrls: ['./parts-detail.component.scss']
})
export class PartsDetailComponent {
  @Input() showRelation = true;
  @Input() showStartInvestigationOnChildParts = true;

  public  shortenPartDetails$: Observable<View<Part>>;
  public readonly selectedPartDetails$: Observable<View<Part>>;
  public readonly manufacturerDetails$: Observable<View<Part>>;
  public readonly customerOrPartSiteDetails$: Observable<View<Part>>;
  public readonly tractionBatteryDetails$: Observable<View<Part>>;
  public readonly importStateDetails$: Observable<View<Part>>;
  public readonly tractionBatterySubcomponents$: Observable<View<TractionBatteryCode>>;

  public readonly displayedColumns: string[];

  public isAsPlannedPart: boolean = false;
  public isPersistentPart: boolean = false;
  public isOwnPart: boolean = false;
  public hasChildren: boolean = false;

  public investigationOnSubcomponentsTooltipMessage: string;
  public incidentCreationTooltipMessage: string;
  public publishAssetsTooltipMessage: string;


  public customerOrPartSiteDetailsHeader$: Subscription;
  public customerOrPartSiteHeader: string;

  public showQualityTypeDropdown = false;
  public qualityTypeOptions: SelectOption[];

  public qualityTypeControl = new FormControl<QualityType>(null);
  public readonly isOpen$: Observable<boolean>;
  public readonly isPublisherOpen$ = new Subject<boolean>();
  public readonly isNotificationRequestOpen = new BehaviorSubject<boolean>(false);


  private readonly isOpenState: State<boolean> = new State<boolean>(false);


  public currentPartId: string;
  public pageIndexHistory: {AS_BUILT_PAGE: string, AS_PLANNED_PAGE: string}

  constructor(private readonly partDetailsFacade: PartDetailsFacade, private readonly router: Router, private readonly route: ActivatedRoute, public roleService: RoleService, private location: Location) {
    this.isOpen$ = this.isOpenState.observable;
    this.setIsOpen(true);

    this.currentPartId = this.route.snapshot.params['partId'];
    this.partDetailsFacade.setPartById(this.currentPartId);
    this.selectedPartDetails$ = this.partDetailsFacade.selectedPart$;
    this.shortenPartDetails$ = this.partDetailsFacade.selectedPart$;

    this.shortenPartDetails$ = this.partDetailsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForView(),
      tap(({ data }) => {
        this.qualityTypeControl.patchValue(data?.qualityType, { emitEvent: false, onlySelf: true })
      }),
    );

    this.manufacturerDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForManufacturerView());
    this.customerOrPartSiteDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForCustomerOrPartSiteView());
    this.tractionBatteryDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForTractionBatteryCodeDetailsView());
    this.tractionBatterySubcomponents$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForTractionBatteryCodeSubComponentsView()) as unknown as Observable<View<TractionBatteryCode>>;

    this.importStateDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForAssetStateDetailsView());

    this.customerOrPartSiteDetailsHeader$ = this.customerOrPartSiteDetails$?.subscribe(data => {
      if (data?.data?.functionValidFrom) {
        this.customerOrPartSiteHeader = 'partDetail.partSiteInformationData';
      } else {
        this.customerOrPartSiteHeader = 'partDetail.customerData';
      }
    });

    this.qualityTypeOptions = Object.values(QualityType).map(value => ({
      label: value,
      value: value,
    }));

    this.selectedPartDetails$.subscribe(part => {
      const loweredSemanticDataModel = part.data.semanticDataModel.toString().toLowerCase();
      if(part?.data?.semanticDataModel) {
        this.isAsPlannedPart = loweredSemanticDataModel === 'partasplanned' || loweredSemanticDataModel === 'tombstoneasplanned'|| loweredSemanticDataModel === 'tombstoneasbuilt' || loweredSemanticDataModel === 'unknown';
      }

      if(part?.data?.importState === ImportState.PERSISTENT) {
        this.isPersistentPart = true;
      }

      if(part?.data?.owner === Owner.OWN) {
        this.isOwnPart = true;
      }

      if(part?.data?.children?.length > 0 ) {
        this.hasChildren = true;
      }

      this.incidentCreationTooltipMessage = this.setRestrictionMessageKeyForIncidentCreation();
      this.publishAssetsTooltipMessage = this.setRestrictionMessageKeyForPublishAssets();
      this.investigationOnSubcomponentsTooltipMessage = this.setRestrictionMessageKeyForInvestigationOnSubcomponents()


    });

    this.displayedColumns = [ 'position', 'productType', 'tractionBatteryCode' ];
  }

  public ngOnInit(): void {
    this.route.queryParams.subscribe((params: {AS_BUILT_PAGE: string, AS_PLANNED_PAGE: string}) => {
      this.pageIndexHistory = params;
    })
    this.selectedPartDetails$.subscribe(next => console.log(next))
  }

  public ngOnDestroy(): void {
    this.partDetailsFacade.selectedPart = null;
  }

  public ngAfterViewInit(): void {
    this.partDetailsFacade.selectedPart$.pipe(filter(({ data }) => !!data)).subscribe(_ => this.setIsOpen(true));
  }

  public setIsOpen(openState: boolean) {
    this.isOpenState.update(openState);

    if (!openState) {
      this.partDetailsFacade.selectedPart = null;
    }
  }

  public openRelationPage(part: Part): void {
    this.partDetailsFacade.selectedPart = null;
    this.router.navigate([ `parts/relations/${ part.id }` ]).then(_ => window.location.reload());
  }
  // valid investigation for subcomponent:
  // - is supplier part
  // - is as built part
  // - has child parts
  // - role is not admin
  private setRestrictionMessageKeyForInvestigationOnSubcomponents(): string {
    if(this.isAsPlannedPart) {
      return 'routing.notAllowedForAsPlanned';
    }
    else if(!this.hasChildren) {
      return 'routing.noChildPartsForInvestigation';
    }
    else if(this.roleService.isAdmin()) {
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
  private setRestrictionMessageKeyForIncidentCreation(): string {
    if(this.isAsPlannedPart) {
      return 'routing.notAllowedForAsPlanned';
    }
    else if(!this.isPersistentPart) {
      return 'routing.notAllowedForNonPersistentPart';
    }
    else if(this.roleService.isAdmin()) {
      return 'routing.unauthorized';
    } else {
      return 'routing.createIncident';
    }

  }

  // valid publish assets on asset (allowance of state is handled in publish assets view)
  // - part owner is own
  // - is admin role
  private setRestrictionMessageKeyForPublishAssets() {
    if(!this.roleService.isAdmin()) {
      return 'routing.unauthorized';
    }
    if(!this.isOwnPart) {
      return 'routing.onlyAllowedForOwnParts';
    } else {
      return 'routing.publishAssets'
    }

  }


  protected readonly NotificationAction = NotificationAction;
  protected readonly Owner = Owner;

  navigateToParentPath() {
    const parentPath = this.router.routerState.snapshot.url.split('/')[1]; //otherParts
    const navigationExtras = this.pageIndexHistory ? {queryParams: this.pageIndexHistory} : null
    this.router.navigate([parentPath], navigationExtras);
  }


  protected readonly NotificationType = NotificationType;
}
