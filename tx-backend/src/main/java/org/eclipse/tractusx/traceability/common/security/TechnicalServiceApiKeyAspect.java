package org.eclipse.tractusx.traceability.common.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.eclipse.tractusx.traceability.common.security.exception.InvalidApiKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class TechnicalServiceApiKeyAspect {

    @Value("${traceability.technicalServiceApiKey}")
    private String apiKey;

    @Pointcut("@annotation(org.eclipse.tractusx.traceability.common.security.TechnicalServiceApiKeyValidator) || @within(org.eclipse.tractusx.traceability.common.security.TechnicalServiceApiKeyValidator)")
    public void apiKeyPointcut() {}

    @Before("apiKeyPointcut()")
    public void checkApiKey() {
        final ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new InvalidApiKeyException("Missing API Key in request header");
        }

        final HttpServletRequest request = attributes.getRequest();
        final String extractedApiKey = request.getHeader("x-technical-service-key");

        if (extractedApiKey == null || extractedApiKey.isEmpty()) {
            throw new InvalidApiKeyException("Missing API Key in request header");
        }


        if (!extractedApiKey.equals(apiKey)) {
             throw new InvalidApiKeyException("Invalid API Key");
        }
    }
}
