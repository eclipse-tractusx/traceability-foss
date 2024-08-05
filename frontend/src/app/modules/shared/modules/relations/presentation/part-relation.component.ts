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

import { Location } from '@angular/common';
import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Part } from '@page/parts/model/parts.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subscription } from 'rxjs';
import { delay, switchMap, takeWhile, tap } from 'rxjs/operators';

@Component({
  selector: 'app-part-relation',
  templateUrl: './part-relation.component.html',
  styleUrls: [ './part-relation.component.scss' ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  host: {
    'class.app-part-relation-host': 'isStandalone',
  },
})
export class PartRelationComponent implements OnInit, OnDestroy {
  @Input() isStandalone = true;
  @Input() showMiniMap = true;
  @Input() isAsBuilt = true;

  public readonly htmlIdBase = 'app-part-relation-';
  public readonly subscriptions = new Subscription();
  public readonly rootPart$: Observable<View<Part>>;
  public readonly resizeTrigger$: Observable<number>;
  public readonly htmlId: string;
  private _rootPart$ = new State<View<Part>>({ loader: true });
  public readonly _resizeTrigger$ = new BehaviorSubject<number>(0);

  constructor(
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly route: ActivatedRoute,
    private readonly cdr: ChangeDetectorRef,
    private readonly location: Location,
    staticIdService: StaticIdService,
  ) {
    this.resizeTrigger$ = this._resizeTrigger$.pipe(delay(0));
    this.rootPart$ = this._rootPart$.observable;
    this.htmlId = staticIdService.generateId(this.htmlIdBase);
  }

  public ngOnInit(): void {
    const initSubscription = this.route.paramMap
      .pipe(
        switchMap(params => {
          if (this.partDetailsFacade.selectedPart) return this.partDetailsFacade.selectedPart$;

          const partId = params.get('partId');
          return partId ? this.partDetailsFacade.getRootPart(partId, this.isAsBuilt) : this.partDetailsFacade.selectedPart$;
        }),
        tap(viewData => this._rootPart$.update(viewData)),
        takeWhile(({ data }) => !data, true),
      )
      .subscribe();
    this.subscriptions.add(initSubscription);
  }

  public ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  public increaseSize(): void {
    this._resizeTrigger$.next(0.1);
  }

  public decreaseSize(): void {
    this._resizeTrigger$.next(-0.1);
  }

  navigateBack() {
    this.location.back();
  }
}
