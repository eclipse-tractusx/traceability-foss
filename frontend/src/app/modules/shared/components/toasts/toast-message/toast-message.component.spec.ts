/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ToastService } from '@shared/components/toasts/toast.service';
import { ToastMessageComponent } from './toast-message.component';
import { ToastMessage, ToastStatus } from './toast-message.model';

describe('ToastMessageComponent', () => {
  let component: ToastMessageComponent;
  let fixture: ComponentFixture<ToastMessageComponent>;
  let toastService: jasmine.SpyObj<ToastService>;

  beforeEach(async () => {
    const toastServiceSpy = jasmine.createSpyObj('ToastService', ['emitClick']);

    await TestBed.configureTestingModule({
      declarations: [ToastMessageComponent],
      providers: [{ provide: ToastService, useValue: toastServiceSpy }]
    })
      .compileComponents();

    toastService = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ToastMessageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call toastService.emitClick() when handleClick is called', () => {
    // Arrange
    component.toastMessage = new ToastMessage(1, 'test', ToastStatus.Informative, 500);
    let event = new MouseEvent("");
    // Act
    component.handleClick(event);

    // Assert
    expect(toastService.emitClick).toHaveBeenCalled();
  });
});
