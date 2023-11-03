import { Given } from '@badeball/cypress-cucumber-preprocessor';


Given('user logged in as {string}', function(userType) {
  let loginMail = '';
  let loginPW = '';
  switch (userType) {
    case 'supervisor': {
      loginMail = Cypress.env('SUPERVISOR_LOGIN');
      loginPW = Cypress.env('SUPERVISOR_PW');
    }
    case 'user': {
      loginMail = Cypress.env('USER_LOGIN');
      loginPW = Cypress.env('USER_PW');
    }
    case 'admin': {
      loginMail = Cypress.env('ADMIN_LOGIN');
      loginPW = Cypress.env('ADMIN_PW');
    }

  }

  cy.visit('https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/auth?client_id=Cl17-CX-Part&redirect_uri=https%3A%2F%2Ftraceability-portal-e2e-a.dev.demo.catena-x.net%2Fdashboard&state=0aaee615-388e-400c-8b0c-81ac443a2cf3&response_mode=fragment&response_type=code&scope=openid&nonce=4104d5ab-b2bd-43a1-b6c2-7adf30543579&code_challenge=uXHR3gDRnSyjPEu8yWNdzm6Izsd7cKzEryfvRAtJTjU&code_challenge_method=S256');
  cy.get('.search').click();
  cy.get('.search').type('CX-Test-Access');
  cy.wait(5000);
  cy.get('.CX_Test_Access').click();
  cy.wait(5000);
  cy.get('input[name="username"]').type(loginMail);
  cy.wait(5000);
  cy.get('input[name="password"]').click().focus().type(loginPW);
  cy.wait(5000);
  cy.get('input[type="submit"]').click();
});


Given('user is directed to the {string}', function(value) {
  cy.wait(5000);
  cy.get('div.layout-content').should('exist');
});
