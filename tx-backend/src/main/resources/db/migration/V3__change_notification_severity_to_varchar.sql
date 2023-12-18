ALTER TABLE investigation_notification DROP COLUMN severity;
ALTER TABLE investigation_notification ADD COLUMN severity varchar(255) NULL;
ALTER TABLE alert_notification DROP COLUMN severity;
ALTER TABLE alert_notification ADD COLUMN severity varchar(255) NULL;
