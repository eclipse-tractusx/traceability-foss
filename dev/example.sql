##    Copyright (c) 2023 Contributors to the Eclipse Foundation
##
##    See the NOTICE file(s) distributed with this work for additional
##    information regarding copyright ownership.
##
##    This program and the accompanying materials are made available under the
##    terms of the Apache License, Version 2.0 which is available at
##    https://www.apache.org/licenses/LICENSE-2.0.
##
##    Unless required by applicable law or agreed to in writing, software
##    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
##    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
##    License for the specific language governing permissions and limitations
##    under the License.
##
##  SPDX-License-Identifier: Apache-2.0

## Query for assets with childs:

SELECT asset.id, asset.customer_part_id,asset.id_short, asset.manufacturer_id, asset.manufacturer_name, asset.manufacturer_part_id, asset.manufacturing_country, asset.manufacturing_date, asset.name_at_customer, asset.name_at_manufacturer, asset.quality_type, asset.batch_id, asset.part_instance_id, asset.van
FROM public.asset
         JOIN asset_child_descriptors
              ON asset.id = asset_child_descriptors.id;

## Query for assets with parents:

SELECT asset.id, asset.customer_part_id,asset.id_short, asset.manufacturer_id, asset.manufacturer_name, asset.manufacturer_part_id, asset.manufacturing_country, asset.manufacturing_date, asset.name_at_customer, asset.name_at_manufacturer, asset.quality_type, asset.batch_id, asset.part_instance_id, asset.van
FROM public.asset
         JOIN asset_parent_descriptors
              ON asset.id = asset_parent_descriptors.id;

## Clean up data consumption process relevant things:

delete FROM public.asset_child_descriptors;
delete FROM public.asset_parent_descriptors;
delete FROM public.asset;
delete FROM public.shell_descriptor;


