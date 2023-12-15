package org.eclipse.tractusx.traceability.qualitynotification.application.alert.response;

import org.eclipse.tractusx.traceability.qualitynotification.application.alert.mapper.AlertResponseMapper;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.testdata.NotificationTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import qualitynotification.alert.response.AlertResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlertResponseMapperTest {
    QualityNotification qualityNotification;

    @BeforeEach
    void beforeEach() {
        qualityNotification = NotificationTestDataFactory.createQualityNotificationTestData();
    }

    @Test
    void testCorrectBpn_from() {
        AlertResponse alertResponse = AlertResponseMapper.from(qualityNotification);

        assertEquals("BPN01", alertResponse.getCreatedBy());
        assertEquals("BPN02", alertResponse.getSendTo());
    }

    @Test
    void testCorrectName_from() {
        AlertResponse alertResponse = AlertResponseMapper.from(qualityNotification);

        assertEquals("Company1", alertResponse.getCreatedByName());
        assertEquals("Company2", alertResponse.getSendToName());
    }
}
