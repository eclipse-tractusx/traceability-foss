/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Step } from 'src/app/shared/components/wizard/step.model';
import { WizardFacade } from '../wizard.facade';

/**
 *
 *
 * @export
 * @class StepsComponent
 * @implements {OnInit}
 */
@Component({
  selector: 'app-steps',
  templateUrl: './steps.component.html',
  styleUrls: ['./steps.component.scss'],
})
export class StepsComponent {
  /**
   * Steps state
   *
   * @type {Observable<Step[]>}
   * @memberof StepsComponent
   */
  public steps$: Observable<Step[]>;

  /**
   * Current step state
   *
   * @type {Observable<Step>}
   * @memberof StepsComponent
   */
  public currentStep$: Observable<Step>;

  /**
   * @constructor StepsComponent.
   * @param {WizardFacade} wizardFacade
   * @memberof StepsComponent
   */
  constructor(private wizardFacade: WizardFacade) {
    this.steps$ = this.wizardFacade.steps$;
    this.currentStep$ = this.wizardFacade.currentStep$;
  }
}
