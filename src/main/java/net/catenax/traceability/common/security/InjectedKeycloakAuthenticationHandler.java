package net.catenax.traceability.common.security;

import org.jetbrains.annotations.NotNull;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InjectedKeycloakAuthenticationHandler implements HandlerMethodArgumentResolver {

	private static final Logger logger = LoggerFactory.getLogger(InjectedKeycloakAuthenticationHandler.class);

	private final String resourceRealm;

	public InjectedKeycloakAuthenticationHandler(String resourceRealm) {
		this.resourceRealm = resourceRealm;
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(InjectedKeycloakAuthentication.class);
	}

	@Override
	public Object resolveArgument(@NotNull MethodParameter parameter,
								  ModelAndViewContainer mavContainer,
								  @NotNull NativeWebRequest webRequest,
								  WebDataBinderFactory binderFactory) {
		Object details = Optional.ofNullable(SecurityContextHolder.getContext())
			.flatMap(it -> Optional.ofNullable(it.getAuthentication()))
			.flatMap(it -> Optional.ofNullable(it.getDetails()))
			.orElseThrow(() -> new IllegalStateException("No valid keycloak authentication found."));

		if (details instanceof SimpleKeycloakAccount simpleKeycloakAccount) {
			RefreshableKeycloakSecurityContext keycloakSecurityContext = simpleKeycloakAccount.getKeycloakSecurityContext();

			AccessToken token = keycloakSecurityContext.getToken();

			Map<String, AccessToken.Access> resourceAccess = token.getResourceAccess();

			AccessToken.Access access = resourceAccess.get(resourceRealm);

			if (access != null) {
				Set<KeycloakRole> keycloakRoles = access.getRoles().stream()
					.map(KeycloakRole::parse)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toSet());

				return new KeycloakAuthentication(keycloakRoles);
			} else {
				logger.warn("Keycloak token didn't contain {} resource realm roles", resourceRealm);

				return KeycloakAuthentication.NO_ROLES;
			}
		}

		throw new IllegalStateException("No valid keycloak authentication found.");
	}
}
