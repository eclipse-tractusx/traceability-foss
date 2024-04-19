ALTER TABLE traction_battery_code_subcomponent
    ADD COLUMN "asset_as_built_id" varchar(255) NULL;
ALTER TABLE traction_battery_code_subcomponent
    ADD CONSTRAINT fk_asset_traction_battery_code_subcomponent FOREIGN KEY (asset_as_built_id) REFERENCES assets_as_built (id) on delete cascade;

ALTER VIEW IF EXISTS assets_as_built_view
    ALTER COLUMN traction_battery_code DROP DEFAULT;

ALTER TABLE assets_as_built
    DROP COLUMN traction_battery_code;
