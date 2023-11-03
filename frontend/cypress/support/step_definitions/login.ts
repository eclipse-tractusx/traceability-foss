import { Given } from '@badeball/cypress-cucumber-preprocessor';


Given(/^user logged in as "supervisor"$/, () => {
  // Simulate the login process here, including entering credentials and submitting the form
  const supervisorLogin = Cypress.env('SUPERVISOR_LOGIN');
  const supervisorPW = Cypress.env('SUPERVISOR_PW');


  cy.visit('https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/auth?client_id=Cl17-CX-Part&redirect_uri=https%3A%2F%2Ftraceability-portal-e2e-a.dev.demo.catena-x.net%2Fdashboard&state=0aaee615-388e-400c-8b0c-81ac443a2cf3&response_mode=fragment&response_type=code&scope=openid&nonce=4104d5ab-b2bd-43a1-b6c2-7adf30543579&code_challenge=uXHR3gDRnSyjPEu8yWNdzm6Izsd7cKzEryfvRAtJTjU&code_challenge_method=S256');
  cy.get('.search').click();
  cy.get('.search').type('CX-Test-Access');
  cy.get('.CX_Test_Access').click();

  cy.get('input[name="username"]').type(supervisorLogin);

  cy.get('input[name="password"]').click().focus().type(supervisorPW);

  cy.get('input[type="submit"]').click();

});


Given('user is directed to the {string}', function(string) {
  cy.get('div.layout-content').should('exist');
});
