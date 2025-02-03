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

# https://docs.getxray.app/display/XRAY/Exporting+Cucumber+Tests+-+REST
curl -u $JIRA_USERNAME:$JIRA_PASSWORD "https://jira.catena-x.net/rest/raven/1.0/export/test?filter=${JIRA_FILTER_ID:-11645}&fz=true" -o features.zip
unzip -o features.zip -d cypress/e2e
rm -f features.zip
