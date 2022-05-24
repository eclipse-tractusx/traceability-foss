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
import { PartsResponse } from '@page/parts/model/parts.model';

export const mockAssets: PartsResponse = {
  data: [
    {
      id: '1',
      name: 'Audi A1 Sportback',
      manufacturer: 'Audi',
      partNumber: '11111',
      serialNumber: '5XXGM4A77CG032209',
      qualityType: 'high',
      productionDate: '1997-05-30',
      productionCountry: 'DE',
    },
    {
      id: '2',
      name: 'BMW 520d Touring',
      manufacturer: 'BMW',
      partNumber: '22222',
      serialNumber: '3N1CE2CPXFL392065',
      qualityType: 'high',
      productionDate: '2020-10-23',
      productionCountry: 'AT',
    },
    {
      id: '3',
      name: 'A 180 Limousine',
      manufacturer: 'Mercedes-Benz',
      partNumber: '33333',
      serialNumber: 'JF1ZNAA12E8706066',
      qualityType: 'high',
      productionDate: '1990-01-13',
      productionCountry: 'DE',
    },
  ],
};
