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
//package org.eclipse.tractusx.traceability.test;

//import static from org.eclipse.tractusx.traceability.test.validator.TestUtils.wrapStringWithTimestamp;
import { When, Then, Given } from '@badeball/cypress-cucumber-preprocessor';
import { QualityInvestigationsPage } from '../../integration/pages/QualityInvestigationsPage';

let notificationDescription = null;

Then("select {string} other part", (partAmount) => {
//since IDs of desired asset are not shown in FE the selection has to be done by other number
  //cy.get('span').contains('NO-989134870198932317923938').parent('.row').as('part');
  //cy.get('@part').get('#mat-mds-checkbox-21').click();
  cy.get('span').contains('As Planned').click(); // see comment above. This has to be done to avoid asPlanned selection
  cy.get('#mat-mdc-checkbox-38*').click(); //---TBD--- this is only a method to make it run, has to be changed to selected part as above!
//   cy.get('span').contains('NO-989134870198932317923938').parentsUntil('.mat-mdc-row mdc-data-table__row cdk-row ng-star-inserted').first().get('[type="checkbox"]').click();
//cy.get('span').contains('NO-989134870198932317923938').parentsUntil('.mat-mdc-row mdc-data-table__row cdk-row ng-star-inserted').children().get('[type="checkbox"]').first().click();
//   cy.get('span').contains('NO-989134870198932317923938').parentsUntil('.mat-mdc-cell mdc-data-table__cell cdk-cell table--cell cdk-column-semanticModelId mat-column-semanticModelId ng-star-inserted').get('[type="checkbox"]').first().click();

//   cy.contains('NO-989134870198932317923938')
//     .parentsUntil('tr').last()
//     .within(() => {
//       // all searches are automatically rooted to the found tr element
//       cy.get('[id="mat-mdc-checkbox-234-input"]').click()
      //cy.get('td').get('mat-checkbox').click()
 //   });

//     <input type="checkbox" class="mdc-checkbox__native-control" id="mat-mdc-checkbox-234-input" tabindex="0">
//     #mat-mdc-checkbox-234-input
    //*[@id="mat-mdc-checkbox-234-input"]
    ///html/body/app-root/div/div/app-layout/main/div/div/div/app-other-parts/div/as-split/as-split-area[1]/mat-tab-group/div/mat-tab-body[1]/div/app-supplier-parts/div/app-parts-table/div[2]/table/tbody/tr[1]/td[1]/mat-checkbox/div/div/input
//   cy.contains('span', 'NO-989134870198932317923938')  // gives you the cell
//     .parent()                              // gives you the row
//     .within(($tr) => {                       // filters just that row
//       cy.children().get($div)//cy.get('[type="checkbox"]')//.get('[data-testid="select-one--test-id"]') // finds the buttons cell of that row
//       .click()
//       });


      //     cy.contains('span', 'NO-989134870198932317923938')  // gives you the cell
      //       .siblings()                            // gives you all the other cells in the row
      //       .get('[type="checkbox"]')               // finds the delete button
      //       .click()
  //cy.get('span').contains('NO-989134870198932317923938').parentsUntil('.mat-mdc-cell mdc-data-table__cell cdk-cell table--cell cdk-column-semanticModelId mat-column-semanticModelId ng-star-inserted').get('[type="checkbox"]').first().click();
});

Then("start investigation creation with description {string}", function (description) {
  //notificationDescription = wrapStringWithTimestamp(input.get("description"));
  //notificationDescription = "Test 123123 description";
  notificationDescription = description;
  cy.get('div').contains('Start investigation').click();
  cy.get('mat-label').contains('Description').click().type(description);
});

When("severity {string}", function (severity) {
  cy.get('#mat-select-56').click(); // First the dropdown has to be opened.
  cy.get('p').contains(severity).click();
  //cy.get('#mat-select-56-panel').select(severity);  // Dropdown menu has own id.
});

When("{string} deadline", function (deadline) {
//---TBD---
      if (deadline == 'no') {
        // do nothing
      } else {
       // ---TBD---
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
  //cy.get('p').contains(notificationDescription).parentsUntil('.mat-mdc-row mdc-data-table__row cdk-row no-hover ng-star-inserted').get('[class="mat-mdc-button-touch-target"]').click()
  cy.get('[data-testid="table-menu-button"]').first().click();
  cy.get('[data-testid="table-menu-button--actions.viewDetails"]').first().click();
});

When("user start cancelation", () => {
  cy.get('div').contains('Cancel').click();
});

//When user cancel selected investigation with entering "correct" id
// #include: check popup (id, description, status, created, createdby, texts, buttons (cancel, approve), then click on Delete
// #check: Deletion is only possible after entering the expected id
// #check: popup on the right sight is shown
When("user cancel selected investigation with entering {string} id", (input) => {
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
        cy.log('Cancel Id ' + cancelId)
        cy.get('#mat-input-0').click().type(cancelId);
      });
      cy.get('span').contains('Confirm cancellation').click();
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
  }
});

Then("informations for selected investigation are displayed as expected", () => {
// ---TBD--- include: overview, supplier parts, STATUS
});

Then("selected {string} has been {string} as expected", (notificationType, expectedStatus) => {
matched = false;
    switch (expectedStatus) {
      case 'canceled': {
        matched = true;
        cy.get('[title="Cancelled"]').should('be.visible');
        break;
      }
      case 'requested': {
      // same as "approved"
      matched = true;
        cy.get('[title="Requested"]').should('be.visible');
        break;
      }
      case 'accepted': {
      matched = true;
        cy.get('[title="Accepted"]').should('be.visible');
        break;
      }
      case 'declined': {
      matched = true;
        cy.get('[title="Declined"]').should('be.visible');
        break;
      }
      case 'acknowledged': {
      matched = true;
        cy.get('[title="Acknowledged"]').should('be.visible');
        break;
      }
      case 'closed': {
      matched = true;
        cy.get('[title="Closed"]').should('be.visible');
        break;
      }
    }
    if (!matched) {
      throw new Error("Set expected status '" + expectedStatus + "' is not one of valid status [canceled, requested, accepted, declined, acknowledged, closed].");
    }
});

When("selected {string} is not allowed to be {string}", (notificationType, status) => {
matched = false;
    switch (status) {
      case 'canceled': {
        matched = true;
        cy.get('div').contains('/^Cancel$/').should('not.exist');
        break;
      }
      case 'approved': {
      matched = true;
        cy.get('div').contains('Approve').should('not.exist');
        break;
      }
      case 'accepted': {
      matched = true;
        cy.get('div').contains('Accept').should('not.exist');
        break;
      }
      case 'declined': {
      matched = true;
        cy.get('div').contains('Decline').should('not.exist');
        break;
      }
      case 'acknowledged': {
      matched = true;
        cy.get('div').contains('Acknowledge').should('not.exist');
        break;
      }
      case 'closed': {
      matched = true;
        cy.get('div').contains('Close').should('not.exist');
        break;
      }
    }
    if (!matched) {
      throw new Error("Set status '" + status + "' is not one of valid status [canceled, approved, accepted, declined, acknowledged, closed].");
    }
});
