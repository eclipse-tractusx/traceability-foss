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

import type { InvestigationResponse } from '@shared/model/investigations.model';

export const buildMockInvestigations = (statuses: string[]): InvestigationResponse[] =>
  new Array(25).fill(null).map((_, index) => {
    const status = statuses[index % statuses.length];
    return {
      id: `id-${index + 1}`,
      description: `Investigation No ${index + 1}`,
      createDate: `2022-05-${(index + 1).toString().padStart(2, '0')}T12:34:12`,
      status,
    };
  });
