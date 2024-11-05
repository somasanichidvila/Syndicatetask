import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

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

	private static final String SUCCESS_MESSAGE = "{\"message\": \"Hello from Lambda\"}";
	private static final String BAD_REQUEST_MESSAGE_TEMPLATE = "{\"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}";
	private static final String SUPPORTED_PATH = "/hello";
	private static final String SUPPORTED_METHOD = "GET";

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
		String path = extractPathFromRequest(request);
		String method = extractMethodFromRequest(request);

		return generateResponse(path, method);
	}

	private String extractPathFromRequest(Map<String, Object> request) {
		Map<String, Object> requestContext = (Map<String, Object>) request.get("requestContext");
		Map<String, Object> httpContext = (Map<String, Object>) requestContext.get("http");
		return (String) httpContext.get("path");
	}

	private String extractMethodFromRequest(Map<String, Object> request) {
		Map<String, Object> requestContext = (Map<String, Object>) request.get("requestContext");
		Map<String, Object> httpContext = (Map<String, Object>) requestContext.get("http");
		return (String) httpContext.get("method");
	}

	private Map<String, Object> generateResponse(String path, String method) {
		Map<String, Object> response = new HashMap<>();
		if (SUPPORTED_PATH.equals(path) && SUPPORTED_METHOD.equals(method)) {
			response.put("statusCode", 200);
			response.put("body", SUCCESS_MESSAGE);
		} else {
			String errorMessage = String.format(BAD_REQUEST_MESSAGE_TEMPLATE, path, method);
			response.put("statusCode", 400);
			response.put("body", errorMessage);
		}
		return response;
	}
}
