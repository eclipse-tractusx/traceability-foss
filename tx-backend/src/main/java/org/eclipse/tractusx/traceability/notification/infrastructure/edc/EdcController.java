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
package org.eclipse.tractusx.traceability.notification.infrastructure.edc;

import assets.importpoc.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.security.TechnicalServiceApiKeyValidator;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.notification.domain.notification.service.NotificationReceiverService;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.eclipse.tractusx.traceability.common.model.SecurityUtils.sanitize;

@Slf4j
@Hidden
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/internal")
public class EdcController {

    private final NotificationReceiverService notificationReceiverService;

    /**
     * Receiver API call for EDC Transfer
     */
    @Operation(operationId = "investigationNotificationReceive",
            summary = "Receive and process investigation type notifications",
            tags = {"Edc"},
            description = "This endpoint receives and processes investigation type notifications.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/qualitynotifications/receive")
    @TechnicalServiceApiKeyValidator
    public void investigationNotificationReceive(final @ValidEDCNotification @Valid @RequestBody EDCNotification edcNotification) {
        EDCNotification cleanEdcNotification = sanitize(edcNotification);
        log.info("EdcController [investigationNotificationReceive] notificationId:{}", cleanEdcNotification);
        validateIsInvestigation(cleanEdcNotification);
        notificationReceiverService.handleReceive(cleanEdcNotification, NotificationType.INVESTIGATION);
    }

    /**
     * Update API call for EDC Transfer
     */
    @Operation(operationId = "investigationNotificationUpdate",
            summary = "Updates a investigation type notification",
            tags = {"Edc"},
            description = "This endpoint handles the update of a investigation type notification.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/qualitynotifications/update")
    @TechnicalServiceApiKeyValidator
    public void investigationNotificationUpdate(final @ValidEDCNotification @Valid @RequestBody EDCNotification edcNotification) {
        EDCNotification cleanEdcNotification = sanitize(edcNotification);
        log.info("EdcController [investigationNotificationUpdate] notificationId:{}", cleanEdcNotification);
        notificationReceiverService.handleUpdate(cleanEdcNotification, NotificationType.INVESTIGATION);
    }

    /**
     * Receiver API call for EDC Transfer
     */
    @Operation(operationId = "alertNotificationReceive",
            summary = "Receive and process alert type notifications",
            tags = {"Edc"},
            description = "This endpoint receives and processes alert type notifications.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/qualityalerts/receive")
    @TechnicalServiceApiKeyValidator
    public void alertNotificationReceive(final @ValidEDCNotification @Valid @RequestBody EDCNotification edcNotification) {
        EDCNotification cleanEdcNotification = sanitize(edcNotification);
        log.info("EdcController [alertNotificationReceive] notificationId:{}", cleanEdcNotification);
        validateIsAlert(cleanEdcNotification);
        notificationReceiverService.handleReceive(cleanEdcNotification, NotificationType.ALERT);
    }

    /**
     * Update API call for EDC Transfer
     */
    @Operation(operationId = "alertNotificationUpdate",
            summary = "Updates a alert type notification",
            tags = {"Edc"},
            description = "This endpoint handles the update of a alert type notifications.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authorization failed.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "415",
                    description = "Unsupported media type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "429",
                    description = "Too many requests.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping("/qualityalerts/update")
    @TechnicalServiceApiKeyValidator
    public void alertNotificationUpdate(final @ValidEDCNotification @Valid @RequestBody EDCNotification edcNotification) {
        EDCNotification cleanEdcNotification = sanitize(edcNotification);
        log.info("EdcController [alertNotificationUpdate] notificationId:{}", cleanEdcNotification);
        notificationReceiverService.handleUpdate(cleanEdcNotification, NotificationType.ALERT);
    }


    private void validateIsInvestigation(EDCNotification edcNotification) {
        org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.NotificationType notificationType = edcNotification.convertNotificationType();
        if (!notificationType.equals(org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.NotificationType.QMINVESTIGATION)) {
            throw new InvestigationIllegalUpdate("Received %s classified edc notification which is not an investigation".formatted(notificationType));
        }
    }

    private void validateIsAlert(EDCNotification edcNotification) {
        org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.NotificationType notificationType = edcNotification.convertNotificationType();
        if (!notificationType.equals(org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.NotificationType.QMALERT)) {
            throw new InvestigationIllegalUpdate("Received %s classified edc notification which is not an alert".formatted(notificationType));
        }
    }
}

