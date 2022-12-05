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


import { RegistryLookupStatus, RegistryProcessResponse } from '@page/admin/core/admin.model';

export const buildMockRegistryProcesses = (): RegistryProcessResponse[] =>
  new Array(25).fill(null).map((_, index) => {
    const status = Object.keys(RegistryLookupStatus) as RegistryLookupStatus[];
    const registryLookupStatus = status[index % 3];

    const failedCount = registryLookupStatus === RegistryLookupStatus.SUCCESSFUL ? 0 : Math.floor(Math.random() * 100);
    const successCount = registryLookupStatus === RegistryLookupStatus.ERROR ? 0 : Math.floor(Math.random() * 100);

    return {
      registryLookupStatus,
      startDate: `2022-05-${(index + 1).toString().padStart(2, '0')}T12:34:12`,
      endDate: `2022-06-${(index + 1).toString().padStart(2, '0')}T12:34:12`,

      successShellDescriptorsFetchCount: failedCount,
      failedShellDescriptorsFetchCount: successCount,
      shellDescriptorsFetchDelta: failedCount + successCount,
    };
  });
