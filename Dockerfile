# STAGE 1: Build
FROM node:alpine as builder
# Configuration profile to use
ARG PROFILE=production
# Copy dependencies info
COPY /package.json /yarn.lock ./
# Setup apk repositories
RUN echo "http://dl-cdn.alpinelinux.org/alpine/latest-stable/main" > /etc/apk/repositories
RUN echo "http://dl-cdn.alpinelinux.org/alpine/latest-stable/community" >> /etc/apk/repositories
# Install neccessery system dependencies required for the yarn inatll
RUN apk add --no-cache chromium --repository=http://dl-cdn.alpinelinux.org/alpine/v3.10/main
RUN apk add --no-cache --update-cache gcc python3 build-base wget freetype-dev libpng-dev openblas-dev

# Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN yarn install && mkdir /ng-app && mv ./node_modules ./ng-app

# Set workdir and copy
WORKDIR /ng-app
COPY ./ .

## Build the angular app in production mode and store the artifacts in dist folder
RUN NODE_ENV=production npm run ng build -- --configuration=${PROFILE} --output-path=dist
RUN mv /ng-app/dist/index.html /ng-app/dist/index.html.reference

# STAGE 2: Serve
FROM nginxinc/nginx-unprivileged:alpine

USER root
RUN whoami
RUN rm /usr/share/nginx/html/index.html

# Copy project files
# From ‘builder’ stage copy over the artifacts in dist folder to default nginx public folder
COPY --from=builder /ng-app/dist /usr/share/nginx/html

# Give ownership to nginx user over dir with content

RUN chmod 666 -R /usr/share/nginx/html/
USER nginx

# Install Node.js from builder stage
COPY --from=builder /usr/lib /usr/lib
COPY --from=builder /usr/local/share /usr/local/share
COPY --from=builder /usr/local/lib /usr/local/lib
COPY --from=builder /usr/local/include /usr/local/include
COPY --from=builder /usr/local/bin /usr/local/bin

# Copy NGINX server configuration
COPY ./build/server.conf /etc/nginx/conf.d/
# Add env variables inject script
COPY ./scripts/run-inject-dynamic-env.sh /docker-entrypoint.d/00-inject-dynamic-env.sh
COPY ./scripts/inject-dynamic-env.js /docker-entrypoint.d/

RUN whoami

# Validate NGINX configuration
RUN nginx -t
