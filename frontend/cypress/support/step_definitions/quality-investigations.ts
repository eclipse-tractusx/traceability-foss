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
import { When, Then, Given } from '@badeball/cypress-cucumber-preprocessor';
import { QualityInvestigationsPage } from '../../integration/pages/QualityInvestigationsPage';

let notificationDescription = null;

Then("select other part with semantic-model-id {string}", (semanticModelId) => {
//since IDs of desired asset are not shown in FE the selection has to be done by other number.
  cy.get('span').contains('As Planned').click(); // This has to be done to avoid asPlanned selection
  //cy.get('span').contains('NO-989134870198932317923938').closest('tr').find('.mdc-checkbox').click();
  cy.get('span').contains(semanticModelId).closest('tr').find('.mdc-checkbox').click();
});


Then("start investigation creation with description {string}", function (description) {
  const date = new Date().getTime();
  notificationDescription = description + "_" + date;
  cy.get('div').contains('Start investigation').click();
  cy.get('mat-label').contains('Description').click().type(notificationDescription);
});


When("severity {string}", function (severity) {
  cy.get('#mat-select-56').click(); // First the dropdown has to be opened.
  cy.get('p').contains(severity).click();
});


When("{string} deadline", function (deadline) {
      if (deadline == 'no') {
        // do nothing
      } else {
       // ---TBD--- implement timepicker once it´s necessary.
      }
});


When("request the investigation", () => {
  cy.get('span').contains('ADD TO QUEUE').click();
});


Then("selected parts are marked as investigated", () => {
  //cy.get('class').contains('highlighted');
  //---TBD--- to check the desired assets, have to be adjusted with desired asset selection
});


When("popup with information about queued investigation is shown", () => {
  cy.contains(/You queued an investigation for 1 part/i).should('be.visible');
});


When("user navigate to {string} with button in popup", (popupClick) => {
  cy.get('a').contains('Go to Queue').click();
});


When("open details of created investigation", () => {
  cy.get('[data-testid="table-menu-button"]').first().click(); //the first investigation will be opened
  cy.get('[data-testid="table-menu-button--actions.viewDetails"]').first().click();
});


// --- TBD --- check id, description, status, created, createdby, text
// --- TBD --- #check: popup on the right sight is shown
When("user confirm cancelation of selected investigation with entering {string} id", (input) => {
  let investigationId = '';
  switch (input) {
    case 'no': {
      cy.get('span').contains('Confirm cancellation').click();
      break;
    }
    case 'wrong': {
      cy.get('#mat-mdc-form-field-label-4').click().focus().type('000');
      cy.get('span').contains('Confirm cancellation').click();
      break;
    }
    case 'correct': {
      cy.get('mat-label').invoke('text').as('cancelId');
      cy.get('@cancelId').then((cancelId) => {
        cy.get('#mat-input-0').click().type(cancelId);
      });
      cy.get('span').contains('Confirm cancellation').click();
      break;
    }
    default: {
      throw new Error("Set cancelation id '" + input + "' is not one of valid actions [no, wrong, correct].");
      break;
    }
  }
});


Then("cancelation is not possible due to {string} id", (id) => {
  switch (id) {
    case 'no': {
      cy.contains(/This field is required!/i).should('be.visible');
      break;
    }
    case 'wrong': {
      cy.contains(/Please enter data that matches this pattern:/i).should('be.visible');
      break;
    }
    default: {
      throw new Error("Set cancelation action '" + id + "' is not one of valid actions [no, wrong].");
      break;
    }
  }
});


When("user {string} selected investigation", (action) => {
//within opened detail view of quality investigation
  switch (action) {
      case 'approve': {
        cy.get('div').contains('Approve').click();
        break;
      }
      case 'cancel': {
        cy.get('div').contains('Cancel').click();
        break;
      }
      case 'close': {
        cy.get('div').contains('Close').click();
        break;
      }
      case 'acknowledge': {
        cy.get('div').contains('Acknowledge').click();
        break;
      }
      case 'accept': {
        cy.get('div').contains('Accept').click();
        break;
      }
      case 'decline': {
        cy.get('div').contains('Decline').click();
        break;
      }
      default: {
        throw new Error("Set action '" + action + "' is not one of valid actions [approve, cancel, close, acknowledge, accept, decline].");
        break;
      }
  }
});


When("user confirm approval of selected investigation", (action) => {
  cy.get('app-confirm').find('span').contains('Approve').click();
});


Then("informations for selected investigation are displayed as expected", () => {
// --- TBD --- include: overview, supplier parts, STATUS
});


Then("selected {string} has been {string} as expected", (notificationType, expectedStatus) => {
matched = false;
    switch (expectedStatus) {
      case 'canceled': {
        matched = true;
        cy.get('[title="Cancelled"]').should('be.visible');
        break;
      }
      case 'approved': {
      // same as "requested"
      matched = true;
        cy.get('[title="Requested"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      case 'accepted': {
      matched = true;
        cy.get('[title="Accepted"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      case 'declined': {
      matched = true;
        cy.get('[title="Declined"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      case 'acknowledged': {
      matched = true;
        cy.get('[title="Acknowledged"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      case 'closed': {
      matched = true;
        cy.get('[title="Closed"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      default: {
        throw new Error("Set expected status '" + expectedStatus + "' is not one of valid status [canceled, approved, accepted, declined, acknowledged, closed].");
        break;
      }
    }
});


When("selected {string} is not allowed to be {string}", (notificationType, status) => {
matched = false;
    switch (status) {
      case 'canceled': {
        matched = true;
        cy.get('div').contains('/^Cancel$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'approved': {
      matched = true;
        cy.get('div').contains('/^Approve$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'accepted': {
      matched = true;
        cy.get('div').contains('/^Accept$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'declined': {
      matched = true;
        cy.get('div').contains('/^Decline$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'acknowledged': {
      matched = true;
        cy.get('div').contains('/^Acknowledge$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'closed': {
      matched = true;
        cy.get('div').contains('/^Close$/', {matchCase: true}).should('not.exist');
        break;
      }
      default: {
        throw new Error("Set status '" + status + "' is not one of valid status [canceled, approved, accepted, declined, acknowledged, closed].");
        break;
      }
    }
});
