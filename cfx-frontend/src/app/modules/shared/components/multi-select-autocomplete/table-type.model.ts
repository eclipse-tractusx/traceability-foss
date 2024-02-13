/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

export enum TableType {
  AS_BUILT_OWN = 'AS_BUILT_OWN',
  AS_PLANNED_OWN = 'AS_PLANNED_OWN',
  AS_BUILT_SUPPLIER = 'AS_BUILT_SUPPLIER',
  AS_BUILT_CUSTOMER = 'AS_BUILT_CUSTOMER',
  AS_PLANNED_SUPPLIER = 'AS_PLANNED_SUPPLIER',
  AS_PLANNED_CUSTOMER = 'AS_PLANNED_CUSTOMER',
  AS_DESIGNED_OWN = 'AS_DESIGNED_OWN',
  AS_ORDERED_OWN = 'AS_ORDERED_OWN',
  AS_SUPPORTED_OWN = 'AS_SUPPORTED_OWN',
  AS_RECYCLED_OWN = 'AS_RECYCLED_OWN',
  AS_DESIGNED_SUPPLIER = 'AS_DESIGNED_SUPPLIER',
  AS_DESIGNED_CUSTOMER = 'AS_DESIGNED_CUSTOMER',
  AS_ORDERED_SUPPLIER = 'AS_ORDERED_SUPPLIER',
  AS_ORDERED_CUSTOMER = 'AS_ORDERED_CUSTOMER',
  AS_SUPPORTED_SUPPLIER = 'AS_SUPPORTED_SUPPLIER',
  AS_SUPPORTED_CUSTOMER = 'AS_SUPPORTED_CUSTOMER',
  AS_RECYCLED_SUPPLIER = 'AS_RECYCLED_SUPPLIER',
  AS_RECYCLED_CUSTOMER = 'AS_RECYCLED_CUSTOMER',
  RECEIVED_INVESTIGATION = 'RECEIVED_INVESTIGATION',
  CREATED_INVESTIGATION = 'CREATED_INVESTIGATION',
  RECEIVED_ALERT = 'RECEIVED_ALERT',
  CREATED_ALERT = 'CREATED_ALERT',
}

export enum NotificationChannel {
  SENDER = 'SENDER',
  RECEIVER = 'RECEIVER'
}
