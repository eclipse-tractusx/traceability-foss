## E2E tests

We use Cypress as e2e testing framework (https://www.cypress.io/) + https://www.npmjs.com/package/@cypress/schematic.

To be able to use Behavior-driven development (BDD) approach we have configured additional lib: https://github.com/badeball/cypress-cucumber-preprocessor

## File structure

- `cypress.config.ts` - config for cypress test
- `cypress/` - main directory for cypress
- `cypress/e2e/` - contains e2e scenarios as \*.feature files (BDD approach - Gherkin format: https://cucumber.io/docs/gherkin/reference/) e.g. dashboard.feature
- `cypress/support/step_definitions/` - contains implementation e2e scenarios as \*.ts files e.g. dashboard.ts
- `cypress/integration/pages/` - contains classes representing pages with some useful function e.g. DashboardPage.ts

## How to run

- local env (via cypress app):
  - run command `ng e2e` - it should open new window with Cypress tool
  - click on "E2E testing" and then on "Start E2E Testing in Chrome"
  - you should see new window under url: http://localhost:4200/\_\_/#/specs when you can click on given scenario to run it.
- local env (run tests in command line): `yarn run cypress:run`
