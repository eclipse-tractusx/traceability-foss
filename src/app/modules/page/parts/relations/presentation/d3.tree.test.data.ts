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

import { TreeStructure } from '@page/parts/relations/model/relations.model';

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
