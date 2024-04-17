ALTER TABLE assets_as_built
    ADD CONSTRAINT unique_traction_battery_code UNIQUE(traction_battery_code);

ALTER TABLE traction_battery_code_subcomponent
    ADD CONSTRAINT fk_asset_traction_battery_code_subcomponent FOREIGN KEY (traction_battery_code) REFERENCES public.assets_as_built (traction_battery_code);
