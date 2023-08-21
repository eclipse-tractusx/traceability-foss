#!/bin/bash

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

version=${1:-LATEST}


echo "Using version = ${version}..."

if [[ $2 == "--summary" ]]; then
    java -jar ./scripts/download/org.eclipse.dash.licenses-${version}.jar yarn.lock -project automotive.tractusx -summary ../DEPENDENCIES_FRONTEND
    grep -E '(restricted, #)|(restricted$)' ../DEPENDENCIES_FRONTEND | if test $(wc -l) -gt 0; then exit 1; fi
else
    java -jar ./scripts/download/org.eclipse.dash.licenses-${version}.jar yarn.lock -project automotive.tractusx
fi
