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

create table asset_parent_descriptors
(
    asset_entity_id varchar(255) not null,
    id              varchar(255),
    id_short        varchar(255)
);

alter table if exists asset_parent_descriptors add constraint fk_asset foreign key (asset_entity_id) references asset;

alter table if exists asset
    add column owner int4;

alter table if exists asset DROP COLUMN supplier_part;

