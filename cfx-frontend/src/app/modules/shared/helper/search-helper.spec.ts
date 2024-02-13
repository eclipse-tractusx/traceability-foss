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

// import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
// import { QueryList } from "@angular/core";
// import { SearchHelper } from './search-helper';
// import { NotificationComponent } from '@shared/modules/notification/presentation/notification.component';


// describe('search helper functions', () => {
//   it('should reset multiSelectAutocompleteComponents for Assets', () => {
//     // Arrange
//     const searchHelper = new SearchHelper();
//     const mockQueryList = <T>(items: T[]): QueryList<T> => {
//       const queryList = new QueryList<T>();
//       queryList.reset(items);
//       return queryList;
//     };

//     const multiSelectAutoCompleteComponents = [
//       {
//         clickClear: jasmine.createSpy('clickClear'),
//       }]
//     const queryListMultiSelect = mockQueryList(multiSelectAutoCompleteComponents);

//     const partsTableComponents: PartsTableComponent[] = [
//       {
//         // @ts-ignore
//         multiSelectAutocompleteComponents: queryListMultiSelect,
//         // @ts-ignore
//         resetFilterActive: jasmine.createSpy('resetFilterActive'),
//       },
//     ];

//     const partsTableComponentsQueryList = mockQueryList(partsTableComponents);

//     // Act
//     searchHelper.resetFilterAndShowToast(true, partsTableComponentsQueryList, undefined);
//     //resetFilterForAssetComponents(partsTableComponentsQueryList, oneFilterSet);

//     // Assert
//     partsTableComponents.forEach((partsTableComponent) => {
//       partsTableComponent.multiSelectAutocompleteComponents.forEach((multiSelectAutocompleteComponent) => {
//         expect(multiSelectAutocompleteComponent.clickClear).toHaveBeenCalledWith(true);
//       });
//       expect(partsTableComponent.resetFilterActive).toHaveBeenCalled();
//     });
//   });


//   it('should reset multiSelectAutocompleteComponents for alerts and investigations', () => {
//     // Arrange

//     const searchHelper = new SearchHelper();
//     const mockQueryList = <T>(items: T[]): QueryList<T> => {
//       const queryList = new QueryList<T>();
//       queryList.reset(items);
//       return queryList;
//     };

//     const multiSelectAutoCompleteComponents = [
//       {
//         clickClear: jasmine.createSpy('clickClear'),
//       }]

//     const queryListMultiSelect = mockQueryList(multiSelectAutoCompleteComponents);

//     const tableComponent = {
//       resetFilter: jasmine.createSpy('resetFilter'),
//       multiSelectAutocompleteComponents: queryListMultiSelect,
//     }
//     const notificationTabComponents = [
//       {
//         onFilterChange: jasmine.createSpy('onFilterChange'),
//         tableComponent
//       }]
//     const queryListNotificationTab = mockQueryList(notificationTabComponents);

//     const notificationComponent: NotificationComponent = {
//       //@ts-ignore
//       notificationTabComponents: queryListNotificationTab,
//     };

//     // Act
//     searchHelper.resetFilterAndShowToast(false, notificationComponent, undefined);
//     //resetFilterForNotificationComponents(notificationComponent, oneFilterSet);

//     // Assert
//     notificationComponent.notificationTabComponents.forEach((notificationTabComponent) => {
//       expect(notificationTabComponent.onFilterChange).toHaveBeenCalled();
//       expect(notificationTabComponent.tableComponent.resetFilter).toHaveBeenCalled();
//       notificationTabComponent.tableComponent.multiSelectAutocompleteComponents.forEach((multiSelectAutocompleteComponent) => {
//         expect(multiSelectAutocompleteComponent.clickClear).toHaveBeenCalledWith(true);
//       });
//     });
//   });
// });
