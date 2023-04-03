package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.investigations.domain.model.AffectedPart;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity;

import java.time.Instant;
import java.util.List;

public class NotificationTestDataFactory {

    public static Notification createNotificationTestData() {
        List<AffectedPart> affectedParts = List.of(new AffectedPart("partId"));
        return new Notification(
                "123",
                "id123",
                "senderBPN",
                "senderManufacturerName",
                "recipientBPN",
                "receiverManufacturerName",
                "senderAddress",
                "agreement",
                "information",
                InvestigationStatus.ACKNOWLEDGED,
                affectedParts,
                Instant.parse("2022-03-01T12:00:00Z"),
                Severity.MINOR,
                "123",
                null,
                null
        );
    }
}
