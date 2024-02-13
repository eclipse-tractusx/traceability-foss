-- public.import_job definition

-- Drop table

-- Drop TABLE public.import_job

CREATE TABLE public.import_job
(
    id                  varchar(255) NOT NULL,
    started_on          timestamp NULL,
    completed_on        timestamp NULL,
    import_job_status   varchar(255) NOT NULL,
    asset_as_built_id   VARCHAR(255) NULL,
    asset_as_planned_id VARCHAR(255) NULL,
    CONSTRAINT import_job_pkey PRIMARY KEY (id),
    CONSTRAINT fk_asset_as_planned_import_job FOREIGN KEY (asset_as_planned_id) REFERENCES public.assets_as_planned (id)
);


CREATE TABLE public.import_job_assets_as_built
(
    import_job_id     VARCHAR(255) NOT NULL,
    asset_as_built_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_import_job FOREIGN KEY (import_job_id) REFERENCES public.import_job (id),
    CONSTRAINT fk_assets_as_built FOREIGN KEY (asset_as_built_id) REFERENCES public.assets_as_built (id)
);

CREATE TABLE public.import_job_assets_as_planned
(
    import_job_id       VARCHAR(255) NOT NULL,
    asset_as_planned_id VARCHAR(255) NOT NULL,
    CONSTRAINT fk_import_job FOREIGN KEY (import_job_id) REFERENCES public.import_job (id),
    CONSTRAINT fk_assets_as_planned FOREIGN KEY (asset_as_planned_id) REFERENCES public.assets_as_planned (id)
);
