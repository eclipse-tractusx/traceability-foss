/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.config;

import assets.importpoc.ErrorResponse;
import assets.importpoc.IRSErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.tractusx.traceability.assets.application.importpoc.validation.exception.JsonFileProcessingException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.ImportException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.ImportJobNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PolicyBadRequestException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PolicyNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PublishAssetException;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnNotFoundException;
import org.eclipse.tractusx.traceability.common.domain.ParseLocalDateException;
import org.eclipse.tractusx.traceability.common.model.UnsupportedSearchCriteriaFieldException;
import org.eclipse.tractusx.traceability.common.request.exception.InvalidFilterException;
import org.eclipse.tractusx.traceability.common.request.exception.InvalidSortException;
import org.eclipse.tractusx.traceability.common.security.TechnicalUserAuthorizationException;
import org.eclipse.tractusx.traceability.common.security.exception.InvalidApiKeyException;
import org.eclipse.tractusx.traceability.contracts.domain.exception.ContractException;
import org.eclipse.tractusx.traceability.discovery.infrastructure.exception.DiscoveryFinderException;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractException;
import org.eclipse.tractusx.traceability.notification.application.notification.validation.UpdateNotificationValidationException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.CatalogItemPolicyMismatchException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.NoCatalogItemException;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationReceiverBpnMismatchException;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationStatusTransitionNotAllowed;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationIllegalUpdate;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationNotFoundException;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationNotSupportedException;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationSenderAndReceiverBPNEqualException;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationStatusTransitionNotAllowed;
import org.eclipse.tractusx.traceability.submodel.domain.model.SubmodelNotFoundException;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorHandlingConfig implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        final String errorMessage = exception
                .getBindingResult()
                .getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("handleMethodArgumentNotValidException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    ResponseEntity<ErrorResponse> handleHttpClientErrorException(final HttpClientErrorException exception) {
        log.warn("handleHttpClientErrorException", exception);

        final HttpStatusCode status = HttpStatusCode.valueOf(exception.getStatusCode().value());
        final String errorMessage;

        if (status.equals(BAD_REQUEST)) {
            try {
                final ResponseEntity<ErrorResponse> errorResponse = mapIRSBadRequestToErrorResponse(exception);
                if (errorResponse != null) return errorResponse;
            } catch (final Exception e) {
                final ResponseEntity<ErrorResponse> body = ResponseEntity.status(BAD_REQUEST)
                        .body(new ErrorResponse(exception.getMessage()));
                return body; // Handle the case where the message cannot be mapped to IRSErrorResponse
            }

        } else if (status.equals(NOT_FOUND)) {
            try {
                final ResponseEntity<ErrorResponse> errorResponse = mapIRSNotFoundToErrorResponse(exception);
                if (errorResponse != null) return errorResponse;
            } catch (final Exception e) {
                final ResponseEntity<ErrorResponse> body = ResponseEntity.status(NOT_FOUND)
                        .body(new ErrorResponse(exception.getMessage()));
                return body; // Handle the case where the message cannot be mapped to IRSErrorResponse
            }
        } else {
            errorMessage = exception.getMessage();
            return ResponseEntity.status(status)
                    .body(new ErrorResponse(errorMessage));
        }
        return ResponseEntity.status(status).body(new ErrorResponse(exception.getMessage()));
    }

    private @Nullable ResponseEntity<ErrorResponse> mapIRSBadRequestToErrorResponse(final HttpClientErrorException exception) throws JsonProcessingException {
        final String rawMessage = exception.getMessage().replaceAll("<EOL>", "");
        // Extract the JSON part of the message
        final int jsonStart = rawMessage.indexOf("{");
        final int jsonEnd = rawMessage.lastIndexOf("}");
        if (jsonStart != -1 && jsonEnd != -1) {
            final String jsonString = rawMessage.substring(jsonStart, jsonEnd + 1);
            final IRSErrorResponse badRequestResponse = objectMapper.readValue(jsonString, IRSErrorResponse.class);
            final List<String> messages = badRequestResponse.getMessages();
            final String concatenatedMessages = String.join(", ", messages);
            return ResponseEntity.status(400).body(new ErrorResponse(concatenatedMessages));
        }
        return null;
    }

    private @Nullable ResponseEntity<ErrorResponse> mapIRSNotFoundToErrorResponse(final HttpClientErrorException exception) throws JsonProcessingException {
        final String rawMessage = exception.getMessage().replaceAll("<EOL>", "");
        // Extract the JSON part of the message
        final int jsonStart = rawMessage.indexOf("{");
        final int jsonEnd = rawMessage.lastIndexOf("}");
        if (jsonStart != -1 && jsonEnd != -1) {
            final String jsonString = rawMessage.substring(jsonStart, jsonEnd + 1);
            final IRSErrorResponse badRequestResponse = objectMapper.readValue(jsonString, IRSErrorResponse.class);
            final List<String> messages = badRequestResponse.getMessages();
            final String concatenatedMessages = String.join(", ", messages);
            return ResponseEntity.status(404).body(new ErrorResponse(concatenatedMessages));
        }
        return null;
    }

    @ExceptionHandler(PolicyBadRequestException.class)
    ResponseEntity<ErrorResponse> handlePolicyBadRequestException(final PolicyBadRequestException exception) {
        log.warn("handlePolicyBadRequestException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(final HttpServerErrorException exception) {
        log.warn("handleHttpServerErrorException", exception);

        final HttpStatusCode status = exception.getStatusCode();
        final String errorMessage = exception.getMessage();
        return ResponseEntity.status(status)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(JpaSystemException.class)
    ResponseEntity<ErrorResponse> handleJpaSystemException(final JpaSystemException exception) {
        log.warn("handleJpaSystemException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse("Failed to deserialize request body."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        log.warn("handleHttpMessageNotReadableException", exception);
        String message = "Failed to deserialize request body.";

        if (exception.getRootCause() instanceof NoSuchElementException) {
            message = ExceptionUtils.getRootCauseMessage(exception);
        }
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(AssetNotFoundException.class)
    ResponseEntity<ErrorResponse> handleAssetNotFoundException(final AssetNotFoundException exception) {
        log.warn("handleAssetNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(PolicyNotFoundException.class)
    ResponseEntity<ErrorResponse> handlePolicyNotFoundException(final PolicyNotFoundException exception) {
        log.warn("handlePolicyNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(DiscoveryFinderException.class)
    ResponseEntity<ErrorResponse> handleDiscoveryFinderException(final DiscoveryFinderException exception) {
        log.warn("handleDiscoveryFinderException", exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(PublishAssetException.class)
    ResponseEntity<ErrorResponse> handlePublishAssetException(final PublishAssetException exception) {
        log.warn("handlePublishAssetException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ImportException.class)
    ResponseEntity<ErrorResponse> handleImportException(final ImportException exception) {
        log.warn("handleImportException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvestigationNotFoundException.class)
    ResponseEntity<ErrorResponse> handleInvestigationNotFoundException(final InvestigationNotFoundException exception) {
        log.warn("handleInvestigationNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NotificationNotSupportedException.class)
    ResponseEntity<ErrorResponse> handleInvestigationNotSupportedException(final NotificationNotSupportedException exception) {
        log.warn("handleInvestigationNotSupportedException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotificationNotFoundException(final NotificationNotFoundException exception) {
        log.warn("handleNotificationNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvestigationStatusTransitionNotAllowed.class)
    ResponseEntity<ErrorResponse> handleInvestigationStatusTransitionNotAllowed(
            final InvestigationStatusTransitionNotAllowed exception) {
        log.warn("handleInvestigationStatusTransitionNotAllowed", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(BpnNotFoundException.class)
    ResponseEntity<ErrorResponse> handleBpnEdcMappingNotFoundException(final BpnNotFoundException exception) {
        log.warn("handleBpnEdcMappingNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvestigationReceiverBpnMismatchException.class)
    ResponseEntity<ErrorResponse> handleInvestigationReceiverBpnMismatchException(
            final InvestigationReceiverBpnMismatchException exception) {
        log.warn("handleInvestigationReceiverBpnMismatchException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NotificationSenderAndReceiverBPNEqualException.class)
    ResponseEntity<ErrorResponse> handleNotificationSenderAndReceiverBPNEqualException(
            final NotificationSenderAndReceiverBPNEqualException exception) {
        log.warn("handleNotificationSenderAndReceiverBPNEqualException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(SendNotificationException.class)
    ResponseEntity<ErrorResponse> handleSendNotificationException(final SendNotificationException exception) {
        log.warn("handleSendNotificationException", exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ErrorResponse> handleValidationException(final ValidationException exception) {
        log.warn("handleValidationException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CatalogItemPolicyMismatchException.class)
    ResponseEntity<ErrorResponse> handlePolicyMismatchException(final CatalogItemPolicyMismatchException exception) {
        log.warn("handlePolicyMismatchException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NoCatalogItemException.class)
    ResponseEntity<ErrorResponse> handleNoCatalogItemException(final NoCatalogItemException exception) {
        log.warn("handlePolicyMismatchException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvestigationIllegalUpdate.class)
    ResponseEntity<ErrorResponse> handleInvestigationIllegalUpdate(final InvestigationIllegalUpdate exception) {
        log.warn("handleInvestigationIllegalUpdate", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NotificationIllegalUpdate.class)
    ResponseEntity<ErrorResponse> handleAlertIllegalUpdate(final NotificationIllegalUpdate exception) {
        log.warn("handleAlertIllegalUpdate", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> handleIllegalArgumentException(final IllegalArgumentException exception) {
        log.warn("handleIllegalArgumentException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ParseLocalDateException.class)
    ResponseEntity<ErrorResponse> handleParseLocalDateException(final ParseLocalDateException exception) {
        log.warn("handleParseLocalDateException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> handleAccessDeniedException(final AccessDeniedException exception) {
        log.warn("handleAccessDeniedException", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(TechnicalUserAuthorizationException.class)
    ResponseEntity<ErrorResponse> handleTechnicalUserAuthorizationException(final TechnicalUserAuthorizationException exception) {
        log.error("Couldn't retrieve token for technical user", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Please try again later."));
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(
            final AuthenticationCredentialsNotFoundException exception) {
        log.warn("Couldn't find authentication for the request", exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication not found."));
    }

    @ExceptionHandler(NotificationStatusTransitionNotAllowed.class)
    ResponseEntity<ErrorResponse> handleNotificationStatusTransitionNotAllowed(
            final NotificationStatusTransitionNotAllowed exception) {
        log.warn("handleNotificationStatusTransitionNotAllowed", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(UpdateNotificationValidationException.class)
    ResponseEntity<ErrorResponse> handleUpdateNotificationValidationException(
            final UpdateNotificationValidationException exception) {
        log.warn("handleUpdateNotificationValidationException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidFilterException.class)
    ResponseEntity<ErrorResponse> handleInvalidFilterException(final InvalidFilterException exception) {
        log.warn("handleInvalidFilterException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(UnsupportedSearchCriteriaFieldException.class)
    ResponseEntity<ErrorResponse> handleUnsupportedSearchCriteriaFieldException(
            final UnsupportedSearchCriteriaFieldException exception) {
        log.warn("handleInvalidFilterException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CreateNotificationContractException.class)
    ResponseEntity<ErrorResponse> handleCreateNotificationContractException(final CreateNotificationContractException exception) {
        log.warn("handleCreateNotificationContractException", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create notification contract."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledException(final Exception exception) {
        log.error("Unhandled exception", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Please try again later."));
    }

    @ExceptionHandler(JsonFileProcessingException.class)
    public ResponseEntity<ErrorResponse> handle(final JsonFileProcessingException exception) {
        log.error("JsonFileProcessingException", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Issue occurred while processing json file: %s".formatted(exception.getMessage())));
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException {
        log.error("AuthenticationException exception", exception);
        final ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
    }

    @ExceptionHandler(InvalidSortException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSortException(final InvalidSortException exception) {
        log.error("InvalidSortException exception", exception);

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(SubmodelNotFoundException.class)
    ResponseEntity<ErrorResponse> handleSubmodelNotFoundException(final SubmodelNotFoundException exception) {
        final String errorMessage = exception
                .getMessage();
        log.warn("handleSubmodelNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        log.error("MethodArgumentTypeMismatchException exception", exception);

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ImportJobNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleImportJobNotFoundException(final ImportJobNotFoundException exception) {
        log.error("ImportJobNotFoundException exception", exception);

        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ContractException.class)
    public ResponseEntity<ErrorResponse> handleContractException(final ContractException exception) {
        log.error("Contract exception", exception);

        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidApiKeyException.class)
    ResponseEntity<ErrorResponse> handleInvalidApiKeyException(final InvalidApiKeyException exception) {
        log.warn("handleInvalidApiKeyException", exception);
        return ResponseEntity.status(UNAUTHORIZED)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErrorResponse> handleDataIntegrityException(final DataIntegrityViolationException exception) {
        log.warn("handleDataIntegrityException", exception);
        String message = exception.getMessage();
        if (message != null && message.length() > 200) {
            message = message.substring(0, 200);
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(message));
    }
}
