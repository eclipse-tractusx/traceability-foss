package org.eclipse.tractusx.traceability.qualitynotification.domain.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Set.of;

public enum QualityNotificationStatus {
    CREATED(QualityNotificationSide.SENDER, emptySet()),
    SENT(QualityNotificationSide.SENDER, Set.of(QualityNotificationSide.SENDER)),
    RECEIVED(QualityNotificationSide.RECEIVER, emptySet()),
    ACKNOWLEDGED(QualityNotificationSide.RECEIVER, Set.of(QualityNotificationSide.RECEIVER, QualityNotificationSide.SENDER)),
    ACCEPTED(QualityNotificationSide.RECEIVER, Set.of(QualityNotificationSide.RECEIVER)),
    DECLINED(QualityNotificationSide.RECEIVER, Set.of(QualityNotificationSide.RECEIVER)),
    CANCELED(QualityNotificationSide.SENDER, Set.of(QualityNotificationSide.SENDER)),
    CLOSED(QualityNotificationSide.SENDER, of(QualityNotificationSide.SENDER, QualityNotificationSide.RECEIVER));

    private static final Map<QualityNotificationStatus, Set<QualityNotificationStatus>> STATE_MACHINE;
    private static final Set<QualityNotificationStatus> NO_TRANSITION_ALLOWED = emptySet();
    private static final Map<String, QualityNotificationStatus> MAPPINGS;

    static {
        STATE_MACHINE = Map.of(
                CREATED, of(SENT, CANCELED),
                SENT, of(RECEIVED, CLOSED, ACKNOWLEDGED),
                RECEIVED, of(ACKNOWLEDGED, CLOSED),
                ACKNOWLEDGED, of(DECLINED, ACCEPTED, CLOSED),
                ACCEPTED, of(CLOSED),
                DECLINED, of(CLOSED),
                CLOSED, NO_TRANSITION_ALLOWED,
                CANCELED, NO_TRANSITION_ALLOWED
        );

        MAPPINGS = Arrays.stream(QualityNotificationStatus.values())
                .collect(Collectors.toMap(Enum::name, qualityNotificationStatus -> qualityNotificationStatus));
    }

    private final QualityNotificationSide qualityNotificationSide;
    private final Set<QualityNotificationSide> allowedTransitionFromSide;

    QualityNotificationStatus(QualityNotificationSide qualityNotificationSide, Set<QualityNotificationSide> allowedTransitionFromSide) {
        this.qualityNotificationSide = qualityNotificationSide;
        this.allowedTransitionFromSide = allowedTransitionFromSide;
    }

    public static Optional<QualityNotificationStatus> fromValue(String value) {
        return Optional.ofNullable(MAPPINGS.get(value));
    }

    public static QualityNotificationStatus fromStringValue(String value) {
        return MAPPINGS.get(value);
    }

    public boolean transitionAllowed(QualityNotificationStatus to) {

        Set<QualityNotificationStatus> allowedStatusesToTransition = STATE_MACHINE.get(this);

        if (!allowedStatusesToTransition.contains(to)) {
            return false;
        }

        return isSideEligibleForTransition(this, to);
    }

    private boolean isSideEligibleForTransition(QualityNotificationStatus from, QualityNotificationStatus to) {
        return to.allowedTransitionFromSide.contains(from.qualityNotificationSide);
    }
}
