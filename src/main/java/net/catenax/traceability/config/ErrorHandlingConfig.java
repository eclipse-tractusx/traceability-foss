package net.catenax.traceability.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
public class ErrorHandlingConfig implements AuthenticationFailureHandler {

	private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingConfig.class);

	private final ObjectMapper objectMapper = new ObjectMapper();

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ErrorResponse> responseStatusException(AccessDeniedException accessDeniedException) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
			.body(new ErrorResponse(accessDeniedException.getMessage()));
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
