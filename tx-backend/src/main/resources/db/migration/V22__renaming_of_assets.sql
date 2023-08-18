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
