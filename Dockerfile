# STAGE 1: Build
FROM node:alpine as builder
# Configuration profile to use
ARG PROFILE=dev
# Install dumb-init (Very handy for easier signal handling of SIGINT/SIGTERM/SIGKILL etc.)
#RUN wget https://github.com/Yelp/dumb-init/releases/download/v1.2.0/dumb-init_1.2.0_amd64.deb
#RUN dpkg -i dumb-init_*.deb
#ENTRYPOINT ["dumb-init"]
COPY /package.json /yarn.lock ./

RUN echo "http://dl-cdn.alpinelinux.org/alpine/latest-stable/main" > /etc/apk/repositories
RUN echo "http://dl-cdn.alpinelinux.org/alpine/latest-stable/community" >> /etc/apk/repositories

RUN apk add --no-cache chromium --repository=http://dl-cdn.alpinelinux.org/alpine/v3.10/main
RUN apk add --no-cache --update-cache gcc python3 build-base wget freetype-dev libpng-dev openblas-dev

# Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN yarn install && mkdir /ng-app && mv ./node_modules ./ng-app

# Run sonarqube report scanner
#RUN npm run sonar

WORKDIR /ng-app
COPY ./ .

## Build the angular app in production mode and store the artifacts in dist folder
RUN npm run ng build -- --configuration=${PROFILE} --output-path=dist

# STAGE 2: Serve
FROM nginxinc/nginx-unprivileged:alpine
# Remove NGINX default configuration
RUN rm /etc/nginx/conf.d/default.conf
# Ensure that /tmp exists and user has access to it
RUN chmod 1777 /tmp
# Create directory for SSL certificates
# Copy project files
# From ‘builder’ stage copy over the artifacts in dist folder to default nginx public folder
COPY --from=builder /ng-app/dist /usr/share/nginx/html
# Copy NGINX server configuration
COPY ./build/server.conf /etc/nginx/conf.d/
# Validate NGINX configuration
RUN nginx -t
