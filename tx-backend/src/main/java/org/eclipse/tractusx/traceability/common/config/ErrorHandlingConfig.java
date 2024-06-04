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
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PolicyNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.PublishAssetException;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnNotFoundException;
import org.eclipse.tractusx.traceability.common.domain.ParseLocalDateException;
import org.eclipse.tractusx.traceability.common.model.UnsupportedSearchCriteriaFieldException;
import org.eclipse.tractusx.traceability.common.request.exception.InvalidFilterException;
import org.eclipse.tractusx.traceability.common.request.exception.InvalidSortException;
import org.eclipse.tractusx.traceability.common.security.TechnicalUserAuthorizationException;
import org.eclipse.tractusx.traceability.contracts.domain.exception.ContractException;
import org.eclipse.tractusx.traceability.discovery.infrastructure.exception.DiscoveryFinderException;
import org.eclipse.tractusx.traceability.notification.application.contract.model.CreateNotificationContractException;
import org.eclipse.tractusx.traceability.notification.application.notification.validation.UpdateNotificationValidationException;
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
import org.springframework.context.support.DefaultMessageSourceResolvable;
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ErrorHandlingConfig implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String errorMessage = exception
                .getBindingResult()
                .getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("handleMethodArgumentNotValidException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    ResponseEntity<ErrorResponse> handleHttpClientErrorException(HttpClientErrorException exception) {
        log.warn("handleHttpClientErrorException", exception);

        HttpStatusCode status = exception.getStatusCode();
        String errorMessage;

        if (status.equals(BAD_REQUEST)) {
            errorMessage = exception.getMessage();
        } else if (status.equals(NOT_FOUND)) {
            errorMessage = exception.getMessage();
        } else {
            errorMessage = exception.getMessage();
        }

        return ResponseEntity.status(status)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorResponse> handleHttpServerErrorException(HttpServerErrorException exception) {
        log.warn("handleHttpServerErrorException", exception);

        HttpStatusCode status = exception.getStatusCode();
        String errorMessage;

        if (status.equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
            errorMessage = exception.getMessage();
        } else {
            errorMessage = exception.getMessage();
        }

        return ResponseEntity.status(status)
                .body(new ErrorResponse(errorMessage));
    }

    @ExceptionHandler(JpaSystemException.class)
    ResponseEntity<ErrorResponse> handleJpaSystemException(JpaSystemException exception) {
        log.warn("handleJpaSystemException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse("Failed to deserialize request body."));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        log.warn("handleHttpMessageNotReadableException", exception);
        String message = "Failed to deserialize request body.";

        if (exception.getRootCause() instanceof NoSuchElementException) {
            message = ExceptionUtils.getRootCauseMessage(exception);
        }
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(message));
    }

    @ExceptionHandler(AssetNotFoundException.class)
    ResponseEntity<ErrorResponse> handleAssetNotFoundException(AssetNotFoundException exception) {
        log.warn("handleAssetNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(PolicyNotFoundException.class)
    ResponseEntity<ErrorResponse> handlePolicyNotFoundException(PolicyNotFoundException exception) {
        log.warn("handlePolicyNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(DiscoveryFinderException.class)
    ResponseEntity<ErrorResponse> handleDiscoveryFinderException(DiscoveryFinderException exception) {
        log.warn("handleDiscoveryFinderException", exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(PublishAssetException.class)
    ResponseEntity<ErrorResponse> handlePublishAssetException(PublishAssetException exception) {
        log.warn("handlePublishAssetException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ImportException.class)
    ResponseEntity<ErrorResponse> handleImportException(ImportException exception) {
        log.warn("handleImportException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvestigationNotFoundException.class)
    ResponseEntity<ErrorResponse> handleInvestigationNotFoundException(InvestigationNotFoundException exception) {
        log.warn("handleInvestigationNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NotificationNotSupportedException.class)
    ResponseEntity<ErrorResponse> handleInvestigationNotSupportedException(NotificationNotSupportedException exception) {
        log.warn("handleInvestigationNotSupportedException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    ResponseEntity<ErrorResponse> handleNotificationNotFoundException(NotificationNotFoundException exception) {
        log.warn("handleNotificationNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvestigationStatusTransitionNotAllowed.class)
    ResponseEntity<ErrorResponse> handleInvestigationStatusTransitionNotAllowed(InvestigationStatusTransitionNotAllowed exception) {
        log.warn("handleInvestigationStatusTransitionNotAllowed", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(BpnNotFoundException.class)
    ResponseEntity<ErrorResponse> handleBpnEdcMappingNotFoundException(BpnNotFoundException exception) {
        log.warn("handleBpnEdcMappingNotFoundException", exception);
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvestigationReceiverBpnMismatchException.class)
    ResponseEntity<ErrorResponse> handleInvestigationReceiverBpnMismatchException(InvestigationReceiverBpnMismatchException exception) {
        log.warn("handleInvestigationReceiverBpnMismatchException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NotificationSenderAndReceiverBPNEqualException.class)
    ResponseEntity<ErrorResponse> handleNotificationSenderAndReceiverBPNEqualException(NotificationSenderAndReceiverBPNEqualException exception) {
        log.warn("handleNotificationSenderAndReceiverBPNEqualException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(SendNotificationException.class)
    ResponseEntity<ErrorResponse> handleSendNotificationException(SendNotificationException exception) {
        log.warn("handleSendNotificationException", exception);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ErrorResponse> handleValidationException(ValidationException exception) {
        log.warn("handleValidationException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvestigationIllegalUpdate.class)
    ResponseEntity<ErrorResponse> handleInvestigationIllegalUpdate(InvestigationIllegalUpdate exception) {
        log.warn("handleInvestigationIllegalUpdate", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NotificationIllegalUpdate.class)
    ResponseEntity<ErrorResponse> handleAlertIllegalUpdate(NotificationIllegalUpdate exception) {
        log.warn("handleAlertIllegalUpdate", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.warn("handleIllegalArgumentException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(ParseLocalDateException.class)
    ResponseEntity<ErrorResponse> handleParseLocalDateException(ParseLocalDateException exception) {
        log.warn("handleParseLocalDateException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
        log.warn("handleAccessDeniedException", exception);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(TechnicalUserAuthorizationException.class)
    ResponseEntity<ErrorResponse> handleTechnicalUserAuthorizationException(TechnicalUserAuthorizationException exception) {
        log.error("Couldn't retrieve token for technical user", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Please try again later."));
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    ResponseEntity<ErrorResponse> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException exception) {
        log.warn("Couldn't find authentication for the request", exception);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Authentication not found."));
    }

    @ExceptionHandler(NotificationStatusTransitionNotAllowed.class)
    ResponseEntity<ErrorResponse> handleNotificationStatusTransitionNotAllowed(NotificationStatusTransitionNotAllowed exception) {
        log.warn("handleNotificationStatusTransitionNotAllowed", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(UpdateNotificationValidationException.class)
    ResponseEntity<ErrorResponse> handleUpdateNotificationValidationException(UpdateNotificationValidationException exception) {
        log.warn("handleUpdateNotificationValidationException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(InvalidFilterException.class)
    ResponseEntity<ErrorResponse> handleInvalidFilterException(InvalidFilterException exception) {
        log.warn("handleInvalidFilterException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(UnsupportedSearchCriteriaFieldException.class)
    ResponseEntity<ErrorResponse> handleUnsupportedSearchCriteriaFieldException(UnsupportedSearchCriteriaFieldException exception) {
        log.warn("handleInvalidFilterException", exception);
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(CreateNotificationContractException.class)
    ResponseEntity<ErrorResponse> handleCreateNotificationContractException(CreateNotificationContractException exception) {
        log.warn("handleCreateNotificationContractException", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Failed to create notification contract."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unhandledException(Exception exception) {
        log.error("Unhandled exception", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Please try again later."));
    }

    @ExceptionHandler(JsonFileProcessingException.class)
    public ResponseEntity<ErrorResponse> handle(JsonFileProcessingException exception) {
        log.error("JsonFileProcessingException", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Issue occurred while processing json file: %s".formatted(exception.getMessage())));
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.error("AuthenticationException exception", exception);
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());

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
    ResponseEntity<ErrorResponse> handleSubmodelNotFoundException(SubmodelNotFoundException exception) {
        String errorMessage = exception
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
}
