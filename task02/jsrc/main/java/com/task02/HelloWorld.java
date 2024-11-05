package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

@LambdaHandler(
		lambdaName = "hello_world",
		roleName = "hello_world-role",
		isPublishVersion = true,
		aliasName = "${lambdas_alias_name}",
		logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(
		authType = AuthType.NONE, // This means no authentication is required to access the Function URL
		invokeMode = InvokeMode.BUFFERED // This can be set to BUFFERED or STREAMING based on your requirements
)

public class HelloWorld implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		String path = request.getPath();
		String method = request.getHttpMethod();

		// Log the entire request object
		context.getLogger().log("Request: " + request.toString());

		if (path == null || method == null) {
			// Log the null values
			if (path == null) {
				context.getLogger().log("Path is null");
			}
			if (method == null) {
				context.getLogger().log("Method is null");
			}

			// Return 400 Bad Request if the path or method is null
			String errorMessage = "{\"message\": \"Bad request syntax. Request path or method is missing.\"}";
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(400)
					.withBody(errorMessage);
		}

		if ("/hello".equals(path) && "GET".equals(method)) {
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(200)
					.withBody("{\"message\": \"Hello from Lambda\"}");
		} else {
			// Return 400 Bad Request for all other endpoints
			String errorMessage = String.format(
					"{\"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}",
					path, method);
			return new APIGatewayProxyResponseEvent()
					.withStatusCode(400)
					.withBody(errorMessage);
		}
	}
}