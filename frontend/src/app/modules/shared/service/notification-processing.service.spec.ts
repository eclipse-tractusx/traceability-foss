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
import { TestBed } from '@angular/core/testing';
import { NotificationProcessingService } from '@shared/service/notification-processing.service';

describe('NotificationProcessingService', () => {
  let service: NotificationProcessingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NotificationProcessingService],
    });

    service = TestBed.inject(NotificationProcessingService);
  });

  it('should set notificationIdsInLoadingState and call onNotificationIdsInLoadingStateRemoval', () => {
    spyOn(service as any, 'onNotificationIdsInLoadingStateRemoval').and.callThrough();

    const newIds = new Set(['id1', 'id2']);

    service.notificationIdsInLoadingState = newIds;

    expect(service.notificationIdsInLoadingState).toEqual(newIds);
    expect((service as any).onNotificationIdsInLoadingStateRemoval).toHaveBeenCalled();
  });

  it('should emit doneEmit when notificationIdsInLoadingState is set', () => {
    spyOn(service.doneEmit, 'emit');

    const newIds = new Set(['id1', 'id2']);

    service.notificationIdsInLoadingState = newIds;

    expect(service.doneEmit.emit).toHaveBeenCalled();
  });
});

