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
package org.eclipse.tractusx.traceability.common.security.apikey;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

@Getter
@Service
@Slf4j
public class ApiKeyFeatureService {
    public static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    private final List<String> apiKeyRequestMapping;

    public ApiKeyFeatureService() {
        apiKeyRequestMapping = getAnnotatedPaths();
    }


    private List<String> getAnnotatedPaths() {
        Set<Method> methods = new Reflections("org.eclipse.tractusx.traceability",
                Scanners.MethodsAnnotated).getMethodsAnnotatedWith(ApiKeyEnabled.class);

        List<String> methodRequestMapping = methods.stream()
                .map(method -> {
                    String classRequestMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class).path()[0];
                    String classPathRequestMatchers = toRequestMatcherPattern(classRequestMapping);

                    if (method.getAnnotation(PostMapping.class) != null) {
                        return classPathRequestMatchers + toRequestMatcherPattern(method.getAnnotation(PostMapping.class).value()[0]);
                    }
                    if (method.getAnnotation(GetMapping.class) != null) {
                        return classPathRequestMatchers + toRequestMatcherPattern(method.getAnnotation(GetMapping.class).value()[0]);
                    }
                    if (method.getAnnotation(PutMapping.class) != null) {
                        return classPathRequestMatchers + toRequestMatcherPattern(method.getAnnotation(PutMapping.class).value()[0]);
                    }
                    if (method.getAnnotation(DeleteMapping.class) != null) {
                        return classPathRequestMatchers + toRequestMatcherPattern(method.getAnnotation(DeleteMapping.class).value()[0]);
                    }
                    return null;
                })
                .distinct()
                .toList();
        log.info("Found the following annotated paths: {}", methodRequestMapping);
        return methodRequestMapping;
    }

    private String toRequestMatcherPattern(String string) {
        return StringUtils.isEmpty(string) ? "" : string.replaceAll("\\{([^{}]*)\\}", "*");
    }

}
