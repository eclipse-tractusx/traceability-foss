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

import { Given, Then } from '@badeball/cypress-cucumber-preprocessor';
import { DashboardPage } from '../../integration/pages/DashboardPage';

Given(/^user logged in as "supervisor"$/, () => {
  // Simulate the login process here, including entering credentials and submitting the form
  cy.visit('https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/auth?client_id=Cl17-CX-Part&redirect_uri=https%3A%2F%2Ftraceability-portal-e2e-a.dev.demo.catena-x.net%2Fdashboard&state=c8729ce7-d710-4139-869a-1154c91814eb&response_mode=fragment&response_type=code&scope=openid&nonce=ee585303-ad03-4772-816f-4937a99917cb&code_challenge=3Qd7kufqOMAetJWnXxSELb25IxLTZXu5QYYEbxVIM7c&code_challenge_method=S256'); // Visit the login page
  cy.get('input[placeholder="Enter your company name"]').type('CX-Test-Access');
  cy.get('a').click();
  cy.get('input#username').type('notrealuser');
  cy.get('input#password').type('notrealpassword');
  cy.get('form#kc-form-login').submit();
});
Given(/^browser is opened to dashboard page$/, () => {
  DashboardPage.visit();
});

Then(/^url should contain dashboard$/, () => {
  cy.url().should('include', '/dashboard');
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
