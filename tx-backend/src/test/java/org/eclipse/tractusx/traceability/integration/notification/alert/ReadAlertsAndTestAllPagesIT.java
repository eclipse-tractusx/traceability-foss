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
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value(NotificationSide.SENDER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder().value("SENT").strategy(SearchCriteriaStrategy.EQUAL.name()).build(),
                                                FilterValue.builder().value("CREATED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"SENT", "SENT"}
                ),
                Arguments.of(
                        1,
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value(NotificationSide.SENDER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder().value("SENT").strategy(SearchCriteriaStrategy.EQUAL.name()).build(),
                                                FilterValue.builder().value("CREATED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"CREATED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createdArguments")
    void givenSmallPageSize_whenCallCreatedEndpoint_thenReturnExpectedResponse(
            final int page,
            final NotificationFilter filter,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {
        // Given
        String sortString = "createdDate,DESC";

        final NotificationMessageEntity[] testData = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final NotificationMessageEntity[] extendedTestData = AlertTestDataFactory.createExtendedSenderAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(page)
                        .size(2)
                        .sort(List.of(sortString))
                        .notificationFilter(filter)
                        .build())
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
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder().value("RECEIVED").strategy(SearchCriteriaStrategy.EQUAL.name()).build(),
                                                FilterValue.builder().value("ACCEPTED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"RECEIVED", "RECEIVED"}
                ),
                Arguments.of(
                        1,
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder().value("RECEIVED").strategy(SearchCriteriaStrategy.EQUAL.name()).build(),
                                                FilterValue.builder().value("ACCEPTED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"ACCEPTED", "ACCEPTED"}
                ),
                Arguments.of(
                        2,
                        NotificationFilter.builder()
                                .channel(FilterAttribute.builder()
                                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .status(FilterAttribute.builder()
                                        .value(List.of(
                                                FilterValue.builder().value("RECEIVED").strategy(SearchCriteriaStrategy.EQUAL.name()).build(),
                                                FilterValue.builder().value("ACCEPTED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()
                                        ))
                                        .operator(SearchCriteriaOperator.OR.name())
                                        .build())
                                .build(),
                        new String[]{"ACCEPTED"}
                )
        );
    }

    @ParameterizedTest
    @MethodSource("receivedArguments")
    void givenSmallPageSize_whenCallReceivedEndpoint_thenReturnExpectedResponse(
            final int page,
            final NotificationFilter filter,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {
        // Given
        String sortString = "createdDate,DESC";

        final NotificationMessageEntity[] testData = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(testData);

        final NotificationMessageEntity[] extendedTestData = AlertTestDataFactory.createExtendedReceiverAlertNotificationEntitiesTestData(bpnSupport.testBpn());
        alertNotificationsSupport.storedAlertNotifications(extendedTestData);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(page).size(2).sort(List.of(sortString)).notificationFilter(filter).build())
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
