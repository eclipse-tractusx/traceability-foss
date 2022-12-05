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

import { TreeStructure } from '@shared/modules/relations/model/relations.model';

export const D3TreeDummyData: TreeStructure = {
  id: 'urn:uuid:62386a1e-d65f-4c9c-a8fb-b5312eb0c45f',
  text: 'Sensor',
  title: 'Sensor | 26137H6-30',
  state: 'done',
  children: [
    {
      id: 'urn:uuid:5951b68e-6e5b-4135-87a3-dda42a5c08d3',
      text: 'Small',
      title: 'Engineering Plastics | 32027H2-54',
      state: 'done',
      children: [],
      relations: null,
    },
    {
      id: 'urn:uuid:5951b68e-6e5b-4135-87a3-xyx11x1y11x1',
      text: 'Long text that needs to be shortened',
      title: 'Engineering Plastics | 32027H2-54',
      state: 'loading',
      children: null,
      relations: null,
    },
  ],
  relations: [
    {
      id: 'urn:uuid:5951b68e-6e5b-4135-87a3-dda42a5c08d3',
      title: 'urn:uuid:5951b68e-6e5b-4135-87a3-dda42a5c08d3',
      state: 'loading',
      children: null,
    },
    {
      id: 'urn:uuid:5951b68e-6e5b-4135-87a3-xyx11x1y11x1',
      title: 'urn:uuid:5951b68e-6e5b-4135-87a3-xyx11x1y11x1',
      state: 'loading',
      children: null,
    },
  ],
};
