package org.eclipse.tractusx.traceability.integration.common.support;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.springframework.http.HttpStatus;

/**
 * Custom Hamcrest matcher that checks whether response status code is 403 (Forbidden) and matches that against
 * an expected result.
 */
public class ForbiddenMatcher extends BaseMatcher<Integer> {
    private final boolean isForbidden;

    public ForbiddenMatcher(boolean isForbidden) {
        this.isForbidden = isForbidden;
    }

    @Override
    public boolean matches(Object o) {
        return ((int)o == HttpStatus.FORBIDDEN.value()) != isForbidden;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(isForbidden);
    }
}
