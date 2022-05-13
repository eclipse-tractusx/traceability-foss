/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Component, ContentChild, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { StepActionsComponent } from './step-actions.component';
import { StepBodyComponent } from './step-body.component';
import { Step } from './step.model';
import { WizardFacade } from './wizard.facade';

@Component({
  selector: 'app-wizard',
  templateUrl: './wizard.component.html',
  styleUrls: ['./wizard.component.scss'],
})
export class WizardComponent implements OnInit {
  @ContentChild(StepBodyComponent) bodyComponent: StepBodyComponent;
  @ContentChild(StepActionsComponent) actionsComponent: StepActionsComponent;

  @Input() steps: Step[];

  public steps$: Observable<Step[]>;
  public currentStep$: Observable<Step>;

  constructor(private wizardFacade: WizardFacade) {
    this.steps$ = this.wizardFacade.steps$;
    this.currentStep$ = this.wizardFacade.currentStep$;
  }

  ngOnInit(): void {
    this.wizardFacade.setSteps(this.steps);
    this.wizardFacade.setCurrentStep(this.steps[0]);
  }
}
