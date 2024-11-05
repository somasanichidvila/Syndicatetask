import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
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
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)
public class HelloWorld implements RequestHandler<Map<String, Object>, Map<String, Object>> {

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
		Map<String, Object> requestContext = (Map<String, Object>) request.get("requestContext");
		Map<String, Object> httpContext = (Map<String, Object>) requestContext.get("http");
		String path = (String) httpContext.get("path");
		String method = (String) httpContext.get("httpMethod");

		Map<String, Object> response = new HashMap<>();

		if ("/hello".equals(path) && "GET".equals(method)) {
			response.put("statusCode", 200);
			response.put("body", "{\"message\": \"Hello from Lambda\"}");
		} else {
			String errorMessage = String.format("{\"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}", path, method);
			response.put("statusCode", 400);
			response.put("body", errorMessage);
		}

		return response;
	}
}