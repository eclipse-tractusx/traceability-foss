package net.catenax.traceability.config.interceptor;

public class KeycloakTechnicalUserAuthorizationException extends RuntimeException {

	public KeycloakTechnicalUserAuthorizationException(String message) {
		super(message);
	}

	public KeycloakTechnicalUserAuthorizationException(Throwable cause) {
		super(cause);
	}
}
