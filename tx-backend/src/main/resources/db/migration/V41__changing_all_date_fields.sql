ALTER TABLE IF EXISTS assets_as_planned
DROP COLUMN function_valid_from;

ALTER TABLE IF EXISTS assets_as_planned
    ADD COLUMN function_valid_from timestamptz;
