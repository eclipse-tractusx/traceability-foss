/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package net.catenax.traceability.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenax.traceability.assets.domain.model.AssetNotFoundException;
import net.catenax.traceability.assets.infrastructure.config.openapi.KeycloakTechnicalUserAuthorizationException;
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

	private final ObjectMapper objectMapper;

	public ErrorHandlingConfig(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

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
			.body(new ErrorResponse("Please try again later."));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> unhandledException(Exception exception) {
		logger.error("Unhandled exception", exception);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
			.body(new ErrorResponse("Please try again later."));
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
