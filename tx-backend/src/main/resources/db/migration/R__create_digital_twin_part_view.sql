-- ${flyway:timestamp}
drop view if exists digital_twin_part_view;

create or replace view digital_twin_part_view as
select
            row_number() over ()                             as id, -- synthetic unique id
            aas.aas_id                                       as aas_id,
            aas.expiration                                   as aas_expiration_date,
            aas.global_asset_id                              as global_asset_id,
            coalesce(ab.expiration_date, ap.expiration_date) as asset_expiration_date,
            coalesce(ab.ttl, ap.ttl)                         as asset_ttl,
            aas.bpn                                          as bpn,
            aas.digital_twin_type                            as digital_twin_type,
            aas.ttl                                          as aas_ttl,
            aas.actor                                        as actor
from aas
         left join assets_as_built ab on aas.global_asset_id = ab.id
         left join assets_as_planned ap on aas.global_asset_id = ap.id;
