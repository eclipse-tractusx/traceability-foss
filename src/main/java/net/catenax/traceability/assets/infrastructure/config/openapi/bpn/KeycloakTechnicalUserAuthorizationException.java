package net.catenax.traceability.assets.infrastructure.config.openapi.bpn;

public class KeycloakTechnicalUserAuthorizationException extends RuntimeException {

	public KeycloakTechnicalUserAuthorizationException(String message) {
		super(message);
	}

	public KeycloakTechnicalUserAuthorizationException(Throwable cause) {
		super(cause);
	}
}
