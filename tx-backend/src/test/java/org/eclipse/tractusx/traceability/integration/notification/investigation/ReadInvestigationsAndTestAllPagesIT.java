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
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
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
            final int page,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {
        // Given
        NotificationFilter notificationFilter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(
                                FilterValue.builder()
                                        .value(NotificationSide.SENDER.name())
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
                                        .build(),
                                FilterValue.builder()
                                        .value("SENT")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String sort = "createdDate,desc";

        final NotificationMessageEntity[] testData = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(testData);

        final NotificationMessageEntity[] extendedTestData = InvestigationTestDataFactory.createExtendedSenderInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(extendedTestData);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of(sort))
                        .notificationFilter(notificationFilter)
                        .build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
            final int page,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {
        // Given
        NotificationFilter notificationFilter = NotificationFilter.builder()
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
                                        .build(),
                                FilterValue.builder()
                                        .value("RECEIVED")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String sort = "createdDate,desc";

        final NotificationMessageEntity[] testData = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(testData);

        final NotificationMessageEntity[] extendedTestData = InvestigationTestDataFactory.createExtendedReceiverInvestigationNotificationEntitiesTestData(bpnSupport.testBpn());
        investigationNotificationsSupport.storedNotifications(extendedTestData);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of(sort))
                        .notificationFilter(notificationFilter)
                        .build())
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
