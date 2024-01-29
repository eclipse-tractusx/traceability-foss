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

# Nonroot user is not needed beause we are using the "nginx-unprivileged" image
# STAGE 1: Build
FROM node:18-alpine AS builder

# Copy dependencies info
COPY ./frontend/package.json ./frontend/yarn.lock ./

# Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN yarn install && mkdir /ng-app && mv ./node_modules ./ng-app



# Set workdir and copy
WORKDIR /ng-app
COPY ./frontend .

## Build the angular app in production mode and store the artifacts in dist folder
RUN yarn build:prod && mv /ng-app/dist/index.html /ng-app/dist/index.html.reference

# STAGE 2: Serve
FROM nginxinc/nginx-unprivileged:alpine
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=10s \
    CMD curl -fSs 127.0.0.1:8080/healthz || exit 1

USER root

RUN rm /usr/share/nginx/html/index.html && rm /etc/nginx/conf.d/default.conf

# Copy project files from ‘builder’ stage copy over the artifacts in dist folder to default nginx public folder
COPY --from=builder /ng-app/dist /usr/share/nginx/html
# Copy license info into final artifact
COPY ./DEPENDENCIES_FRONTEND /usr/share/nginx/html
COPY ./SECURITY.md /usr/share/nginx/html
COPY ./NOTICE.md /usr/share/nginx/html
COPY ./LICENSE /usr/share/nginx/html
# Give ownership to nginx user over dir with content

RUN chown -R nginx:nginx /usr/share/nginx/html/
USER nginx

# Install Node.js from builder stage
COPY --from=builder /usr/lib /usr/lib
COPY --from=builder /usr/local/include /usr/local/include
COPY --from=builder /usr/local/bin /usr/local/bin

# Copy NGINX server configuration
COPY ./frontend/build/security-headers.conf ./frontend/build/nginx.conf /etc/nginx/

# Copy custom script runner
COPY ./frontend/scripts/custom-injector.sh /docker-entrypoint.d/00-custom-injector.sh

# Add env variables inject script
COPY ./frontend/scripts/inject-dynamic-env.js /docker-entrypoint.d/
# Add replace base url script
COPY ./frontend/scripts/replace-base-href.js /docker-entrypoint.d/

USER root
RUN chown nginx:nginx /etc/nginx/nginx.conf
RUN chown nginx:nginx /etc/nginx/security-headers.conf

USER 101
