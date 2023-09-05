ALTER TABLE if exists assets_as_planned
    RENAME COLUMN validityPeriodFrom TO validity_period_from;

ALTER TABLE if exists assets_as_planned
    RENAME COLUMN validityPeriodTo TO validity_period_to;

ALTER TABLE if exists assets_as_planned
    RENAME COLUMN functionValidUntil TO function_valid_until;

ALTER TABLE if exists assets_as_planned
    RENAME COLUMN functionValidFrom TO function_valid_from;
