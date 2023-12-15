package org.eclipse.tractusx.traceability.integration.qualitynotification.investigation;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

class ReadInvestigationsAndTestAllPagesIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;

    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;

    private static Stream<Arguments> createdArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        new String[]{"SENT", "ACCEPTED"}
                ),
                Arguments.of(
                        1,
                        new String[]{"SENT", "ACCEPTED"}
                ),
                Arguments.of(
                        2,
                        new String[]{"ACCEPTED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createdArguments")
    void givenSmallPageSize_whenCallCreatedEndpoint_thenReturnExpectedResponse(
            final long page,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        final InvestigationNotificationEntity[] testData = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(testData);

        final InvestigationNotificationEntity[] extendedTestData = InvestigationTestDataFactory.createExtendedSenderInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(extendedTestData);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", page)
                .param("size", 2)
                .param("filter", "status,EQUAL,SENT")
                .param("filter", "status,EQUAL,ACCEPTED")
                .param("filterOperator", "AND")
                .param("sort", "createdDate,desc")
                .log().all()
                .when()
                .get("/api/investigations/created")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(5))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }

    private static Stream<Arguments> receivedArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        new String[]{"RECEIVED", "ACCEPTED"}
                ),
                Arguments.of(
                        1,
                        new String[]{"ACCEPTED", "RECEIVED"}
                ),
                Arguments.of(
                        2,
                        new String[]{"RECEIVED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("receivedArguments")
    void givenSmallPageSize_whenCallReceivedEndpoint_thenReturnExpectedResponse(
            final long page,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        final InvestigationNotificationEntity[] testData = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(testData);

        final InvestigationNotificationEntity[] extendedTestData = InvestigationTestDataFactory.createExtendedReceiverInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(extendedTestData);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", page)
                .param("size", 2)
                .param("filter", "status,EQUAL,RECEIVED")
                .param("filter", "status,EQUAL,ACCEPTED")
                .param("filterOperator", "AND")
                .param("sort", "createdDate,desc")
                .log().all()
                .when()
                .get("/api/investigations/received")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(5))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }
}
