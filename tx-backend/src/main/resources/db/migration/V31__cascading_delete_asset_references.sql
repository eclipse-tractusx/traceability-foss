ALTER TABLE public.assets_as_built_childs
    DROP CONSTRAINT IF EXISTS fk_asset;

ALTER TABLE public.assets_as_built_parents
    DROP CONSTRAINT IF EXISTS fk_asset;

ALTER TABLE public.submodel_payload
    DROP CONSTRAINT IF EXISTS fk_asset_as_built;

ALTER TABLE public.import_job_assets_as_built
    DROP CONSTRAINT IF EXISTS fk_assets_as_built;

ALTER TABLE public.assets_as_built_notifications
    DROP CONSTRAINT IF EXISTS fk_asset_entity;


-- Re-add constraint for assets_as_built_childs
ALTER TABLE public.assets_as_built_childs
    ADD CONSTRAINT fk_asset
        FOREIGN KEY (asset_as_built_id)
            REFERENCES public.assets_as_built (id)
            ON DELETE CASCADE;

-- Re-add constraint for assets_as_built_parents
ALTER TABLE public.assets_as_built_parents
    ADD CONSTRAINT fk_asset
        FOREIGN KEY (asset_as_built_id)
            REFERENCES public.assets_as_built (id)
            ON DELETE CASCADE;

-- Re-add constraint for submodel_payload
ALTER TABLE public.submodel_payload
    ADD CONSTRAINT fk_asset_as_built
        FOREIGN KEY (asset_as_built_id)
            REFERENCES public.assets_as_built (id)
            ON DELETE CASCADE;

-- Re-add constraint for import_job_assets_as_built
ALTER TABLE public.import_job_assets_as_built
    ADD CONSTRAINT fk_assets_as_built
        FOREIGN KEY (asset_as_built_id)
            REFERENCES public.assets_as_built (id)
            ON DELETE CASCADE;

-- Re-add constraint for assets_as_built_notifications
ALTER TABLE public.assets_as_built_notifications
    ADD CONSTRAINT fk_asset_entity
        FOREIGN KEY (asset_id)
            REFERENCES public.assets_as_built (id)
            ON DELETE CASCADE;


ALTER TABLE public.submodel_payload
    DROP CONSTRAINT IF EXISTS fk_asset_as_planned;

ALTER TABLE public.import_job_assets_as_planned
    DROP CONSTRAINT IF EXISTS fk_assets_as_planned;

ALTER TABLE public.import_job
    DROP CONSTRAINT IF EXISTS fk_asset_as_planned_import_job;

-- Re-add constraint for submodel_payload
ALTER TABLE public.submodel_payload
    ADD CONSTRAINT fk_asset_as_planned
        FOREIGN KEY (asset_as_planned_id)
            REFERENCES public.assets_as_planned (id)
            ON DELETE CASCADE;

-- Re-add constraint for import_job_assets_as_planned
ALTER TABLE public.import_job_assets_as_planned
    ADD CONSTRAINT fk_assets_as_planned
        FOREIGN KEY (asset_as_planned_id)
            REFERENCES public.assets_as_planned (id)
            ON DELETE CASCADE;

-- Re-add constraint for import_job
ALTER TABLE public.import_job
    ADD CONSTRAINT fk_asset_as_planned_import_job
        FOREIGN KEY (asset_as_planned_id)
            REFERENCES public.assets_as_planned (id)
            ON DELETE CASCADE;

