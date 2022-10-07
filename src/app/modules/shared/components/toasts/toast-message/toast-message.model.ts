/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

/**
 * Types or status of toast messages
 * Success status - shows the toast with a green status bar, typically used to inform the user
 * warning status - shows the toast with a yellow status bar, typically used to alert the user
 * error status - shows the toast with a red status bar, typically used for error messages
 * informative status - shows the toast with a blue status bar, typically used for informative messages
 */

export const enum ToastStatus {
  Success = 'success',
  Warning = 'warning',
  Error = 'error',
  Informative = 'informative',
}

export class ToastMessage {
  public isSliderON = true;

  constructor(public id: number, public message: string, public status: ToastStatus | null, public timeout: number) {}
}
