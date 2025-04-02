package org.eclipse.tractusx.traceability.integration.notification.alert;

import common.FilterAttribute;
import common.FilterValue;
import io.restassured.http.ContentType;
import notification.request.NotificationFilter;
import notification.request.NotificationRequest;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
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
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value(NotificationSide.RECEIVER.name())
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value("RECEIVED")
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build(),
                                                FilterValue.builder()
                                                        .value("ACCEPTED")
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .severity(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value("CRITICAL")
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"RECEIVED", "RECEIVED"}
                ),
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value(NotificationSide.RECEIVER.name())
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value("ACCEPTED")
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .severity(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value("LIFE_THREATENING")
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build(),
                                                FilterValue.builder()
                                                        .value("MAJOR")
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"ACCEPTED", "ACCEPTED"}
                ),
                Arguments.of(
                        0,
                        5,
                        "createdDate,desc",
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value(NotificationSide.RECEIVER.name())
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .createdBy(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value("BPNL00000000000A")
                                                        .strategy(SearchCriteriaStrategy.STARTS_WITH.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value("ACKNOWLEDGED")
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .severity(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder()
                                                        .value("MAJOR")
                                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                                        .build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
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
            final NotificationFilter filter,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()

                        .page(page)
                        .size(size)
                        .sort(List.of(sort))
                        .notificationFilter(filter)
                        .build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(
                                FilterValue.builder()
                                        .value(NotificationSide.RECEIVER.name())
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .status(FilterAttribute.builder()
                        .value(List.of(
                                FilterValue.builder()
                                        .value("RECEIVED")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build(),
                                FilterValue.builder()
                                        .value("ACKNOWLEDGED")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build(),
                                FilterValue.builder()
                                        .value("ACCEPTED")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build(),
                                FilterValue.builder()
                                        .value("DECLINED")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()

                        .page(0)
                        .size(5)
                        .sort(List.of(sort))
                        .notificationFilter(filter)
                        .build())
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
