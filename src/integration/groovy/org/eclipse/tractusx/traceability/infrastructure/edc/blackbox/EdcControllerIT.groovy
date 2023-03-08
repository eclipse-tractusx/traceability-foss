package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox


import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.security.JwtRole
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.common.support.NotificationsSupport
import org.eclipse.tractusx.traceability.common.support.TestDataSupport
import org.eclipse.tractusx.traceability.infrastructure.jpa.investigation.InvestigationEntity
import org.eclipse.tractusx.traceability.infrastructure.jpa.notification.NotificationEntity
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus

import java.time.Instant

import static io.restassured.RestAssured.given

class EdcControllerIT extends IntegrationSpecification implements TestDataSupport, NotificationsSupport, InvestigationsSupport {

	def "should call the /qualitynotifications/receive api successfully "() {
		given:

		NotificationEntity notification = new NotificationEntity(
			null,
			"senderBpnNumber",
			"receiverBpnNumber",
			null,
			"notificationReferenceId"
		)
		notification.setEdcUrl("http://example.com")
		notification.setContractAgreementId("contractAgreementId")

		InvestigationEntity investigation = new InvestigationEntity(
			[], "BPNL00000003AXS3", InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "some-description", Instant.now())

		InvestigationEntity investigationEntity = storedInvestigationFullObject(investigation)

		NotificationEntity notificationEntity = storedNotification(notification)

		notificationEntity.setInvestigation(investigationEntity)
		storedNotification(notificationEntity)

		when:
		given()
			.contentType(ContentType.JSON)
			.body("{\n" +
				"\t\"header\": {\n" +
				"\t\t\"notificationId\": \"notificationReferenceId\",\n" +
				"\t\t\"senderBPN\": \"BPNL00000003AXS3\",\n" +
				"\t\t\"senderAddress\": \"https://some-url.com\",\n" +
				"\t\t\"recipientBPN\": \"OTHER_BPN\",\n" +
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
			.post("/api/qualitynotifications/receive")
			.then()
			.statusCode(200)

		then:
		eventually {
			assertNotificationsSize(1)
			assertInvestigationsSize(1)
			assertInvestigationStatus(InvestigationStatus.CLOSED)
		}


	}

	def "should call the /qualitynotifications/receive api with wrong requestobject "() {
		given:

		NotificationEntity notification = new NotificationEntity(
			null,
			"senderBpnNumber",
			"receiverBpnNumber",
			null,
			"notificationReferenceId"
		)
		notification.setEdcUrl("http://example.com")
		notification.setContractAgreementId("contractAgreementId")

		InvestigationEntity investigation = new InvestigationEntity(
			[], "BPNL00000003AXS3", InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "some-description", Instant.now())

		InvestigationEntity investigationEntity = storedInvestigationFullObject(investigation)

		NotificationEntity notificationEntity = storedNotification(notification)

		notificationEntity.setInvestigation(investigationEntity)
		storedNotification(notificationEntity)

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
			.post("/api/qualitynotifications/receive")
			.then()
			.statusCode(400)

		then:
		eventually {
			assertNotificationsSize(1)
			assertInvestigationsSize(1)
			assertInvestigationStatus(InvestigationStatus.RECEIVED)
		}


	}

	def "should call the /qualitynotifications/update api successfully "() {
		given:

		NotificationEntity notification = new NotificationEntity(
				null,
				"senderBpnNumber",
				"receiverBpnNumber",
				null,
				"notificationReferenceId"
		)
		notification.setEdcUrl("http://example.com")
		notification.setContractAgreementId("contractAgreementId")

		InvestigationEntity investigation = new InvestigationEntity(
				[], "BPNL00000003AXS3", InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "some-description", Instant.now())

		InvestigationEntity investigationEntity = storedInvestigationFullObject(investigation)

		NotificationEntity notificationEntity = storedNotification(notification)

		notificationEntity.setInvestigation(investigationEntity)
		storedNotification(notificationEntity)

		when:
		given()
				.contentType(ContentType.JSON)
				.body("{\n" +
						"\t\"header\": {\n" +
						"\t\t\"notificationId\": \"notificationReferenceId\",\n" +
						"\t\t\"senderBPN\": \"BPNL00000003AXS3\",\n" +
						"\t\t\"senderAddress\": \"https://some-url.com\",\n" +
						"\t\t\"recipientBPN\": \"OTHER_BON\",\n" +
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
				.statusCode(200)

		then:
		eventually {
			assertNotificationsSize(1)
			assertInvestigationsSize(1)
			assertInvestigationStatus(InvestigationStatus.CLOSED)
		}


	}

	def "should call the /qualitynotifications/update api with wrong requestobject "() {
		given:

		NotificationEntity notification = new NotificationEntity(
				null,
				"senderBpnNumber",
				"receiverBpnNumber",
				null,
				"notificationReferenceId"
		)
		notification.setEdcUrl("http://example.com")
		notification.setContractAgreementId("contractAgreementId")

		InvestigationEntity investigation = new InvestigationEntity(
				[], "BPNL00000003AXS3", InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "some-description", Instant.now())

		InvestigationEntity investigationEntity = storedInvestigationFullObject(investigation)

		NotificationEntity notificationEntity = storedNotification(notification)

		notificationEntity.setInvestigation(investigationEntity)
		storedNotification(notificationEntity)

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
		eventually {
			assertNotificationsSize(1)
			assertInvestigationsSize(1)
			assertInvestigationStatus(InvestigationStatus.RECEIVED)
		}
	}


	def "should call the /qualitynotifications/update api successfully by receiver"() {
		given:

		NotificationEntity notification = new NotificationEntity(
			null,
			"senderBpnNumber",
			"receiverBpnNumber",
			null,
			"notificationReferenceId"
		)
		notification.setEdcUrl("http://example.com")
		notification.setContractAgreementId("contractAgreementId")

		InvestigationEntity investigation = new InvestigationEntity(
			[], "BPNL00000003AXS3", InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "some-description", Instant.now())

		InvestigationEntity investigationEntity = storedInvestigationFullObject(investigation)

		NotificationEntity notificationEntity = storedNotification(notification)

		notificationEntity.setInvestigation(investigationEntity)
		storedNotification(notificationEntity)

		when:
		given()
			.contentType(ContentType.JSON)
			.body("{\n" +
				"\t\"header\": {\n" +
				"\t\t\"notificationId\": \"notificationReferenceId\",\n" +
				"\t\t\"senderBPN\": \"OTHER_BPN\",\n" +
				"\t\t\"senderAddress\": \"https://some-url.com\",\n" +
				"\t\t\"recipientBPN\": \"BPNL00000003AXS3\",\n" +
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
			.statusCode(200)

		then:
		eventually {
			assertNotificationsSize(1)
			assertInvestigationsSize(1)
			assertInvestigationStatus(InvestigationStatus.CLOSED)
		}


	}

}
