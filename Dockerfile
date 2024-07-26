# Copyright (c) 2023 Contributors to the Eclipse Foundation
#
# See the NOTICE file(s) distributed with this work for additional
# information regarding copyright ownership.
#
# This program and the accompanying materials are made available under the
# terms of the Apache License, Version 2.0 which is available at
# https://www.apache.org/licenses/LICENSE-2.0. *
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations
# under the License.
#
# * SPDX-License-Identifier: Apache-2.0

# Dependencies
FROM maven:3-openjdk-18-slim AS maven
ARG BUILD_TARGET=tx-backend

# Create Working Directory
WORKDIR /build

# Copy to Working Directory
COPY pom.xml .
COPY tx-parent-spring-boot tx-parent-spring-boot
COPY tx-cucumber-tests tx-cucumber-tests
COPY tx-coverage tx-coverage
COPY tx-models tx-models
COPY docs docs
COPY tx-backend tx-backend

# the --mount option requires BuildKit.
# --mount=type=cache,target=/root/.m2 -> mounts cache volume to the .m2 directorym in container
# -B Batch Mode
# -pl specify project to build
# :Variable specifies an artifact ID of project to build
# -am build all dependencies of a project
RUN --mount=type=cache,target=/root/.m2 mvn -B clean package -pl :$BUILD_TARGET -am -DskipTests

# Copy the jar and build image
FROM eclipse-temurin:21-jre-alpine AS traceability-app

WORKDIR /app

COPY --chmod=755 --from=maven /build/tx-backend/target/traceability-app-*-exec.jar app.jar

RUN apk info -vv

USER 10000:1000

ENTRYPOINT ["java", "-jar", "app.jar"]

HEALTHCHECK --interval=5m --timeout=3s \
  CMD curl -f http://localhost:4004/actuator/health || exit 1
