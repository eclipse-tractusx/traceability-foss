/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.irs.common.ApiConstants;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

import static org.eclipse.tractusx.traceability.common.config.ApplicationProfiles.NOT_INTEGRATION_TESTS;

@Profile(NOT_INTEGRATION_TESTS)
@Slf4j
public class TrustedEndpointsFilter implements Filter {
    private int trustedPortNum;

    /* package */ TrustedEndpointsFilter(final String trustedPort) {
        try {
            if (StringUtils.isNotEmpty(trustedPort)) {
                trustedPortNum = Integer.parseInt(trustedPort);
            } else {
                trustedPortNum = 0;
            }
        } catch (NumberFormatException e) {
            trustedPortNum = 0;
        }
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {
        if (trustedPortNum != 0) {

            if (isRequestForTrustedEndpoint(servletRequest) && servletRequest.getLocalPort() != trustedPortNum) {
                log.warn("denying request for trusted endpoint on untrusted port");
                if (servletResponse instanceof HttpServletResponseWrapper httpServletResponse) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                servletResponse.getOutputStream().close();
                return;
            }

            if (!isRequestForTrustedEndpoint(servletRequest) && servletRequest.getLocalPort() == trustedPortNum) {
                log.warn("denying request for untrusted endpoint on trusted port");
                if (servletResponse instanceof HttpServletResponseWrapper httpServletResponse) {
                    httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                servletResponse.getOutputStream().close();
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isRequestForTrustedEndpoint(final ServletRequest servletRequest) {
        log.warn(((HttpServletRequestWrapper) servletRequest).getRequestURI());
        return ((HttpServletRequestWrapper) servletRequest).getRequestURI()
                .startsWith(ApplicationConfig.CONTEXT_PATH + ApplicationConfig.INTERNAL_ENDPOINT);
    }
}
