## Copyright (c) 2023 Contributors to the Eclipse Foundation
##
## See the NOTICE file(s) distributed with this work for additional
## information regarding copyright ownership.
##
## This program and the accompanying materials are made available under the
## terms of the Apache License, Version 2.0 which is available at
## https://www.apache.org/licenses/LICENSE-2.0. *
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
## WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
## License for the specific language governing permissions and limitations
## under the License.
##
## * SPDX-License-Identifier: Apache-2.0

create table bpn_edc_mappings
(
	bpn varchar(255),
	url varchar(255),
    created timestamptz,
    updated timestamptz,
	primary key (bpn),
	unique(url)
);

insert into bpn_edc_mappings values('BPNL00000003AYRE', 'https://tracex-consumer-controlplane.dev.demo.catena-x.net', current_timestamp, null);
insert into bpn_edc_mappings values('BPNL00000003B2OM', 'https://tracex-test-consumer-controlplane.dev.demo.catena-x.net', current_timestamp, null);
