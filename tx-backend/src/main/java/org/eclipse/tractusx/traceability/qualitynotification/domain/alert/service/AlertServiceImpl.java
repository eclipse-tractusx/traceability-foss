package org.eclipse.tractusx.traceability.qualitynotification.domain.alert.service;

import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.application.alert.service.AlertService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AlertServiceImpl implements AlertService {

    @Override
    public QualityNotificationId start(List<String> partIds, String description, Instant targetDate, String severity) {
        return null;
    }

    @Override
    public PageResult<QualityNotification> getCreated(Pageable pageable) {
        return null;
    }

    @Override
    public PageResult<QualityNotification> getReceived(Pageable pageable) {
        return null;
    }

    @Override
    public QualityNotification find(Long notificationId) {
        return null;
    }

    @Override
    public QualityNotification loadOrNotFoundException(QualityNotificationId notificationId) {
        return null;
    }

    @Override
    public QualityNotification loadByEdcNotificationIdOrNotFoundException(String edcNotificationId) {
        return null;
    }

    @Override
    public void approve(Long notificationId) {

    }

    @Override
    public void cancel(Long notificationId) {

    }

    @Override
    public void update(Long notificationId, QualityNotificationStatus notificationStatus, String reason) {

    }
}
