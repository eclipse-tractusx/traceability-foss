/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs;

import assets.importpoc.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.assets.domain.base.OrderRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.ProcessingState;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.eclipse.tractusx.traceability.common.security.Sanitizer.sanitize;

@Slf4j
@RestController
@Hidden
@RequiredArgsConstructor
@Validated
public class IrsCallbackController {

    private final OrderRepository orderRepository;

    @Operation(operationId = "irsCallback",
            summary = "Callback of irs get order details",
            tags = {"IRSCallback"},
            description = "The endpoint retrieves the information about a order which has been completed recently.",
            security = @SecurityRequirement(name = "oAuth2", scopes = "profile email"))
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Retrieves order id in completed state."),
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
    @GetMapping("/irs/order/callback")
    void handleIrsOrderCallback(
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "batchId", required = false) String batchId,
            @RequestParam(value = "orderState", required = false) String orderState,
            @RequestParam(value = "batchState", required = false) String batchState) {

        log.info("Received IRS order callback with orderId: {}, batchId: {}, orderState: {}, batchState: {}",
                sanitize(orderId), sanitize(batchId), sanitize(orderState), sanitize(batchState));
        try {
            validateIds(orderId, batchId);
            log.debug("Validated orderId and batchId successfully.");

            ProcessingState orderStateEnum = ProcessingState.fromString(orderState);
            ProcessingState batchStateEnum = ProcessingState.fromString(batchState);

            log.debug("Parsed orderState: {} and batchState: {}", orderStateEnum, batchStateEnum);

            orderRepository.handleOrderFinishedCallback(orderId, batchId, orderStateEnum, batchStateEnum);
            log.info("Successfully handled callback for orderId: {}", sanitize(orderId));
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed for callback parameters: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error occurred while handling IRS order callback for orderId: {}", sanitize(orderId), e);
        }
    }

    private void validateIds(String... ids) {
        for (String id : ids) {
            if (id != null && !id.isEmpty() && !id.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-8][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")) {
                log.warn("Invalid UUID format detected: {}", sanitize(id));
                throw new IllegalArgumentException("Invalid UUID format " + id);
            }

            if (StringUtils.isEmpty(id)) {
                log.warn("Empty or null UUID detected");
                throw new IllegalArgumentException("Empty or null UUID");
            }
        }
    }
}
