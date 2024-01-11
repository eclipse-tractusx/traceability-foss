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
let desiredSemanticModelId = null;
let desiredId = null;

Then("select other part with semantic-model-id {string}", function(semanticModelId) {
//since IDs of desired asset are not shown in FE the selection has to be done by other number.
  desiredSemanticModelId = semanticModelId;
  cy.get('span').contains(semanticModelId).closest('tr').find('.mdc-checkbox').click();
});

Then("select part with id {string}", function(id) {
  desiredId = id;
  cy.get('span').contains(id).closest('tr').find('.mdc-checkbox').click();
});


Then("start {string} creation with description {string}", function(notificationType, description) {
  const date = new Date().getTime();
  notificationDescription = description + "_" + date;
  switch (notificationType) {
    case 'investigation': {
      cy.get('div').contains('Start investigation').click();
      break;
    }
    case 'alert': {
      cy.get('div').contains('Create alert').click();
      break;
    }
    default: {
      throw new Error("Set notification type '" + notificationType + "' is not one of valid types [investigation, alert].");
      break;
    }
  }
  cy.get('mat-label').contains(/^Description$/i).click().type(notificationDescription);
});


When("receiver BPN {string}", function(receiverBPN) {
  cy.get('mat-label').contains(/^BPN$/i).click().type(receiverBPN);
});


When("severity {string}", function(severity) {
  cy.get('div').contains('Severity').click(); // First the dropdown has to be opened.
  cy.get('p').contains(severity).click();
});


When("{string} deadline", function(deadline) {
      if (deadline == 'no') {
        // do nothing
      } else {
       // ---TBD--- implement timepicker once it´s necessary.
      }
});


When("request the {string}", function(notificationType) {
  cy.get('span').contains('ADD TO QUEUE').click();
});


Then("selected parts are marked as {string}", function(notificationType) {
  switch (notificationType) {
    case 'investigated': {
      cy.get('span').contains(desiredSemanticModelId).closest('tr').should('have.class', "highlighted");
      break;
    }
    case 'alerted': {
      cy.get('span').contains(desiredId).closest('tr').should('have.class', "highlighted");
      break;
    }
    default: {
      throw new Error("Set notificationType change'" + notificationType + "' is not one of valid types [investigated, alerted].");
      break;
    }
  }
});


When("popup with information about queued {string} is shown", function(notificationType) {
  switch (notificationType) {
    case 'investigation': {
      cy.contains(/You queued an investigation for 1 part/i).should('be.visible');
      break;
    }
    case 'alert': {
      cy.contains(/You queued an alert for 1 part/i).should('be.visible');
      break;
    }
    default: {
      throw new Error("Set notificationType '" + notificationType + "' is not one of valid types [investigation, alert].");
      break;
    }
  }
});


When("user navigate to {string} with button in popup", function(popupClick) {
  cy.get('a').contains('Go to Queue').click();
});


When("open details of created {string}", () => {
  cy.wait(1000);
  cy.get('[data-testid="table-menu-button"]').first().click(); //the first investigation will be opened
    if (cy.get('[data-testid="table-menu-button--actions.viewDetails"]').should('not.be.visible')) {
            cy.get('[data-testid="table-menu-button"]').first().click();
            cy.get('[data-testid="table-menu-button--actions.viewDetails"]').first().click();
          } else {
              cy.get('[data-testid="table-menu-button--actions.viewDetails"]').first().click();
          }
});


When("user confirm cancellation of selected {string}", function(notificationType) {
  cy.get('[class="mdc-dialog__container"]').find('.mdc-checkbox').click();
  cy.get('span').contains('Confirm cancellation').click();
});


When("user {string} selected {string}", function(action) {
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


When("user confirm approval of selected {string}", function(action) {
  cy.get('app-confirm').find('span').contains('Approve').click();
});


Then("informations for selected investigation are displayed as expected", () => {
// --- TBD --- include: overview, supplier parts, STATUS
});


Then("selected {string} has been {string} as expected", function(notificationType, expectedStatus) {
    switch (expectedStatus) {
      case 'canceled': {
        cy.get('[title="Cancelled"]').should('be.visible');
        break;
      }
      case 'approved': {
      // same as "requested"
        cy.get('[title="Requested"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      case 'accepted': {
        cy.get('[title="Accepted"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      case 'declined': {
        cy.get('[title="Declined"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      case 'acknowledged': {
        cy.get('[title="Acknowledged"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      case 'closed': {
        cy.get('[title="Closed"]', { timeout: 10000 }).should('be.visible');
        break;
      }
      default: {
        throw new Error("Set expected status '" + expectedStatus + "' is not one of valid status [canceled, approved, accepted, declined, acknowledged, closed].");
        break;
      }
    }
});


Then("popup for successful {string} has been shown", function(status) {
    switch (status) {
      case 'cancellation': {
        cy.contains(/.*was canceled successfully./i).should('be.visible');
        break;
      }
      case 'approval': {
        cy.contains(/.*was approved successfully./i).should('be.visible');
        break;
      }
      case 'acceptance': {
        cy.contains(/.*was accepted successfully./i).should('be.visible');
        break;
      }
      case 'declination': {
        cy.contains(/.*was declined successfully./i).should('be.visible');
        break;
      }
      case 'acknowledge': {
        cy.contains(/.*was acknowledged successfully./i).should('be.visible');
        break;
      }
      case 'closure': {
        cy.contains(/.*was closed successfully./i).should('be.visible');
        break;
      }
      default: {
        throw new Error("Set expected status '" + status + "' is not one of valid status [cancellation, approval, acceptance, declination, acknowledge, closure].");
        break;
      }
    }
});


When("selected {string} is not allowed to be {string}", function(notificationType, status) {
    switch (status) {
      case 'canceled': {
        cy.get('div').contains('/^Cancel$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'approved': {
        cy.get('div').contains('/^Approve$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'accepted': {
        cy.get('div').contains('/^Accept$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'declined': {
        cy.get('div').contains('/^Decline$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'acknowledged': {
        cy.get('div').contains('/^Acknowledge$/', {matchCase: true}).should('not.exist');
        break;
      }
      case 'closed': {
        cy.get('div').contains('/^Close$/', {matchCase: true}).should('not.exist');
        break;
      }
      default: {
        throw new Error("Set status '" + status + "' is not one of valid status [canceled, approved, accepted, declined, acknowledged, closed].");
        break;
      }
    }
});
