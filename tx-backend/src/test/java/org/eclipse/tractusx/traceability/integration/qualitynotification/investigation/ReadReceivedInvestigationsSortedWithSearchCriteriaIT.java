package org.eclipse.tractusx.traceability.integration.qualitynotification.investigation;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
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

class ReadReceivedInvestigationsSortedWithSearchCriteriaIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;

    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;

    @BeforeEach
    void before() {
        final InvestigationNotificationEntity[] testData = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(testData);

        final InvestigationNotificationEntity[] extendedTestData = InvestigationTestDataFactory.createExtendedReceiverInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(extendedTestData);
    }

    private static Stream<Arguments> sortAndFilterArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        "status,EQUAL,RECEIVED,AND",
                        "status,EQUAL,ACCEPTED,AND",
                        "severity,EQUAL,2,AND",
                        new String[]{"RECEIVED"}
                ),
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        "status,EQUAL,ACCEPTED,AND",
                        "severity,EQUAL,3,AND",
                        "severity,EQUAL,2,AND",
                        new String[]{"ACCEPTED", "ACCEPTED"}
                ),
                Arguments.of(
                        0,
                        5,
                        "createdDate,desc",
                        "createdBy,STARTS_WITH,BPNL00000000000A,AND",
                        "status,EQUAL,ACKNOWLEDGED,AND",
                        "severity,EQUAL,2,AND",
                        new String[]{"ACKNOWLEDGED"}
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
                .param("sort", sort)
                .log().all()
                .when()
                .get("/api/investigations/received")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(expectedOrderOfIdShortItems.length))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }

    @Test
    void testDashboardLatestFiveActiveInvestigationEntries() throws JoseException {
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
                .get("/api/investigations/received")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(6))
                .body("content.status", Matchers.containsInRelativeOrder("RECEIVED", "ACKNOWLEDGED", "ACCEPTED", "ACCEPTED", "RECEIVED"));
    }
}
