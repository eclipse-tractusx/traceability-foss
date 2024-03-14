ALTER TABLE investigation_notification DROP COLUMN is_initial;
ALTER TABLE alert_notification DROP COLUMN is_initial;

ALTER TABLE investigation_notification DROP COLUMN edc_url;
ALTER TABLE alert_notification DROP COLUMN edc_url;
