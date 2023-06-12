package org.eclipse.tractusx.traceability.common.support

import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity

trait AlertNotificationsSupport implements AlertNotificationsRepositoryProvider {

    AlertNotificationEntity storedAlertNotification(AlertNotificationEntity notification) {
        return jpaAlertNotificationRepository().save(notification)
    }

    void storedAlertNotifications(AlertNotificationEntity... notifications) {
        notifications.each {
            storedAlertNotification(it)
        }
    }

    void assertAlertNotificationsSize(int size) {
        List<AlertNotificationEntity> notifications = jpaAlertNotificationRepository().findAll()

        assert notifications.size() == size
    }

    void assertAlertNotifications(Closure closure) {
        jpaAlertNotificationRepository().findAll().each closure
    }

}
