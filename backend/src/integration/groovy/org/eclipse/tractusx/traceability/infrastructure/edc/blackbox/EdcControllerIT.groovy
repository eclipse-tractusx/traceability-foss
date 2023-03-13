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
import org.springframework.beans.factory.annotation.Autowired

import java.time.Instant

import static io.restassured.RestAssured.given

class EdcControllerIT extends IntegrationSpecification implements TestDataSupport, AssetsSupport, NotificationsSupport, InvestigationsSupport {
	@Autowired
	private ObjectMapper objectMapper

	def "should create a investigation including notification on API callback /qualitynotifications/receive success"() {
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

	def "should not create a investigation on API callback /qualitynotifications/receive bad request wrong bpn"() {
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
			null,
			"senderBpnNumber",
			"receiverBpnNumber",
			null,
			null,
			Instant.parse("2022-03-01T12:00:00Z")
		)

		InvestigationEntity investigation = new InvestigationEntity(
			[], "BPNL00000003AXS3", InvestigationStatus.SENT, InvestigationSide.SENDER, "", "some-description", Instant.now())
		List<NotificationEntity> notificationEntities = new ArrayList<>()
		InvestigationEntity persistedInvestigation = storedInvestigationFullObject(investigation)


		NotificationEntity notificationEntity = storedNotification(notification)
		notification.setInvestigation(persistedInvestigation);
		storedNotification(notificationEntity)
		String notificationId = notificationEntity.getId()

		String notificationJson = readFile("edc_notification_okay_update.json").replaceAll("REPLACE_ME", notificationId)
		EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

		notificationEntities.add(notificationEntity)
		investigation.setNotifications(notificationEntities)

		storedInvestigationFullObject(investigation)

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
		// todo clarify the target status of this case
		//assertInvestigationStatus(InvestigationStatus.ACCEPTED)
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
