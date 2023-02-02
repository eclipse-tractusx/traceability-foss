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

### Using host machine browsers

- local env (via cypress app):
  - run command `ng e2e` - it should open new window with Cypress tool
  - click on "E2E testing" and then on "Start E2E Testing in Chrome"
  - you should see new window under url: http://localhost:4200/\_\_/#/specs when you can click on given scenario to run it.
- local env (run tests in command line): `yarn run cypress:run`

### Using docker browsers

- local env - docker image (use the same browser engine versions as github CI/CD)
  - run this command first to build docker image with cypress browsers: `docker compose -f cypress/docker-compose.yml build `
  - then make sure you have running frontend app: `yarn start` because docker image uses localhost url to connect run tests
  - then we can run E2E cypress tests on these browsers:
    - chrome - `docker compose -f cypress/docker-compose.yml run cypress --browser=chrome`
    - firefox - `docker compose -f cypress/docker-compose.yml run cypress --browser=firefox`
    - webkit (safari's engine) - `docker compose -f cypress/docker-compose.yml run cypress --browser=webkit`

## How it works on github actions (CI/CD)

- configuration file is here: `.github/workflows/e2e-tests.yml`
- we run in parallel e2e tests for 3 browser engines: chrome, firefox and webkit (used by Safari)
- we use one common pre-stage named 'install'
- then base on 'install' stage cache result it runs 3 separated threads for every browser
  - cypress-run-chrome
  - cypress-run-firefox
  - cypress-run-webkit
- every thread as a part of result has an Artifact section with cypress generated files like videos or screenshots. we can download zip file with those files and open video/screenshot to check what happened.
