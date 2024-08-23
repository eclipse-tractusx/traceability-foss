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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

class ReadReceivedAlertsSortedWithSearchCriteriaIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;

    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    @BeforeEach
    void before() {
        final NotificationMessageEntity[] testData = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final NotificationMessageEntity[] extendedTestData = AlertTestDataFactory.createExtendedReceiverAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);
    }

    private static Stream<Arguments> sortAndFilterArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        "status,EQUAL,RECEIVED,AND",
                        "status,EQUAL,ACCEPTED,AND",
                        "severity,EQUAL,CRITICAL,AND",
                        new String[]{"RECEIVED", "RECEIVED"}
                ),
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        "status,EQUAL,ACCEPTED,AND",
                        "severity,EQUAL,LIFE_THREATENING,AND",
                        "severity,EQUAL,MAJOR,AND",
                        new String[]{"ACCEPTED", "ACCEPTED"}
                ),
                Arguments.of(
                        0,
                        5,
                        "createdDate,desc",
                        "createdBy,STARTS_WITH,BPNL00000000000A,AND",
                        "status,EQUAL,ACKNOWLEDGED,AND",
                        "severity,EQUAL,MAJOR,AND",
                        new String[]{"ACKNOWLEDGED", "ACKNOWLEDGED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("sortAndFilterArguments")
    void givenSortAndTwoStatusFilters_whenCallSortAndFilterEndpoint_thenReturnExpectedResponse(
            final int page,
            final int size,
            final String sort,
            final String filter1,
            final String filter2,
            final String filter3,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        String filterString = "channel,EQUAL,RECEIVER,AND";

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(page, size, List.of(sort)), new SearchCriteriaRequestParam(List.of(filterString, filter1, filter2, filter3))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(expectedOrderOfIdShortItems.length))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }

    @Test
    void testDashboardLatestFiveActiveAlertEntries() throws JoseException {
        String sort = "createdDate,desc";
        String filterString = "channel,EQUAL,RECEIVER,AND";
        String filter1 = "status,EQUAL,RECEIVED,OR";
        String filter2 = "status,EQUAL,ACKNOWLEDGED,OR";
        String filter3 = "status,EQUAL,ACCEPTED,OR";
        String filter4 = "status,EQUAL,DECLINED,OR";

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 5, List.of(sort)), new SearchCriteriaRequestParam(List.of(filterString, filter1, filter2, filter3, filter4))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(7))
                .body("content.status", Matchers.containsInRelativeOrder("RECEIVED", "RECEIVED", "ACCEPTED", "ACCEPTED", "ACCEPTED"));
    }
}
