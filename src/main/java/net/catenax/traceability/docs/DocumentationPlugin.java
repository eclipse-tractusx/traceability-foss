package net.catenax.traceability.docs;

import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

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
			.summary(getProperty(operationKey + ".summary"));
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

	@Override
	public boolean supports(DocumentationType delimiter) {
		return true;
	}
}
