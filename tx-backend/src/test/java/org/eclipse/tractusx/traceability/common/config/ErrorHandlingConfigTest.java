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

package org.eclipse.tractusx.traceability.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnNotFoundException;
import org.eclipse.tractusx.traceability.common.response.ErrorResponse;
import org.eclipse.tractusx.traceability.common.security.TechnicalUserAuthorizationException;
import org.eclipse.tractusx.traceability.qualitynotification.application.contract.model.CreateNotificationContractException;
import org.eclipse.tractusx.traceability.qualitynotification.application.validation.UpdateQualityNotificationValidationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationReceiverBpnMismatchException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationStatusTransitionNotAllowed;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.NotificationStatusTransitionNotAllowed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ErrorHandlingConfigTest {

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(new ErrorHandlingConfig(objectMapper))
                .build();
    }

    @Test
    void givenMethodArgumentNotValidException_handler_respondsBadRequest() throws Exception {

        mockMvc.perform(get("/methodArgumentNotValidException"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenJpaSystemException_handler_respondsBadRequest() throws Exception {
        mockMvc.perform(get("/jpaSystemException"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenHttpMessageNotReadableException_handler_respondsBadRequest() throws Exception {
        mockMvc.perform(get("/httpMessageNotReadableException"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAssetNotFoundException_handler_respondsNotFound() throws Exception {
        mockMvc.perform(get("/assetNotFoundException"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvestigationNotFoundException_handler_respondsNotFound() throws Exception {
        mockMvc.perform(get("/investigationNotFoundException"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvestigationStatusTransitionNotAllowed_handler_respondsBadRequest() throws Exception {
        mockMvc.perform(get("/investigationStatusTransitionNotAllowed"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenBpnEdcMappingNotFoundException_handler_respondsNotFound() throws Exception {
        mockMvc.perform(get("/bpnEdcMappingNotFoundException"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvestigationReceiverBpnMismatchException_handler_respondsBadRequest() throws Exception {
        mockMvc.perform(get("/investigationReceiverBpnMismatchException"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidationException_handler_respondsBadRequest() throws Exception {
        mockMvc.perform(get("/validationException"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvestigationIllegalUpdate_handler_respondsForbidden() throws Exception {
        mockMvc.perform(get("/investigationIllegalUpdate"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenIllegalArgumentException_handler_respondsBadRequest() throws Exception {
        mockMvc.perform(get("/illegalArgumentException"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAccessDeniedException_handler_respondsForbidden() throws Exception {
        mockMvc.perform(get("/accessDeniedException"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenTechnicalUserAuthorizationException_handler_respondsInternalServerError() throws Exception {
        mockMvc.perform(get("/technicalUserAuthorizationException"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenAuthenticationCredentialsNotFoundException_handler_respondsUnauthorized() throws Exception {
        mockMvc.perform(get("/authenticationCredentialsNotFoundException"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNotificationStatusTransitionNotAllowed_handler_respondsBadRequest() throws Exception {
        mockMvc.perform(get("/notificationStatusTransitionNotAllowed"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenUpdateQualityNotificationValidationException_handler_respondsBadRequest() throws Exception {
        mockMvc.perform(get("/updateQualityNotificationValidationException"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenCreateNotificationContractException_handler_respondsInternalServerError() throws Exception {
        mockMvc.perform(get("/createNotificationContractException"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenException_handler_respondsInternalServerError() throws Exception {
        mockMvc.perform(get("/exception"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenAuthenticationException_handler_respondsUnauthorized() throws Exception {
        // given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final ServletOutputStream os = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(os);
        final String expectedOsOutput = objectMapper.writeValueAsString(
                new ErrorResponse("msg")
        );

        // when
        new ErrorHandlingConfig(objectMapper)
                .onAuthenticationFailure(request, response, new AuthenticationExceptionTest());

        // then
        verify(response, times(1))
                .setStatus(HttpStatus.UNAUTHORIZED.value());
        verify(response, times(1))
                .getOutputStream();
        verify(os, times(1))
                .println(expectedOsOutput);
    }


    private static class AuthenticationExceptionTest extends AuthenticationException {

        public AuthenticationExceptionTest() {
            super("msg");
        }
    }

    @Hidden
    @RestController
    private static class DummyController {

        @GetMapping("/methodArgumentNotValidException")
        public void methodArgumentNotValidException() throws MethodArgumentNotValidException {
            MethodParameter methodParameter = mock(MethodParameter.class);
            BindingResult bindingResult = mock(BindingResult.class);
            throw new MethodArgumentNotValidException(methodParameter, bindingResult);
        }

        @GetMapping("/jpaSystemException")
        public void jpaSystemException() {
            throw new JpaSystemException(new RuntimeException());
        }

        @GetMapping("/httpMessageNotReadableException")
        public void httpMessageNotReadableException() {
            byte[] message = new byte[]{};
            throw new HttpMessageNotReadableException(
                    "",
                    new MockHttpInputMessage(message));
        }

        @GetMapping("/assetNotFoundException")
        public void assetNotFoundException() {
            throw new AssetNotFoundException("");
        }

        @GetMapping("/investigationNotFoundException")
        public void investigationNotFoundException() {
            throw new InvestigationNotFoundException("");
        }

        @GetMapping("/investigationStatusTransitionNotAllowed")
        public void investigationStatusTransitionNotAllowed() {
            throw new InvestigationStatusTransitionNotAllowed(
                    new QualityNotificationId(1L),
                    QualityNotificationStatus.CLOSED,
                    QualityNotificationStatus.ACCEPTED
            );
        }

        @GetMapping("/bpnEdcMappingNotFoundException")
        public void bpnEdcMappingNotFoundException() {
            throw new BpnNotFoundException("");
        }

        @GetMapping("/investigationReceiverBpnMismatchException")
        public void investigationReceiverBpnMismatchException() {
            throw new InvestigationReceiverBpnMismatchException("");
        }

        @GetMapping("/validationException")
        public void validationException() {
            throw new ValidationException("");
        }

        @GetMapping("/investigationIllegalUpdate")
        public void investigationIllegalUpdate() {
            throw new InvestigationIllegalUpdate("");
        }

        @GetMapping("/illegalArgumentException")
        public void illegalArgumentException() {
            throw new IllegalArgumentException("");
        }

        @GetMapping("/accessDeniedException")
        public void accessDeniedException() {
            throw new AccessDeniedException("");
        }

        @GetMapping("/technicalUserAuthorizationException")
        public void technicalUserAuthorizationException() {
            throw new TechnicalUserAuthorizationException("");
        }

        @GetMapping("/authenticationCredentialsNotFoundException")
        public void authenticationCredentialsNotFoundException() {
            throw new AuthenticationCredentialsNotFoundException("");
        }

        @GetMapping("/notificationStatusTransitionNotAllowed")
        public void notificationStatusTransitionNotAllowed() {
            throw new NotificationStatusTransitionNotAllowed(
                    "",
                    QualityNotificationStatus.CLOSED,
                    QualityNotificationStatus.ACCEPTED);
        }

        @GetMapping("/updateQualityNotificationValidationException")
        public void updateQualityNotificationValidationException() {
            throw new UpdateQualityNotificationValidationException("");
        }

        @GetMapping("/createNotificationContractException")
        public void createNotificationContractException() {
            throw new CreateNotificationContractException("");
        }

        @GetMapping("/exception")
        public void exception() throws Exception {
            throw new Exception("");
        }
    }
}
