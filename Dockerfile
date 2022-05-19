# STAGE 1: Build
FROM node:14.14.0 as builder
# Configuration profile to use
ARG PROFILE=dev
# Install dumb-init (Very handy for easier signal handling of SIGINT/SIGTERM/SIGKILL etc.)
#RUN wget https://github.com/Yelp/dumb-init/releases/download/v1.2.0/dumb-init_1.2.0_amd64.deb
#RUN dpkg -i dumb-init_*.deb
#ENTRYPOINT ["dumb-init"]
COPY /webapp/package.json /webapp/yarn.lock ./

# Storing node modules on a separate layer will prevent unnecessary npm installs at each build
RUN yarn install && mkdir /ng-app && mv ./node_modules ./ng-app

# Run sonarqube report scanner
#RUN npm run sonar

WORKDIR /ng-app
COPY ./webapp .

## Build the angular app in production mode and store the artifacts in dist folder
RUN npm run ng build -- --configuration=${PROFILE} --output-path=dist

# STAGE 2: Serve
FROM nginx
# Remove NGINX default configuration
RUN rm /etc/nginx/conf.d/default.conf
# Create directory for SSL certificates
# Copy project files
# From ‘builder’ stage copy over the artifacts in dist folder to default nginx public folder
COPY --from=builder /ng-app/dist /usr/share/nginx/html
# Copy NGINX server configuration
COPY ./webapp/build/server.conf /etc/nginx/conf.d/
# Validate NGINX configuration
RUN nginx -t
