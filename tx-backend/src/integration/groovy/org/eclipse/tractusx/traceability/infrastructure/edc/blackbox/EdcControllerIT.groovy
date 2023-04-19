package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox

import com.fasterxml.jackson.databind.ObjectMapper
import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.security.JwtRole
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.common.support.NotificationsSupport
import org.eclipse.tractusx.traceability.common.support.TestDataSupport
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification
import org.eclipse.tractusx.traceability.infrastructure.jpa.investigation.InvestigationEntity
import org.eclipse.tractusx.traceability.infrastructure.jpa.notification.NotificationEntity
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity
import org.springframework.beans.factory.annotation.Autowired

import java.time.Instant

import static io.restassured.RestAssured.given

class EdcControllerIT extends IntegrationSpecification implements TestDataSupport, AssetsSupport, NotificationsSupport, InvestigationsSupport {
    @Autowired
    private ObjectMapper objectMapper

    def "should create an investigation including notification on API callclass EdcControllerIT extends IntegrationSpecification implements TestDataSupport, AssetsSupport, NotificationsSupport, InvestigationsSupport {\nback /qualitynotifications/receive success"() {
        given:
        defaultAssetsStored()
        String notificationJson = readFile("edc_notification_okay.json")
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        when:
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/receive")
                .then()
                .statusCode(200)

        then:
        assertNotificationsSize(1)
        assertInvestigationsSize(1)
        assertInvestigationStatus(InvestigationStatus.RECEIVED)
    }

    def "should not create an investigation on API callback /qualitynotifications/receive bad request bpn of notification does not match app bpn"() {
        given:
        String notificationJson = readFile("edc_notification_wrong_bpn.json")
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);
        when:
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/receive")
                .then()
                .statusCode(400)

        then:
        assertNotificationsSize(0)
        assertInvestigationsSize(0)
    }

    def "should add a notification to an existing investigation on API callback /qualitynotifications/update success"() {
        given:
        defaultAssetsStored()
        NotificationEntity notification = new NotificationEntity(
                "1",
                null,
                "senderBpnNumber",
                "senderManufacturerName",
                "receiverBpnNumber",
                "receiverManufacturerName",
                null,
                null,
                Instant.parse("2022-03-01T12:00:00Z"),
                Severity.CRITICAL,
                "cda2d956-fa91-4a75-bb4a-8e5ba39b268a",
                null,
                "messageId"
        )

        InvestigationEntity investigation = new InvestigationEntity(
                [], "BPNL00000003AXS3", InvestigationStatus.SENT, InvestigationSide.SENDER, "", "some-description", Instant.now())

        InvestigationEntity persistedInvestigation = storedInvestigationFullObject(investigation)

        NotificationEntity notificationEntity = storedNotification(notification)
        notificationEntity.setInvestigation(persistedInvestigation);
        NotificationEntity persistedNotification = storedNotification(notificationEntity)

        investigation.setNotifications(List.of(persistedNotification))

        storedInvestigationFullObject(investigation)


        String notificationJson = readFile("edc_notification_okay_update.json").replaceAll("REPLACE_ME", notificationEntity.getEdcNotificationId())
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);


        when:
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/update")
                .then()
                .statusCode(200)

        then:
        assertNotificationsSize(2)
        assertInvestigationsSize(1)

        assertInvestigationStatus(InvestigationStatus.ACKNOWLEDGED)
    }

    def "should throw bad request because edcNotification Method is not supported /qualitynotifications/receive"() {
        given:
        defaultAssetsStored()
        NotificationEntity notification = new NotificationEntity(
                "1",
                null,
                "senderBpnNumber",
                "senderManufacturerName",
                "receiverBpnNumber",
                "receiverManufacturerName",
                null,
                null,
                Instant.parse("2022-03-01T12:00:00Z"),
                Severity.CRITICAL,
                "cda2d956-fa91-4a75-bb4a-8e5ba39b268a",
                null,
                "messageId"
        )

        InvestigationEntity investigation = new InvestigationEntity(
                [], "BPNL00000003AXS3", InvestigationStatus.SENT, InvestigationSide.SENDER, "", "some-description", Instant.now())

        InvestigationEntity persistedInvestigation = storedInvestigationFullObject(investigation)

        NotificationEntity notificationEntity = storedNotification(notification)
        notificationEntity.setInvestigation(persistedInvestigation);
        NotificationEntity persistedNotification = storedNotification(notificationEntity)

        investigation.setNotifications(List.of(persistedNotification))

        storedInvestigationFullObject(investigation)


        String notificationJson = readFile("edc_notification_classification_unsupported.json").replaceAll("REPLACE_ME", notificationEntity.getEdcNotificationId())
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);


        when:
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/receive")
                .then()
                .statusCode(400)

        then:
        assertNotificationsSize(1)

    }

    def "should call the /qualitynotifications/update api with wrong requestobject "() {
        given:

        InvestigationEntity investigation = new InvestigationEntity(
                [], "BPNL00000003AXS3", InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "some-description", Instant.now())

        storedInvestigationFullObject(investigation)

        when:
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "\t\"header\": {\n" +
                        "\t\t\"notificationId\": \"notificationReferenceId\",\n" +
                        "\t\t\"senderBPN\": \"NOT_SAME_AS_APP_BPN\",\n" +
                        "\t\t\"senderAddress\": \"https://some-url.com\",\n" +
                        "\t\t\"recipientBPN\": \"NOT_SAME_AS_APP_BPN\",\n" +
                        "\t\t\"classification\": \"QM-Investigation\",\n" +
                        "\t\t\"severity\": \"CRITICAL\",\n" +
                        "\t\t\"relatedNotificationId\": \"\",\n" +
                        "\t\t\"status\": \"CLOSED\",\n" +
                        "\t\t\"targetDate\": \"\"\n" +
                        "\t},\n" +
                        "\t\"content\": {\n" +
                        "\t\t\"information\": \"Some long description\",\n" +
                        "\t\t\"listOfAffectedItems\": [\n" +
                        "\t\t\t\"urn:uuid:171fed54-26aa-4848-a025-81aaca557f37\"\n" +
                        "\t\t]\n" +
                        "\t}\n" +
                        "}")
                .header(jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/qualitynotifications/update")
                .then()
                .statusCode(400)

        then:
        assertNotificationsSize(0)
        assertInvestigationsSize(1)
        assertInvestigationStatus(InvestigationStatus.RECEIVED)

    }
}
