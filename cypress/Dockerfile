
# if you need to change image please make sure use the same version in all places
# (here and in .github/workflows/e2e-tests.yml)

# why we don't use pure browser image locally?
# because of some problems on local docker image:
# "Command was killed with SIGTRAP (Debugger breakpoint):..."
# but cypress/included:12.3.0 version base on cypress/browsers:node16.16.0-chrome107-ff107-edge
FROM cypress/included:12.3.0

RUN mkdir /ng-app
WORKDIR /ng-app

# Copy dependencies info
COPY package.json  /ng-app/package.json
COPY yarn.lock /ng-app/yarn.lock

RUN yarn install
# https://docs.cypress.io/guides/guides/launching-browsers#Linux-Dependencies
RUN npx playwright install --with-deps webkit

