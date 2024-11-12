package org.eclipse.tractusx.traceability.integration.common.support;

import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.USER;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Support class that supplies expected role combinations to be used in the *AuthorizationIT tests.
 * All methods return a Stream of 4 elements, representing the 3 application roles + no role at all.
 * Each element consists the Role (or null for the "no role" case) and a boolean that indicates if access
 * to an endpoint should be possible with that role.
 */

public abstract class RoleSupport {
    static Stream<Arguments> allRolesAllowed() {
        return Stream.of(
                arguments(USER, true),
                arguments(SUPERVISOR, true),
                arguments(ADMIN, true),
                arguments(null, false)
        );
    }

    static Stream<Arguments> supervisorAndUserRolesAllowed() {
        return Stream.of(
                arguments(USER, true),
                arguments(SUPERVISOR, true),
                arguments(ADMIN, false),
                arguments(null, false)
        );
    }

    static Stream<Arguments> supervisorRoleAllowed() {
        return Stream.of(
                arguments(USER, false),
                arguments(SUPERVISOR, true),
                arguments(ADMIN, false),
                arguments(null, false)
        );
    }

    static Stream<Arguments> adminRoleAllowed() {
        return Stream.of(
                arguments(USER, false),
                arguments(SUPERVISOR, false),
                arguments(ADMIN, true),
                arguments(null, false)
        );
    }

    static Stream<Arguments> supervisorAndAdminRolesAllowed() {
        return Stream.of(
                arguments(USER, false),
                arguments(SUPERVISOR, true),
                arguments(ADMIN, true),
                arguments(null, false)
        );
    }

    static Stream<Arguments> noRoleRequired() {
        return Stream.of(
                arguments(USER, true),
                arguments(SUPERVISOR, true),
                arguments(ADMIN, true),
                arguments(null, true)
        );
    }
}
