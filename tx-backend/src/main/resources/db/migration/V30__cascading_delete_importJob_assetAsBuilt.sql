ALTER TABLE public.import_job_assets_as_built
    DROP CONSTRAINT IF EXISTS fk_import_job;

ALTER TABLE public.import_job_assets_as_built
    ADD CONSTRAINT fk_import_job
        FOREIGN KEY (import_job_id)
            REFERENCES public.import_job (id)
            ON DELETE CASCADE;


ALTER TABLE public.import_job_assets_as_planned
    DROP CONSTRAINT IF EXISTS fk_import_job;

ALTER TABLE public.import_job_assets_as_planned
    ADD CONSTRAINT fk_import_job
        FOREIGN KEY (import_job_id)
            REFERENCES public.import_job (id)
            ON DELETE CASCADE;


ALTER TABLE public.import_job
    DROP CONSTRAINT IF EXISTS fk_assets_as_built;

ALTER TABLE public.import_job
    ADD CONSTRAINT fk_assets_as_built
        FOREIGN KEY (asset_as_built_id)
            REFERENCES public.assets_as_built (id)
            ON DELETE CASCADE;


ALTER TABLE public.import_job
    DROP CONSTRAINT IF EXISTS fk_asset_as_planned_import_job;

ALTER TABLE public.import_job
    ADD CONSTRAINT fk_asset_as_planned_import_job
        FOREIGN KEY (asset_as_planned_id)
            REFERENCES public.assets_as_planned (id)
            ON DELETE CASCADE;
