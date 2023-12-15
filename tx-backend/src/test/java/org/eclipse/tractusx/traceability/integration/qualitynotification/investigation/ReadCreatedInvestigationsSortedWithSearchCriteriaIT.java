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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

class ReadCreatedInvestigationsSortedWithSearchCriteriaIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;

    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;

    @BeforeEach
    void before() {
        final InvestigationNotificationEntity[] testData = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(testData);

        final InvestigationNotificationEntity[] extendedTestData = InvestigationTestDataFactory.createExtendedSenderInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(extendedTestData);
    }

    private static Stream<Arguments> sortAndFilterArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        "status,EQUAL,SENT",
                        "status,EQUAL,ACCEPTED",
                        "severity,EQUAL,2",
                        "AND",
                        new String[]{"SENT", "SENT"}
                ),
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        "status,EQUAL,ACKNOWLEDGED",
                        "severity,EQUAL,1",
                        "severity,EQUAL,3",
                        "AND",
                        new String[]{"ACKNOWLEDGED"}
                ),
                Arguments.of(
                        0,
                        5,
                        "createdDate,desc",
                        "sendTo,STARTS_WITH,BPNL000000000001",
                        "status,EQUAL,CREATED",
                        "severity,EQUAL,1",
                        "AND",
                        new String[]{"CREATED"}
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
                .get("/api/investigations/created")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(expectedOrderOfIdShortItems.length))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }
}
