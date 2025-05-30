package org.eclipse.tractusx.traceability.integration.assets;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ControllerIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @MethodSource("roles")
    void givenRoles_whenGetDashboardWithSpecificParts_thenReturnExpectedDashboardData(final List<JwtRole> roles) throws JoseException {
        // given: create exactly one asset of each type and owner
        assetsSupport.storeAssetAsBuilt(Owner.CUSTOMER);
        assetsSupport.storeAssetAsPlanned(Owner.CUSTOMER);
        assetsSupport.storeAssetAsBuilt(Owner.SUPPLIER);
        assetsSupport.storeAssetAsPlanned(Owner.SUPPLIER);
        assetsSupport.storeAssetAsBuilt(Owner.OWN);
        assetsSupport.storeAssetAsPlanned(Owner.OWN);

        // when/then: assert full dashboard response
        given()
                .header(oAuth2Support.jwtAuthorization(roles.toArray(new JwtRole[0])))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/dashboard")
                .then()
                .statusCode(200)
                .log().all()
                // asset counts
                .body("asBuiltCustomerParts", equalTo(1))
                .body("asPlannedCustomerParts", equalTo(1))
                .body("asBuiltSupplierParts", equalTo(1))
                .body("asPlannedSupplierParts", equalTo(1))
                .body("asBuiltOwnParts", equalTo(1))
                .body("asPlannedOwnParts", equalTo(1))

                // open alerts & investigations (should be 0 by default)
                .body("myPartsWithOpenAlerts", equalTo(0))
                .body("myPartsWithOpenInvestigations", equalTo(0))
                .body("supplierPartsWithOpenAlerts", equalTo(0))
                .body("customerPartsWithOpenAlerts", equalTo(0))
                .body("supplierPartsWithOpenInvestigations", equalTo(0))
                .body("customerPartsWithOpenInvestigations", equalTo(0))

                // sent/received notifications (should also be 0)
                .body("receivedActiveAlerts", equalTo(0))
                .body("receivedActiveInvestigations", equalTo(0))
                .body("sentActiveAlerts", equalTo(0))
                .body("sentActiveInvestigations", equalTo(0));
    }

    private static Stream<Arguments> roles() {
        return Stream.of(
                arguments(List.of(JwtRole.ADMIN)),
                arguments(List.of(JwtRole.SUPERVISOR)),
                arguments(List.of(JwtRole.USER))
        );
    }
}
