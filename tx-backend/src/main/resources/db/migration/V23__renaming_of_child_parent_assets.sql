ALTER TABLE assets_as_built_parents
    RENAME COLUMN asset_entity_id TO asset_as_built_id;
ALTER TABLE assets_as_built_childs
    RENAME COLUMN asset_entity_id TO asset_as_built_id;
