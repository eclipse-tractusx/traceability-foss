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

INSERT INTO asset (id, customer_part_id, id_short, manufacturer_id, manufacturer_name, manufacturer_part_id,
                   manufacturing_country, manufacturing_date, name_at_customer, name_at_manufacturer, quality_type,
                   batch_id, part_instance_id, van, owner)
VALUES ('urn:uuid:51ff7c73-34e9-45d4-816c-d92ownerbpna', '1O222E8-43', '--', 'BPNL00000003CML1', 'Tier A', '1O222E8-43',
        'DEU', '2022-02-04 13:48:54', 'Transmission', 'Transmission', 0, '--', 'NO-712627233731926672258402', NULL, 2);

INSERT INTO asset (id, customer_part_id, id_short, manufacturer_id, manufacturer_name, manufacturer_part_id,
                   manufacturing_country, manufacturing_date, name_at_customer, name_at_manufacturer, quality_type,
                   batch_id, part_instance_id, van, owner)
VALUES ('urn:uuid:51ff7c73-34e9-45d4-816c-d92ownerbpnb', '1O222E8-43', '--', 'BPNL00000003CNKC', 'Tier A', '1O222E8-43',
        'DEU', '2022-02-04 13:48:54', 'Transmission', 'Transmission', 0, '--', 'NO-712627233731926672258402', NULL, 0);
