package org.eclipse.tractusx.traceability.common.support

import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository.JpaAlertNotificationRepository

interface AlertNotificationsRepositoryProvider {
    JpaAlertNotificationRepository jpaAlertNotificationRepository()
}
