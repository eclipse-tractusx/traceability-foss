import { Given } from '@badeball/cypress-cucumber-preprocessor';


Given('user logged in as {string}', function(userType) {
  let loginMail = '';
  let loginPW = '';
  switch (userType) {
    case 'supervisor': {
      loginMail = Cypress.env('SUPERVISOR_LOGIN');
      loginPW = Cypress.env('SUPERVISOR_PW');
      break;
    }
    case 'user': {
      loginMail = Cypress.env('USER_LOGIN');
      loginPW = Cypress.env('USER_PW');
      break;
    }
    case 'admin': {
      loginMail = Cypress.env('ADMIN_LOGIN');
      loginPW = Cypress.env('ADMIN_PW');
      break;
    }

  }

  let redirectUri = encodeURIComponent(Cypress.env('REDIRECT_URI'));

  cy.visit('https://centralidp.int.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/auth?client_id=app12&redirect_uri=' + redirectUri);
  cy.get('.search').click();
  cy.get('.search').type('BMW Trace-X');
  cy.get('.idp-card').contains('BMW Trace-X').click();
  cy.get('input[name="username"]').type(loginMail);
  cy.get('input[name="password"]').click().focus().type(loginPW, {log:false});
  cy.get('input[type="submit"]').click();
});


Given('user is directed to the {string}', function(value) {
  cy.get('div.layout-content').should('exist');
});
