package net.catenax.traceability.common.docs;

import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Collection;
import java.util.List;

@Component
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
public class DocumentationPlugin implements OperationBuilderPlugin, ParameterBuilderPlugin {

	private final Environment environment;

	public DocumentationPlugin(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void apply(OperationContext context) {
		String operationKey = getOperationKey(context);

		context.operationBuilder()
			.notes(getProperty(operationKey + ".notes"))
			.summary(getProperty(operationKey + ".summary"))
			.authorizations(buildJWTAuthentication(operationKey));
	}

	@Override
	public void apply(ParameterContext parameterContext) {
		String operationKey = getOperationKey(parameterContext.getOperationContext());
		String parameterKey = operationKey + ".params." + parameterContext
			.resolvedMethodParameter()
			.defaultName()
			.orElse("default");

		parameterContext.requestParameterBuilder()
			.description(getProperty(parameterKey));
	}

	private String getOperationKey(OperationContext context) {
		return "operations." + context.requestMappingPattern()
			.replaceAll("/", ".")
			.replaceAll("\\{", "")
			.replaceAll("\\}", "")
			.substring(1);
	}

	private String getProperty(String key) {
		return environment.getProperty("swagger." + key);
	}

	private Collection<SecurityReference> buildJWTAuthentication(String operationKey) {
		String authKey = operationKey + ".authorizations";

		String authProperty = getProperty(authKey);

		return List.of(new SecurityReference(authProperty, new AuthorizationScope[]{}));
	}

	@Override
	public boolean supports(DocumentationType delimiter) {
		return true;
	}
}
