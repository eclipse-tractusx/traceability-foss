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

import type { InvestigationResponse } from '@page/investigations/model/investigations.model';

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
