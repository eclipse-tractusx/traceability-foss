import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  HostListener,
  Inject,
  Input,
  Output,
  QueryList,
  ViewChildren,
} from '@angular/core';
import { Notification } from '@shared/model/notification.model';
import { BehaviorSubject } from 'rxjs/internal/BehaviorSubject';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { Part } from '@page/parts/model/parts.model';
import { ModalComponent } from '@shared/modules/modal/component/modal.component';
import { FormGroup, FormControl } from '@angular/forms';
import { SupplierPartsComponent } from '@page/other-parts/presentation/supplier-parts/supplier-parts.component';
import { OwnPartsComponent } from '@page/parts/presentation/own-parts/own-parts.component';
import { Subject } from 'rxjs';
import { RequestContext } from '../request-notification.base';

@Component({
  selector: 'app-forward-notification',
  templateUrl: './forward-notification.component.html',
  styleUrls: ['./forward-notification.component.scss']
})
export class ForwardNotificationComponent {
  public totalItems: number;
  public context: RequestContext;
  public tabIndex = 0;
  public readonly isLoading$ = new BehaviorSubject(false);
  public itemCount: number[] = [];

  public searchFormGroup = new FormGroup({});
  public searchControl: FormControl;

  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();
  public isOpen = false;
  public forwardedNotification: Notification;

  @ViewChildren(SupplierPartsComponent) supplierPartsComponents: QueryList<SupplierPartsComponent>;
  @ViewChildren(OwnPartsComponent) ownPartsComponents: QueryList<OwnPartsComponent>;
  @Input() public selectedParts: Part[] = [];
  @Output() deselectPart = new EventEmitter<Part>();

  protected readonly MainAspectType = MainAspectType;
  protected readonly RequestContext = RequestContext;

  private fromExternal = false;

  constructor(public dialog: MatDialog, @Inject(MAT_DIALOG_DATA) data,
              private readonly changeDetector: ChangeDetectorRef,
  ) {
    this.context = data.context;
    this.forwardedNotification = data.forwardedNotification;
    this.selectedParts = data.selectedItems ?? [];
    this.fromExternal = data.fromExternal ?? false;
    this.isOpen = true;
  }

  ngOnInit(): void {
    this.searchFormGroup.addControl('partSearch', new FormControl([]));
    this.searchControl = this.searchFormGroup.get('partSearch') as unknown as FormControl;
  }

  public closeAction(): void {
    this.isOpen = false;

    if (this.fromExternal) {
      this.dialog.closeAll();
      return;
    }

    const contextTag = this.context === RequestContext.REQUEST_INVESTIGATION ? 'commonInvestigation' : 'commonAlert';

    this.dialog.open(ModalComponent, {
      autoFocus: false,
      data: {
        title: `${contextTag}.discardHeader`,
        message: `${contextTag}.discardMessage`,
        buttonLeft: 'parts.confirmationDialog.resume',
        buttonRight: 'parts.confirmationDialog.discard',
        primaryButtonColour: 'primary',
        onConfirm: () => {
          this.dialog.closeAll();
        }
      }
    });
  }

  public onPartsSelected(parts: Part[]): void {
    this.selectedParts = parts;
  }

  public onPartDeselected(part: Part): void {
    this.deselectPart.emit(part);

    if (this.fromExternal === false) {
      this.deselectPartTrigger$.next([part]);
    }
  }

  public triggerPartSearch() {
    const searchValue = this.searchFormGroup.get('partSearch').value;

    for (const supplierPartsComponent of this.supplierPartsComponents) {
      supplierPartsComponent.updateSupplierParts(searchValue);
    }

    for (const ownPartsComponent of this.ownPartsComponents) {
      ownPartsComponent.updateOwnParts(searchValue);
    }
  }

  public onTotalItemsChanged(totalItems: number): void {
    this.totalItems = totalItems;
    this.changeDetector.detectChanges();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('cdk-overlay-backdrop') &&
      (event.target as HTMLElement).classList.contains('cdk-overlay-dark-backdrop') &&
      (event.target as HTMLElement).classList.contains('cdk-overlay-backdrop-showing')) {
      this.closeAction();
    }
  }

  @HostListener('document:keydown.escape', ['$event'])
  onEscKey(): void {
    this.closeAction();
  }

}
