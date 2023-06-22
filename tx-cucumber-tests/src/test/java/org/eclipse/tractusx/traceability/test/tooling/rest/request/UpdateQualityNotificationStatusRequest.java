package org.eclipse.tractusx.traceability.test.tooling.rest.request;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.NoSuchElementException;
import java.util.stream.Stream;

public enum UpdateQualityNotificationStatusRequest {
    ACKNOWLEDGED,
    ACCEPTED,
    DECLINED;


    @JsonCreator
    public static UpdateQualityNotificationStatusRequest fromValue(final String value) {
        return Stream.of(UpdateQualityNotificationStatusRequest.values())
                .filter(updateInvestigationStatus -> updateInvestigationStatus.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Unsupported UpdateInvestigationStatus"));
    }


}
