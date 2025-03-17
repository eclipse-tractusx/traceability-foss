package org.eclipse.tractusx.traceability.assets.infrastructure.base.model;

import static org.eclipse.tractusx.traceability.common.security.Sanitizer.sanitize;

public enum JobState {
    UNSAVED,
    INITIAL,
    RUNNING,
    TRANSFERS_FINISHED,
    COMPLETED,
    CANCELED,
    ERROR;

    /**
     * Sanitizes the input state string and returns a valid JobState.
     * If the input is null or invalid, it defaults to ERROR.
     *
     * @param state the input state string
     * @return a valid JobState
     */
    public static JobState fromString(String state) {
        if (state == null || state.isEmpty()) {
            return null;
        }
        try {
            return JobState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ProcessingState value: " + sanitize(state));
        }
    }
}
