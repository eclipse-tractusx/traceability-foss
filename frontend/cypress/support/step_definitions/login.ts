import { Given, Then } from '@badeball/cypress-cucumber-preprocessor';
import { DashboardPage } from '../../integration/pages/DashboardPage';


Given(/^user is logged in as "supervisor"$/, () => {
  // Simulate the login process here, including entering credentials and submitting the form
  cy.visit('https://centralidp.dev.demo.catena-x.net/auth/realms/CX-Central/protocol/openid-connect/auth?client_id=Cl17-CX-Part&redirect_uri=https%3A%2F%2Ftraceability-portal-e2e-a.dev.demo.catena-x.net%2Fdashboard&state=c8729ce7-d710-4139-869a-1154c91814eb&response_mode=fragment&response_type=code&scope=openid&nonce=ee585303-ad03-4772-816f-4937a99917cb&code_challenge=3Qd7kufqOMAetJWnXxSELb25IxLTZXu5QYYEbxVIM7c&code_challenge_method=S256'); // Visit the login page
  cy.get('input[placeholder="Enter your company name"]').type('CX-Test-Access');
  cy.get('a').click();
  cy.get('input#username').type('notrealuser');
  cy.get('input#password').type('notrealpassword');
  cy.get('form#kc-form-login').submit();
});
