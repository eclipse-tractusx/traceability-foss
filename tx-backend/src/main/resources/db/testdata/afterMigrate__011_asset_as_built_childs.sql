-- this script is executed by flyway as part of a lifecycle hook after all migrations have been applied
-- (see https://documentation.red-gate.com/fd/callback-concept-184127466.html).
-- it is only intended for usage in local or test environments

insert into assets_as_built_childs
    (asset_as_built_id  , id                 , id_short)
values
    -- owner is child of customer
    (${assetAsBuiltId01}, ${assetAsBuiltId03}, '01'), -- Xenon Left-Headlights isChildOf Osram Left-AX400
    (${assetAsBuiltId01}, ${assetAsBuiltId05}, '02'), -- Xenon Left-Headlights isChildOf Xenon Vision Left-D3R
    (${assetAsBuiltId02}, ${assetAsBuiltId04}, '03'), -- Xenon Right-Headlights isChildOf Osram Right-AX400
    (${assetAsBuiltId02}, ${assetAsBuiltId06}, '04'), -- Xenon Right-Headlights isChildOf Xenon Vision Right-D3R

    -- customer is child of owner
    (${assetAsBuiltId07}, ${assetAsBuiltId01}, '05'), -- BMW Z1 isChildOf Xenon Left-Headlights
    (${assetAsBuiltId07}, ${assetAsBuiltId02}, '06'); -- BMW Z1 isChildOf Xenon Right-Headlights
