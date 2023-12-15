-- public.submodel_payload definition

-- Drop table

-- Drop TABLE public.submodel_payload

CREATE TABLE public.submodel_payload
(
    id       varchar(255) NOT NULL,
    json     varchar      NULL,
    CONSTRAINT submodel_payload_pkey PRIMARY KEY (id)
);


-- public.assets_as_built_submodel_payload definition

-- Drop table

-- DROP TABLE public.assets_as_built_submodel_payload;

CREATE TABLE public.assets_as_built_submodel_payload
(
    submodel_payload_id varchar(255) NOT NULL,
    asset_id            varchar(255) NOT NULL,
    CONSTRAINT fk_submodel_payload FOREIGN KEY (submodel_payload_id) REFERENCES public.submodel_payload (id),
    CONSTRAINT fk_asset_entity FOREIGN KEY (asset_id) REFERENCES public.assets_as_built (id)
);

-- public.assets_as_planned_submodel_payload definition

-- Drop table

-- DROP TABLE public.assets_as_planned_submodel_payload;

CREATE TABLE public.assets_as_planned_submodel_payload
(
    submodel_payload_id varchar(255) NOT NULL,
    asset_id            varchar(255) NOT NULL,
    CONSTRAINT fk_submodel_payload FOREIGN KEY (submodel_payload_id) REFERENCES public.submodel_payload (id),
    CONSTRAINT fk_asset_entity FOREIGN KEY (asset_id) REFERENCES public.assets_as_planned (id)
);
