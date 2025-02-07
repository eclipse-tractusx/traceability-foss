package org.eclipse.tractusx.traceability.integration.notification.alert;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.testdata.AlertTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

class ReadAlertsAndTestAllPagesIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;

    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    private static Stream<Arguments> createdArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        "status,EQUAL,SENT,AND",
                        "status,EQUAL,CREATED,AND",
                        new String[]{"SENT", "SENT"}
                ),
                Arguments.of(
                        1,
                        "status,EQUAL,SENT,AND",
                        "status,EQUAL,CREATED,AND",
                        new String[]{"CREATED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createdArguments")
    void givenSmallPageSize_whenCallCreatedEndpoint_thenReturnExpectedResponse(
            final int page,
            final String filter1,
            final String filter2,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {
        // Given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "createdDate,DESC";

        final NotificationMessageEntity[] testData = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final NotificationMessageEntity[] extendedTestData = AlertTestDataFactory.createExtendedSenderAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(page, 2, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString, filter1, filter2))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(3))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }

    private static Stream<Arguments> receivedArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        "status,EQUAL,RECEIVED,AND",
                        "status,EQUAL,ACCEPTED,AND",
                        new String[]{"RECEIVED", "RECEIVED"}
                ),
                Arguments.of(
                        1,
                        "status,EQUAL,RECEIVED,AND",
                        "status,EQUAL,ACCEPTED,AND",
                        new String[]{"ACCEPTED", "ACCEPTED"}
                ),
                Arguments.of(
                        2,
                        "status,EQUAL,RECEIVED,AND",
                        "status,EQUAL,ACCEPTED,AND",
                        new String[]{"ACCEPTED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("receivedArguments")
    void givenSmallPageSize_whenCallReceivedEndpoint_thenReturnExpectedResponse(
            final int page,
            final String filter1,
            final String filter2,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        String filterString = "channel,EQUAL,RECEIVER,AND";
        String sortString = "createdDate,DESC";

        final NotificationMessageEntity[] testData = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final NotificationMessageEntity[] extendedTestData = AlertTestDataFactory.createExtendedReceiverAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(page, 2, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString, filter1, filter2))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(5))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }
}
