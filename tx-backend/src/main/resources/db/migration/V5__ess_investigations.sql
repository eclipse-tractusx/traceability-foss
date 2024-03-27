CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.ess_investigations
(
    id           VARCHAR(255) NOT NULL,
    part_id      VARCHAR(255) NOT NULL,
    part_name    VARCHAR(255),
    bpns         VARCHAR(255) NOT NULL,
    company_name VARCHAR(255),
    job_id       VARCHAR(255),
    status       VARCHAR(255),
    impacted     VARCHAR(255),
    response     VARCHAR(255),
    created      TIMESTAMP,
    updated      TIMESTAMP,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.ess_investigations
    ADD CONSTRAINT fk_part FOREIGN KEY (part_id)
        REFERENCES public.assets_as_planned (id);

create or replace view public.v_ess_investigations as
select '#' || row_number() over (order by i.id) as row_number,
       i.id,
       i.job_id,
       p.manufacturer_part_id,
       p.name_at_manufacturer,
       p.catenax_site_id,
       i.bpns,
       i.company_name,
       i.status,
       i.impacted,
       i.response,
       to_char(i.created, 'mm/dd/yy, hh:mi AM') AS created,
       to_char(i.updated, 'mm/dd/yy, hh:mi AM') AS updated
from public.ess_investigations as i,
     public.assets_as_planned as p
where i.part_id = p.id
;
