#    Copyright (c) 2023 Contributors to the Eclipse Foundation
#
#    See the NOTICE file(s) distributed with this work for additional
#    information regarding copyright ownership.
#
#    This program and the accompanying materials are made available under the
#    terms of the Apache License, Version 2.0 which is available at
#    https://www.apache.org/licenses/LICENSE-2.0.
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#    License for the specific language governing permissions and limitations
#    under the License.
#
#  SPDX-License-Identifier: Apache-2.0

# DISCLAIMER!
# purpose of this file is to run cypress E2E tests locally, we don't use it in other env
# because of statement above, we don't need to change settings to comply with some KICS requirements


# if you need to change image please make sure use the same version in all places
# (here and in .github/workflows/e2e-tests.yml)

# why we don't use pure browser image locally?
# because of some problems on local docker image:
# "Command was killed with SIGTRAP (Debugger breakpoint):..."
# but cypress/included:12.3.0 version base on cypress/browsers:node16.16.0-chrome107-ff107-edge
FROM cypress/included:12.3.0

USER root
RUN mkdir /ng-app
WORKDIR /ng-app

# Copy dependencies info
COPY package.json  /ng-app/package.json
COPY yarn.lock /ng-app/yarn.lock

RUN yarn install
# https://docs.cypress.io/guides/guides/launching-browsers#Linux-Dependencies
RUN npx playwright install --with-deps webkit
