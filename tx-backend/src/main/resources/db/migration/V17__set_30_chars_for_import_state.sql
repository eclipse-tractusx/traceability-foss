DROP VIEW IF EXISTS assets_as_built_view;
ALTER TABLE assets_as_planned ALTER COLUMN import_state TYPE VARCHAR (30);
ALTER TABLE assets_as_built ALTER COLUMN import_state TYPE VARCHAR(30);
