/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { BpnConfig, BpnConfigFormGroup } from '@page/admin/core/admin.model';
import { AdminFacade } from '@page/admin/core/admin.facade';
import { BehaviorSubject, combineLatest, Observable, of, Subject, Subscription, tap } from 'rxjs';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import { BpnConfigEntry, ChangedInformation } from './bpn-configuration.model';
import { SaveBpnConfigModal } from '@page/admin/presentation/bpn-configuration/save-modal/save-modal.component';
import { takeUntil } from 'rxjs/operators';

export const bpnRegex = /^BPN[ALS][0-9A-Za-z]{10}[0-9A-Za-z]{2}$/;

@Component({
  selector: 'app-bpn-configuration',
  templateUrl: './bpn-configuration.component.html',
  styleUrls: [ './bpn-configuration.component.scss' ],
})
export class BpnConfigurationComponent implements OnInit, OnDestroy {
  @ViewChild(SaveBpnConfigModal) private saveBpnConfigModal: SaveBpnConfigModal;

  public readonly listOfHiddenElements$ = new BehaviorSubject([]);
  public readonly changeInformation$ = new BehaviorSubject<ChangedInformation>({ changed: [], deleted: [], added: [] });
  public readonly searchControl = new FormControl('');

  public readonly editBpnFormGroup: BpnConfigFormGroup;
  public readonly newBpnFormGroup: BpnConfigFormGroup;

  public newEntryGroup: FormGroup<BpnConfigEntry>;

  private readonly listOfItemsToDelete: string[] = [];
  private readonly subscription = new Subscription();
  private readonly resetPageTrigger$ = new Subject();

  private originalValue: BpnConfig[];

  constructor(formBuilder: FormBuilder, private readonly adminFacade: AdminFacade) {
    this.editBpnFormGroup = formBuilder.group({ bpnConfig: formBuilder.array([]) }) as BpnConfigFormGroup;
    this.newBpnFormGroup = formBuilder.group({ bpnConfig: formBuilder.array([]) }) as BpnConfigFormGroup;
  }

  public ngOnInit(): void {
    this.initSearchListener();
    this.initBpnConfiguration();
  }

  public ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  public get editBpnConfig(): FormArray<FormGroup<BpnConfigEntry>> {
    return this.editBpnFormGroup.get('bpnConfig') as FormArray<FormGroup<BpnConfigEntry>>;
  }

  public get newBpnConfig(): FormArray<FormGroup<BpnConfigEntry>> {
    return this.newBpnFormGroup.get('bpnConfig') as FormArray<FormGroup<BpnConfigEntry>>;
  }

  public onBpnDelete(formGroupIndex: number, bpnConfig: FormGroup<BpnConfigEntry>): void {
    const bpn = bpnConfig.getRawValue().bpn;
    this.listOfItemsToDelete.push(bpn);

    setTimeout(() => {
      this.editBpnConfig.removeAt(formGroupIndex);
      this.listOfItemsToDelete.splice(this.listOfItemsToDelete.indexOf(bpn), 1);
      // Animation time is 0.3seconds
    }, 300);
  }

  public addEntry(): void {
    this.newEntryGroup.markAllAsTouched();
    this.newEntryGroup.updateValueAndValidity();

    if (this.newEntryGroup.invalid) {
      return;
    }

    const bpnConfig = this.newEntryGroup.getRawValue();
    const added = [ ...this.changeInformation$.value.added, bpnConfig ];
    this.changeInformation$.next({ ...this.changeInformation$.value, added });

    this.newBpnConfig.push(BpnConfigurationComponent.createBpnConfigFormGroup(bpnConfig));
    this.newEntryGroup.reset();
  }

  public onBpnRemove(formGroupIndex: number): void {
    this.newBpnConfig.removeAt(formGroupIndex);
    this.changeInformation$.next({ ...this.changeInformation$.value, added: this.newBpnConfig.getRawValue() });
  }

  public getStylingClass(bpnConfig: FormGroup<BpnConfigEntry>): Record<string, boolean> {
    const changes = this.changeInformation$.value;

    if (changes.changed.find(({ bpn }) => bpn === bpnConfig.getRawValue().bpn)) return { 'bpn-config__edit': true };
    if (changes.added.find(({ bpn }) => bpn === bpnConfig.getRawValue().bpn)) return { 'bpn-config__new': true };
  }

  public isEditField(bpnConfig: FormGroup<BpnConfigEntry>): boolean {
    const changes = this.changeInformation$.value;
    return !!changes.changed.find(({ bpn }) => bpn === bpnConfig.getRawValue().bpn);
  }

  public resetField(bpnConfig: FormGroup<BpnConfigEntry>, formGroupIndex: number): void {
    const originalEntry = this.originalValue.find(entry => entry.bpn === bpnConfig.value.bpn);
    this.editBpnConfig.at(formGroupIndex).reset(originalEntry);
  }

  public saveData(): void {
    this.editBpnFormGroup.markAllAsTouched();
    this.editBpnFormGroup.updateValueAndValidity({ emitEvent: false });

    this.newBpnFormGroup.markAllAsTouched();
    this.newBpnFormGroup.updateValueAndValidity();

    if (this.editBpnFormGroup.invalid || this.newBpnFormGroup.invalid)
      return BpnConfigurationComponent.scrollToFirstError();

    this.saveBpnConfigModal.show(this.changeInformation$.value, this.originalValue);
  }

  public updateCall({ changed, added, deleted }: ChangedInformation): Observable<any> {
    const addCall = added.length ? this.adminFacade.createBpnFallbackConfig(added) : of(null);
    const updateCall = changed.length ? this.adminFacade.updateBpnFallbackConfig(changed) : of(null);
    const deleteCalls = deleted.length
      ? deleted.map(({ bpn }) => this.adminFacade.deleteBpnFallbackConfig(bpn))
      : [ of(null) ];

    return combineLatest([ addCall, updateCall, ...deleteCalls ]).pipe(
      tap(_ => {
        this.resetPageTrigger$.next(null);
        while (this.newBpnConfig.length !== 0) this.newBpnConfig.removeAt(0, { emitEvent: false });
        while (this.editBpnConfig.length !== 0) this.editBpnConfig.removeAt(0, { emitEvent: false });
        this.changeInformation$.next({ changed: [], deleted: [], added: [] });
        this.initBpnConfiguration();
      }),
    );
  }

  public isBeingDeleted(bpnConfig: FormGroup<BpnConfigEntry>): boolean {
    return this.listOfItemsToDelete.includes(bpnConfig.getRawValue().bpn);
  }

  private static createBpnConfigFormGroup(bpnConfig?: BpnConfig): FormGroup<BpnConfigEntry> {
    const { bpn = '', url = '' } = bpnConfig || {};

    const urlRegex = new RegExp(
      '^(https?:\\/\\/)?' + // protocol
      '((?:[a-z\\d](?:[a-z\\d-]*[a-z\\d])?\\.)+[a-z]{2,}|' + // domain name
      '(?:\\d{1,3}\\.){3}\\d{1,3})' + // OR ip (v4) address
      '(?::\\d+)?' + // port
      '(?:\\/[-a-z\\d%_.~+]*)*' + // path
      '(?:\\?[;&a-z\\d%_.~+=-]*)?' + // query string
      '(?:#[-a-z\\d_]*)?$', // fragment locator
      'i',
    );

    return new FormGroup({
      bpn: new FormControl(bpn, [ Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn') ]),
      url: new FormControl(url, [ Validators.required, BaseInputHelper.getCustomPatternValidator(urlRegex, 'url') ]),
    });
  }

  private static scrollToFirstError(): void {
    const errorElements = document.getElementsByClassName('mat-form-field-invalid');
    const options: ScrollIntoViewOptions = { behavior: 'smooth', block: 'center' };
    errorElements[0]?.scrollIntoView(options);
  }

  private initEditListener(): void {
    this.editBpnFormGroup.valueChanges.pipe(takeUntil(this.resetPageTrigger$)).subscribe(({ bpnConfig }) => {
      const deleted = [];
      const changed = [];

      this.originalValue.forEach((originalEntry: BpnConfig) => {
        const index = bpnConfig.findIndex(config => config.bpn === originalEntry.bpn);
        const currenConfig = bpnConfig[index];

        if (!currenConfig) return deleted.push(originalEntry);
        else if (currenConfig.url !== originalEntry.url) changed.push(currenConfig);
        bpnConfig.splice(index, 1);
      });

      this.changeInformation$.next({
        deleted,
        changed,
        added: [ ...bpnConfig, ...this.newBpnConfig.getRawValue() ],
      });
    });
  }

  private initSearchListener(): void {
    const sub = this.searchControl.valueChanges.subscribe(searchValue => {
      const search = searchValue.toLowerCase();
      const listOfItems = [ ...this.editBpnConfig.getRawValue(), ...this.newBpnConfig.getRawValue() ];

      const itemsToExclude = listOfItems
        .map(bpnConfig => {
          const list = Object.values(bpnConfig).filter((data: string) => data.toLowerCase().indexOf(search) >= 0);
          return !searchValue || !!list.length ? null : bpnConfig.bpn;
        })
        .filter(data => data !== null);

      this.listOfHiddenElements$.next(itemsToExclude);
    });

    this.subscription.add(sub);
  }

  private initBpnConfiguration(): void {
    this.adminFacade.readBpnFallbackConfig().subscribe({
      next: bpnConfigList => {
        bpnConfigList.forEach(bpnConfig =>
          this.editBpnConfig.push(BpnConfigurationComponent.createBpnConfigFormGroup(bpnConfig)),
        );

        this.originalValue = this.editBpnConfig.getRawValue();
        if (!this.newEntryGroup) {
          this.newEntryGroup = BpnConfigurationComponent.createBpnConfigFormGroup();
        } else {
          this.newEntryGroup.reset();
        }
        this.initEditListener();
      },
    });
  }
}
