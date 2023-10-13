ALTER TABLE IF EXISTS assets_as_planned
DROP COLUMN function_valid_from;

ALTER TABLE IF EXISTS assets_as_planned
    ADD COLUMN function_valid_from timestamptz;

ALTER TABLE IF EXISTS assets_as_planned
DROP COLUMN function_valid_until;

ALTER TABLE IF EXISTS assets_as_planned
    ADD COLUMN function_valid_until timestamptz;

ALTER TABLE IF EXISTS assets_as_planned
DROP COLUMN validity_period_from;

ALTER TABLE IF EXISTS assets_as_planned
    ADD COLUMN validity_period_from timestamptz;

ALTER TABLE IF EXISTS assets_as_planned
DROP COLUMN validity_period_to;

ALTER TABLE IF EXISTS assets_as_planned
    ADD COLUMN validity_period_to timestamptz;

ALTER TABLE IF EXISTS assets_as_built
DROP COLUMN manufacturing_date;

ALTER TABLE IF EXISTS assets_as_built
    ADD COLUMN manufacturing_date timestamptz;

