DROP TABLE public.assets_as_built_submodel_payload;
DROP TABLE public.assets_as_planned_submodel_payload;
Drop TABLE public.submodel_payload;

-- public.submodel_payload definition

-- Drop table

-- Drop TABLE public.submodel_payload

CREATE TABLE public.submodel_payload
(
    id               VARCHAR(255) NOT NULL,
    json             VARCHAR      NULL,
    aspect_type      VARCHAR(255),
    asset_as_built_id   VARCHAR(255) NULL,
    asset_as_planned_id VARCHAR(255) NULL,
    CONSTRAINT submodel_payload_pkey PRIMARY KEY (id),
    CONSTRAINT fk_asset_as_built FOREIGN KEY (asset_as_built_id) REFERENCES public.assets_as_built (id),
    CONSTRAINT fk_asset_as_planned FOREIGN KEY (asset_as_planned_id) REFERENCES public.assets_as_planned (id)
);
