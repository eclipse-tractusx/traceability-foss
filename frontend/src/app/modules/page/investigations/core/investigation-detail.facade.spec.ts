// /********************************************************************************
//  * Copyright (c) 2023 Contributors to the Eclipse Foundation
//  *
//  * See the NOTICE file(s) distributed with this work for additional
//  * information regarding copyright ownership.
//  *
//  * This program and the accompanying materials are made available under the
//  * terms of the Apache License, Version 2.0 which is available at
//  * https://www.apache.org/licenses/LICENSE-2.0.
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
//  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
//  * License for the specific language governing permissions and limitations
//  * under the License.
//  *
//  * SPDX-License-Identifier: Apache-2.0
//  ********************************************************************************/
// import { TitleCasePipe } from '@angular/common';
// import { HttpClientTestingModule } from '@angular/common/http/testing';
// import { TestBed } from '@angular/core/testing';
// import { InvestigationDetailFacade } from '@page/investigations/core/investigation-detail.facade';
// import { InvestigationDetailState } from '@page/investigations/core/investigation-detail.state';
// import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
// import { Part } from '@page/parts/model/parts.model';
// import { PartsAssembler } from '@shared/assembler/parts.assembler';
// import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
// import { PartsService } from '@shared/service/parts.service';
// import { KeycloakService } from 'keycloak-angular';
// import { MOCK_part_1 } from '../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';

// describe('InvestigationDetailFacade', () => {
//   let investigationDetailFacade: InvestigationDetailFacade;
//   let investigationDetailState: InvestigationDetailState;
//   let partService: PartsService;

//   beforeEach(() => {

//     TestBed.configureTestingModule({
//       imports: [
//         HttpClientTestingModule,
//       ],
//       providers: [
//         KeycloakService,
//         PartsService,
//         TitleCasePipe,
//         InvestigationDetailFacade,
//         InvestigationDetailState,
//         FormatPartlistSemanticDataModelToCamelCasePipe,
//       ],
//     });

//     investigationDetailFacade = TestBed.inject(InvestigationDetailFacade);

//     investigationDetailState = TestBed.inject(InvestigationDetailState);

//     partService = TestBed.inject(PartsService);
//   });

//   [
//     {
//       method: 'sortNotificationParts',
//       prop: 'investigationPartsInformation' as any,
//     },
//     {
//       method: 'sortSupplierParts',
//       prop: 'supplierPartsInformation' as any,
//     },
//   ].forEach(object => {

//     describe(`${ object.method }()`, () => {

//       let part: Part;

//       beforeEach(function() {

//         part = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);

//         this.spy = spyOn(partService, 'sortParts').and.callFake(() => [ part ]);
//       });

//       [ [ part ], null, undefined ].forEach((fallacy, index) => {

//         it('should pass sortParts', function() {

//           spyOnProperty(investigationDetailState, object.prop, 'get').and.returnValue({
//             data: fallacy,
//           });

//           investigationDetailFacade[object.method]('', '');

//           index == 0
//             ? (() => {
//               expect(this.spy).toHaveBeenCalled();
//               expect(this.spy).toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(String), jasmine.any(String));
//             })()
//             : (() => {
//               expect(this.spy).not.toHaveBeenCalled();
//               expect(this.spy).not.toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(String), jasmine.any(String));
//             })();
//         });

//         it('should set part infos after sort', function() {

//           spyOnProperty(investigationDetailState, object.prop, 'get').and.returnValue({
//             data: fallacy,
//           });

//           this.spyPropSet = spyOnProperty(investigationDetailState, object.prop, 'set');

//           investigationDetailFacade[object.method]('', '');

//           index == 0
//             ? (() => {
//               expect(this.spyPropSet).toHaveBeenCalledTimes(1);
//             })()
//             : (() => {
//               expect(this.spyPropSet).not.toHaveBeenCalledTimes(1);
//             })();
//         });
//       });
//     });
//   });
// });
