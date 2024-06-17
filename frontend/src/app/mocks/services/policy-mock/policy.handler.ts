/********************************************************************************
 * Copyright (c) 2022, 2023, 2024 Contributors to the Eclipse Foundation
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
import { environment } from '@env';
import { rest } from 'msw';
import { getPolicies, getPolicyById } from './policy.model';

export const policyHandler = (_ => {
  return [
    rest.get(`*${ environment.apiUrl }/policies`, (req, res, ctx) => {
      return res(ctx.status(200), ctx.json(getPolicies()));
    }),

    rest.post(`*${ environment.apiUrl }/policies`, (req, res, ctx) => {
      return res(ctx.status(201), ctx.json('success'));
    }),

    rest.get(`*${ environment.apiUrl }/policies/:policyId`, (req, res, ctx) => {
      const { policyId } = req.params;
      const policy = getPolicyById(policyId);
      return res(ctx.status(200), ctx.json(policy));
    }),

    rest.put(`*${ environment.apiUrl }/policies`, (req, res, ctx) => {
      return res(ctx.status(200), ctx.json('success'));
    }),

    rest.delete(`*${ environment.apiUrl }/policies/:policyId`, (req, res, ctx) => {
      return res(ctx.status(200), ctx.json('success'));
    }),

  ]
})();
