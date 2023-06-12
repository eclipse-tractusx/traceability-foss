package org.eclipse.tractusx.traceability.common.support

import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity

import java.time.Instant

trait AlertsSupport implements AlertsRepositoryProvider {

    Long defaultReceivedAlertStored() {
        AlertEntity entity = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .description("some description")
                .createdDate(Instant.now())
                .build();

        return storedAlert(entity)
    }

    Long defaultAcknowledgedAlertStored() {
        AlertEntity entity = AlertEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(QualityNotificationStatusBaseEntity.ACKNOWLEDGED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .createdDate(Instant.now())
                .build();

        return storedAlert(entity)
    }

    void assertAlertsSize(int size) {
        List<AlertEntity> alerts = jpaAlertRepository().findAll()

        assert alerts.size() == size
    }

    void assertAlertStatus(QualityNotificationStatus alertStatus) {
        jpaAlertRepository().findAll().each {
            assert it.status.name() == alertStatus.name()
        }
    }

    void storedAlerts(AlertEntity... alerts) {
        alerts.each {
            jpaAlertRepository().save(it)
        }
    }

    Long storedAlert(AlertEntity alert) {
        return jpaAlertRepository().save(alert).id
    }

    AlertEntity storedAlertFullObject(AlertEntity alert) {
        return jpaAlertRepository().save(alert);
    }
}
