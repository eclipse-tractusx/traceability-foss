package org.eclipse.tractusx.traceability.integration.notification.investigation;

import common.FilterAttribute;
import common.FilterValue;
import io.restassured.http.ContentType;
import notification.request.NotificationFilter;
import notification.request.NotificationRequest;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
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
        final NotificationMessageEntity[] testData = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(testData);

        final NotificationMessageEntity[] extendedTestData = InvestigationTestDataFactory.createExtendedSenderInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(extendedTestData);
    }

    private static Stream<Arguments> sortAndFilterArguments() {
        return Stream.of(
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.SENDER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder().value("SENT").strategy(SearchCriteriaStrategy.EQUAL.name()).build(),
                                                FilterValue.builder().value("ACCEPTED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .severity(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value("CRITICAL").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"SENT", "SENT"}
                ),
                Arguments.of(
                        0,
                        50,
                        "createdDate,desc",
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.SENDER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value("ACKNOWLEDGED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .severity(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder().value("MAJOR").strategy(SearchCriteriaStrategy.EQUAL.name()).build(),
                                                FilterValue.builder().value("LIFE-THREATENING").strategy(SearchCriteriaStrategy.EQUAL.name()).build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"ACKNOWLEDGED"}
                ),
                Arguments.of(
                        0,
                        5,
                        "createdDate,desc",
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.SENDER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .sendTo(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value("BPNL000000000001").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value("CREATED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .severity(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value("MAJOR").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"CREATED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("sortAndFilterArguments")
    void givenSortAndTwoStatusFilters_whenCallSortAndFilterEndpoint_thenReturnExpectedResponse(
            final int page,
            final int size,
            final String sort,
            final NotificationFilter filter1,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {
        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(page).size(size).sort(List.of(sort)).notificationFilter(filter1).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(expectedOrderOfIdShortItems.length))
                .body("content.status", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }
}
