/* Delete View with relations to removed fields */
DROP VIEW assets_as_built_view;

/* Remove fields which are not needed anymore */
ALTER TABLE public.assets_as_built DROP COLUMN IF EXISTS active_alert;
ALTER TABLE public.assets_as_built DROP COLUMN IF EXISTS in_investigation;

ALTER TABLE public.assets_as_planned DROP COLUMN IF EXISTS active_alert;
ALTER TABLE public.assets_as_planned DROP COLUMN IF EXISTS in_investigation;
