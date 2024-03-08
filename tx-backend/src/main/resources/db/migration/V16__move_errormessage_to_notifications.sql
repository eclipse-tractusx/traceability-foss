ALTER TABLE investigation_notification ADD COLUMN error_message varchar;
ALTER TABLE alert_notification ADD COLUMN error_message varchar;

ALTER TABLE alert DROP COLUMN error_message;
ALTER TABLE investigation DROP COLUMN error_message;
