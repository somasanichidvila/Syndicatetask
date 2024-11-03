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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		if (request.getPath().equals("/hello") && request.getHttpMethod().equalsIgnoreCase("GET")) {
			response.setStatusCode(200);
			response.setBody("{\"statusCode\": 200, \"message\": \"Hello from Lambda\"}");
		} else {
			response.setStatusCode(400);
			response.setBody("{\"statusCode\": 400, \"message\": \"Bad request syntax or unsupported method. Request path: "
					+ request.getPath()
					+ ". HTTP method: "
					+ request.getHttpMethod()
					+ "\"}");
		}
		response.setHeaders(Collections.singletonMap("Content-Type", "application/json"));
		return response;
	}
}