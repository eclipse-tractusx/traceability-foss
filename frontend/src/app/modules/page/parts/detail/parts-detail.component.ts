import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {RoleService} from '@core/user/role.service';
import {TractionBatteryCode} from '@page/parts/model/aspectModels.model';
import {Owner} from '@page/parts/model/owner.enum';

import {Part, QualityType} from '@page/parts/model/parts.model';
import {PartsAssembler} from '@shared/assembler/parts.assembler';
import {SelectOption} from '@shared/components/select/select.component';
import {State} from '@shared/model/state';
import {View} from '@shared/model/view.model';
import {NotificationAction} from '@shared/modules/notification/notification-action.enum';

import {PartDetailsFacade} from '@shared/modules/part-details/core/partDetails.facade';
import {Observable, Subscription} from 'rxjs';
import {filter, tap} from 'rxjs/operators';
import {Location} from "@angular/common";

@Component({
  selector: 'app-parts-detail',
  templateUrl: './parts-detail.component.html',
  styleUrls: ['./parts-detail.component.scss']
})
export class PartsDetailComponent {
  @Input() showRelation = true;
  @Input() showStartInvestigation = true;

  public  shortenPartDetails$: Observable<View<Part>>;
  public readonly selectedPartDetails$: Observable<View<Part>>;
  public readonly manufacturerDetails$: Observable<View<Part>>;
  public readonly customerOrPartSiteDetails$: Observable<View<Part>>;
  public readonly tractionBatteryDetails$: Observable<View<Part>>;
  public readonly importStateDetails$: Observable<View<Part>>;
  public readonly tractionBatterySubcomponents$: Observable<View<TractionBatteryCode>>;

  public readonly displayedColumns: string[];

  public isAsPlannedPart: boolean = false;

  public customerOrPartSiteDetailsHeader$: Subscription;
  public customerOrPartSiteHeader: string;

  public showQualityTypeDropdown = false;
  public qualityTypeOptions: SelectOption[];

  public qualityTypeControl = new FormControl<QualityType>(null);
  public readonly isOpen$: Observable<boolean>;

  private readonly isOpenState: State<boolean> = new State<boolean>(false);

  public authorizationTooltipMessage: string;
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

      if(part?.data?.semanticDataModel) {
        this.isAsPlannedPart = part.data.semanticDataModel.toString() === 'PartAsPlanned';
      }

      if(part?.data?.children?.length > 0 ) {
        this.authorizationTooltipMessage = this.getRestrictionMessageKey(true);
      } else {
        this.authorizationTooltipMessage = this.getRestrictionMessageKey(false);
      }
    });

    this.displayedColumns = [ 'position', 'productType', 'tractionBatteryCode' ];
  }

  public ngOnInit(): void {
    this.route.queryParams.subscribe((params: {AS_BUILT_PAGE: string, AS_PLANNED_PAGE: string}) => {
      this.pageIndexHistory = params;
      console.log(this.pageIndexHistory)
    })
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

  getRestrictionMessageKey(hasChildren: boolean): string {
    if(this.isAsPlannedPart) {
      return 'routing.notAllowedForAsPlanned';
    }
    else if(!hasChildren) {
      return 'routing.noChildPartsForInvestigation';
    }
    else if(this.roleService.isAdmin()) {
      return 'routing.unauthorized';
    } else {
      return null;
    }

  }

  protected readonly NotificationAction = NotificationAction;
  protected readonly Owner = Owner;

  navigateToPartsView() {
    const navigationExtras = this.pageIndexHistory ? {queryParams: this.pageIndexHistory} : null
    this.router.navigate(['parts'], navigationExtras);
  }
}
