package org.eclipse.tractusx.traceability.common.support

import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository.JpaAlertRepository

interface AlertsRepositoryProvider {
    JpaAlertRepository jpaAlertRepository()
}
