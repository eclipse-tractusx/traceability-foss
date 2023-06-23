# Nonroot user is not needed beause we are using the "nginx-unprivileged" image
# STAGE 1: Build
FROM node:18-alpine AS builder

# Copy dependencies info
COPY /package.json /yarn.lock ./

# Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN yarn install && mkdir /ng-app && mv ./node_modules ./ng-app

# Set workdir and copy
WORKDIR /ng-app
COPY ./ .

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

# Give ownership to nginx user over dir with content

RUN chown -R nginx:nginx /usr/share/nginx/html/
USER nginx

# Install Node.js from builder stage
COPY --from=builder /usr/lib /usr/lib
COPY --from=builder /usr/local/include /usr/local/include
COPY --from=builder /usr/local/bin /usr/local/bin

# Copy NGINX server configuration
COPY ./build/security-headers.conf ./build/nginx.conf /etc/nginx/

# Copy custom script runner
COPY scripts/custom-injector.sh /docker-entrypoint.d/00-custom-injector.sh

# Add env variables inject script
COPY ./scripts/inject-dynamic-env.js /docker-entrypoint.d/
# Add replace base url script
COPY ./scripts/replace-base-href.js /docker-entrypoint.d/

USER root
RUN chown nginx:nginx /etc/nginx/nginx.conf
RUN chown nginx:nginx /etc/nginx/security-headers.conf

USER 101
