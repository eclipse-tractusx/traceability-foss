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

import { When } from '@badeball/cypress-cucumber-preprocessor';
import { AdminPage } from '../../integration/pages/AdminPage';


When("user navigate to {string}", function(desiredMenu) {
    switch (desiredMenu) {
      case 'Parts': {
        cy.get('[href="/parts"]').click();
        break;
      }
      case 'Dashboard': {
        cy.get('[href="/dashboard"]').click();
        break;
      }
      case 'Quality alerts': {
        cy.get('[href="/inbox"]').click();
        break;
      }
      case 'About': {
        cy.get('[href="/about"]').click();
        break;
      }
      case 'Administration': {
        AdminPage.visit();
        break;
      }
      default: {
        throw new Error("Set header menu '" + desiredMenu + "' is not one of valid status [Dashboard, Parts, Quality investigations, Quality alerts, About, Administration].");
      }
    }
});


When("user change language to {string}", function(language) {
cy.get('[data-testid="user-menu"]').click();
      switch (language) {
        case 'german': {
          cy.get('button').contains('DE').click();
          break;
        }
        case 'english': {
          cy.get('button').contains('EN').click();
          break;
        }
        default: {
          throw new Error("Set language '" + language + "' is not one of valid language [german, english].");
          break;
        }
      }
});
