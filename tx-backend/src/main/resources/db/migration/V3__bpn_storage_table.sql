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

create table bpn_storage
(
	manufacturer_id   varchar(255) not null,
	manufacturer_name varchar(255) not null,
	primary key (manufacturer_id)
);

insert into bpn_storage values('BPNL00000000BJTL', 'Sub Tier C');
insert into bpn_storage values('BPNL00000003AXS3', 'Sub Tier B');
insert into bpn_storage values('BPNL00000003AYRE', 'OEM A');
insert into bpn_storage values('BPNL00000003AZQP', 'OEM C');
insert into bpn_storage values('BPNL00000003B0Q0', 'N-Tier A');
insert into bpn_storage values('BPNL00000003B2OM', 'Tier A');
insert into bpn_storage values('BPNL00000003B3NX', 'Sub Tier A');
insert into bpn_storage values('BPNL00000003B5MJ', 'Tier B');
insert into bpn_storage values('BPNL00000003CSGV', 'Tier C');
