ALTER TABLE public.assets_as_built DROP COLUMN IF EXISTS active_alert CASCADE;
ALTER TABLE public.assets_as_built DROP COLUMN IF EXISTS in_investigation CASCADE;

ALTER TABLE public.assets_as_planned DROP COLUMN IF EXISTS active_alert CASCADE;
ALTER TABLE public.assets_as_planned DROP COLUMN IF EXISTS in_investigation CASCADE;

