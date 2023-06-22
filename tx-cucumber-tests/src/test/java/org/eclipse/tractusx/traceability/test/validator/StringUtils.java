package org.eclipse.tractusx.traceability.test.validator;

import java.time.Instant;

public class StringUtils {

    private static final String UNIQUE_SEPARATOR = ":-:";

    public static String wrapStringWithTimestamp(String string) {

        return UNIQUE_SEPARATOR + string + UNIQUE_SEPARATOR + Instant.now();
    }

    public static String unWrapStringWithTimestamp(String string) {
        String[] array = string.split(UNIQUE_SEPARATOR);
        return array[1];
    }
}
