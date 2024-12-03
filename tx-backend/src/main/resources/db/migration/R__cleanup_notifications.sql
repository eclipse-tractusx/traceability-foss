DO $$
    DECLARE
        cleanupNotifications BOOLEAN := '${cleanupNotifications}';
    BEGIN
        IF cleanupNotifications THEN
            TRUNCATE notification CASCADE;
        END IF;
    END $$;
