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

ALTER TABLE asset RENAME TO assets_as_built;
ALTER TABLE asset_child_descriptors RENAME TO assets_as_built_childs;
ALTER TABLE asset_parent_descriptors RENAME TO assets_as_built_parents;
ALTER TABLE assets_investigations RENAME TO assets_as_built_investigations;
ALTER TABLE assets_notifications RENAME TO assets_as_built_notifications;

ALTER TABLE assets_as_built
    ADD COLUMN active_alert boolean NOT NULL default (false);

ALTER TABLE assets_as_built
    ADD COLUMN semantic_model_id varchar(255);

ALTER TABLE assets_as_built DROP COLUMN part_instance_id;

ALTER TABLE assets_as_built DROP COLUMN batch_id;
