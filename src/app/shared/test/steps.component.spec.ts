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

import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { StepsComponent } from '../components/wizard/steps/steps.component';

describe('StepsComponent', () => {
  let component: StepsComponent;
  let fixture: ComponentFixture<StepsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
      declarations: [StepsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepsComponent);
    component = fixture.componentInstance;
    component.steps$ = of([
      { stepIndex: 1, isComplete: false, isActive: true, label: 'Investigation Details', isOptional: false },
      { stepIndex: 2, isComplete: false, isActive: false, label: 'Affected Parts', isOptional: true },
      { stepIndex: 3, isComplete: false, isActive: false, label: 'Organizations', isOptional: true },
      { stepIndex: 4, isComplete: false, isActive: false, label: 'Summary', isOptional: true },
    ]);
    component.currentStep$ = of({
      stepIndex: 1,
      isComplete: false,
      isActive: true,
      label: 'Investigation Details',
      isOptional: false,
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
