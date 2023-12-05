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

import { Given, Then, When } from '@badeball/cypress-cucumber-preprocessor';
import { DashboardPage } from '../../integration/pages/DashboardPage';


Given(/^browser is opened to dashboard page$/, () => {

  cy.url().then((url) => {
    const newOrigin = new URL(url).origin;

    // Set the new origin for subsequent tests
    Cypress.config('baseUrl', newOrigin);

    // You can also visit a specific path on the new origin
    DashboardPage.visit();
  });
});

Then(/^url should contain dashboard$/, () => {
  cy.url().should('include', '');
});

Then(/^should be visible "Dashboard" header$/, () => {
  cy.get('h3').contains('Dashboard').should('be.visible');
});

Then(/^should be visible "TOTAL OF PARTS" section$/, () => {
  cy.get('section').contains('Total of parts').should('be.visible');
});

Then(/^should be visible "TOTAL OF OTHER PARTS" section$/, () => {
  cy.get('section').contains('Total of other parts').should('be.visible');
});

Then(/^should be visible "TOTAL OF OPEN INVESTIGATIONS" section$/, () => {
  cy.get('section').contains('Total of open investigations').should('be.visible');
});

Then(/^should be visible "Quality Investigations" section$/, () => {
  cy.get('section').contains('Quality Investigations').should('be.visible');
});

Then(
  /^in "Quality Investigations" section should be able to click on "View all" button and go to integrations page$/,
  () => {
    DashboardPage.clickQualityInvestigationViewAllButton();
    cy.url().should('include', '/investigations');
  },
);
