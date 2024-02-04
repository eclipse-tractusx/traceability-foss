package org.eclipse.tractusx.traceability.integration.qualitynotification.alert;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
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

   /* private static Stream<Arguments> createdArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        new String[]{"SENT", "SENT"}
                ),

                Arguments.of(
                        1,
                        new String[]{"CREATED"}
                )
        );
    }*/

/*    @ParameterizedTest
    @MethodSource("createdArguments")
    void givenSmallPageSize_whenCallCreatedEndpoint_thenReturnExpectedResponse(
            final int page,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        final AlertNotificationEntity[] testData = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final AlertNotificationEntity[] extendedTestData = AlertTestDataFactory.createExtendedSenderAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", page)
                .param("size", 2)
                .param("filter", "status,EQUAL,SENT,AND")
                .param("filter", "status,EQUAL,CREATED,AND")
                .param("sort", "createdDate,desc")
                .log().all()
                .when()
                .get("/api/alerts/created")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(3))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }*/

   /* private static Stream<Arguments> receivedArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        new String[]{"RECEIVED", "RECEIVED"}
                ),

                Arguments.of(
                        1,
                        new String[]{"ACCEPTED", "ACCEPTED"}
                ),
                Arguments.of(
                        2,
                        new String[]{"ACCEPTED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("receivedArguments")
    void givenSmallPageSize_whenCallReceivedEndpoint_thenReturnExpectedResponse(
            final long page,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        final AlertNotificationEntity[] testData = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final AlertNotificationEntity[] extendedTestData = AlertTestDataFactory.createExtendedReceiverAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", page)
                .param("size", 2)
                .param("filter", "status,EQUAL,RECEIVED,AND")
                .param("filter", "status,EQUAL,ACCEPTED,AND")
                .param("sort", "createdDate,desc")
                .log().all()
                .when()
                .get("/api/alerts/received")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(5))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }*/


    private static Stream<Arguments> createdArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        "status,EQUAL,SENT,AND",
                        2,
                        new String[]{"SENT", "SENT"}
                ),

                Arguments.of(
                        1,
                        "status,EQUAL,CREATED,AND",
                        1,
                        new String[]{"CREATED"}
                )
        );
    }
    @ParameterizedTest
    @MethodSource("createdArguments")
    void givenSmallPageSize_whenCallCreatedEndpoint_thenReturnExpectedResponse(
            final long page,
            final String filter,
            final int totalItems,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "createdDate,DESC";

        final AlertNotificationEntity[] testData = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final AlertNotificationEntity[] extendedTestData = AlertTestDataFactory.createExtendedSenderAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);
        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 2, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(2))
                .body("totalItems", equalTo(totalItems))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }

    private static Stream<Arguments> receivedArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        "status,EQUAL,RECEIVED,AND",
                        2,
                        new String[]{"RECEIVED", "RECEIVED"}
                ),

                Arguments.of(
                        1,
                        "status,EQUAL,ACCEPTED,AND",
                        3,
                        new String[]{"ACCEPTED", "ACCEPTED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("receivedArguments")
    void givenSmallPageSize_whenCallReceivedEndpoint_thenReturnExpectedResponse(
            final long page,
            final String filter,
            final int totalItems,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        String filterString = "channel,EQUAL,RECEIVER,AND";
        String sortString = "createdDate,DESC";

        final AlertNotificationEntity[] testData = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final AlertNotificationEntity[] extendedTestData = AlertTestDataFactory.createExtendedReceiverAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 2, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(2))
                .body("totalItems", equalTo(totalItems))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }
}
