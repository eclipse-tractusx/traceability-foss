package net.catenax.traceability.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.traceability.assets.AssetNotFoundException;
import net.catenax.traceability.config.interceptor.KeycloakTechnicalUserAuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandlingConfig implements AuthenticationFailureHandler {

	private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingConfig.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
		String errorMessage = methodArgumentNotValidException
			.getBindingResult()
			.getAllErrors().stream()
			.map(DefaultMessageSourceResolvable::getDefaultMessage)
			.collect(Collectors.joining(", "));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse(errorMessage));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException httpMessageNotReadableException) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(new ErrorResponse("Failed to deserialize request body."));
	}

	@ExceptionHandler(AssetNotFoundException.class)
	ResponseEntity<ErrorResponse> handleAssetNotFoundException(AssetNotFoundException assetNotFoundException) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(new ErrorResponse(assetNotFoundException.getMessage()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException accessDeniedException) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(new ErrorResponse(accessDeniedException.getMessage()));
	}

	@ExceptionHandler(KeycloakTechnicalUserAuthorizationException.class)
	ResponseEntity<ErrorResponse> handleKeycloakTokenRetrieveException(KeycloakTechnicalUserAuthorizationException keycloakTechnicalUserAuthorizationException) {
		logger.error("Couldn't retrieve keycloak token for technical user", keycloakTechnicalUserAuthorizationException);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse("Please try again latter."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> unhandledException(Exception exception) {
		logger.error("Unhandled exception", exception);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse("Please try again latter."));
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
		ErrorHandlingConfig.ErrorResponse errorResponse = new ErrorHandlingConfig.ErrorResponse(exception.getMessage());

		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
	}

	public record ErrorResponse(String message) {
	}
}
