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

import { Component, ContentChild, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Step } from './step.model';
import { StepBodyComponent } from './step-body.component';
import { WizardFacade } from './wizard.facade';
import { StepActionsComponent } from './step-actions.component';

/**
 *
 *
 * @export
 * @class WizardComponent
 * @implements {OnInit}
 */
@Component({
  selector: 'app-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss'],
})
export class WizardComponent implements OnInit {
  /**
   * Step body component template
   *
   * @type {StepBodyComponent}
   * @memberof WizardComponent
   */
  @ContentChild(StepBodyComponent) bodyComponent: StepBodyComponent;

  /**
   * Step actions template
   *
   * @type {StepActionsComponent}
   * @memberof WizardComponent
   */
  @ContentChild(StepActionsComponent) actionsComponent: StepActionsComponent;

  /**
   * Wizard steps
   *
   * @type {Step[]}
   * @memberof WizardComponent
   */
  @Input() steps: Step[];

  /**
   * Steps state
   *
   * @type {Observable<Step[]>}
   * @memberof WizardComponent
   */
  public steps$: Observable<Step[]>;

  /**
   * Current step state
   *
   * @type {Observable<Step>}
   * @memberof WizardComponent
   */
  public currentStep$: Observable<Step>;

  /**
   * @constructor WizardComponent
   * @param {WizardFacade} wizardFacade
   * @memberof WizardComponent
   */
  constructor(private wizardFacade: WizardFacade) {
    this.steps$ = this.wizardFacade.steps$;
    this.currentStep$ = this.wizardFacade.currentStep$;
  }

  /**
   * Angular lifecycle method - Ng on init
   *
   * @memberof WizardComponent
   */
  ngOnInit(): void {
    this.wizardFacade.setSteps(this.steps);
    this.wizardFacade.setCurrentStep(this.steps[0]);
  }
}
