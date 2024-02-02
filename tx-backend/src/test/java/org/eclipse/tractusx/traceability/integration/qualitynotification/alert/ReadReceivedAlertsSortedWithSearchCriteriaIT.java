package org.eclipse.tractusx.traceability.integration.qualitynotification.alert;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.testdata.AlertTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

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
        final AlertNotificationEntity[] testData = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final AlertNotificationEntity[] extendedTestData = AlertTestDataFactory.createExtendedReceiverAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);
    }

    private static Stream<Arguments> sortAndFilterArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        "status,EQUAL,RECEIVED",
                        "status,EQUAL,ACCEPTED",
                        "severity,EQUAL,2",
                        "AND",
                        new String[]{"RECEIVED", "RECEIVED"}
                ),
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        "status,EQUAL,ACCEPTED",
                        "severity,EQUAL,3",
                        "severity,EQUAL,1",
                        "AND",
                        new String[]{"ACCEPTED", "ACCEPTED"}
                ),
                Arguments.of(
                        0,
                        5,
                        "createdDate,desc",
                        "createdBy,STARTS_WITH,BPNL00000000000A",
                        "status,EQUAL,ACKNOWLEDGED",
                        "severity,EQUAL,1",
                        "AND",
                        new String[]{"ACKNOWLEDGED", "ACKNOWLEDGED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("sortAndFilterArguments")
    void givenSortAndTwoStatusFilters_whenCallSortAndFilterEndpoint_thenReturnExpectedResponse(
            final long page,
            final long size,
            final String sort,
            final String filter1,
            final String filter2,
            final String filter3,
            final String filterOperator,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", page)
                .param("size", size)
                .param("filter", filter1)
                .param("filter", filter2)
                .param("filter", filter3)
                .param("filterOperator", filterOperator)
                .param("sort", sort)
                .log().all()
                .when()
                .get("/api/alerts/received")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(expectedOrderOfIdShortItems.length))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }

    @Test
    void testDashboardLatestFiveActiveAlertEntries() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", 0)
                .param("size", 5)
                .param("filter", "status,EQUAL,RECEIVED,OR")
                .param("filter", "status,EQUAL,ACKNOWLEDGED,OR")
                .param("filter", "status,EQUAL,ACCEPTED,OR")
                .param("filter", "status,EQUAL,DECLINED,OR")
                .param("sort", "createdDate,desc")
                .log().all()
                .when()
                .get("/api/alerts/received")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(7))
                .body("content.status", Matchers.containsInRelativeOrder("RECEIVED", "RECEIVED", "ACCEPTED", "ACCEPTED", "ACCEPTED"));
    }
}
