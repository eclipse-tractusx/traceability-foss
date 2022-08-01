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

import { TranslationObject } from '@shared/pipes/i18n.pipe';
import { NotificationStatus } from './notification-status';
import { NotificationText } from './notification-text';

export class NotificationMessage {
  public id: number;
  public isSliderON: boolean;
  public message: NotificationText | string | TranslationObject;
  public status: NotificationStatus;
  public timeout: number;

  constructor(
    id: number,
    message: NotificationText | string | TranslationObject,
    status: NotificationStatus | null,
    timeout: number,
  ) {
    this.id = id;
    this.message = message;
    this.status = status;
    this.isSliderON = true;
    this.timeout = timeout;
  }
}
