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

	@Override
	public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
		Map<String, Object> payloadRequestContext = (Map<String, Object>) request.get("requestContext");
		Map<String, Object> payloadRequestContextHttp = (Map<String, Object>) payloadRequestContext.get("http");

		String path = (String) payloadRequestContextHttp.get("path");
		String method = (String) payloadRequestContextHttp.get("httpMethod");

		Map<String, Object> responseMap = new HashMap<>();

		if (path != null && method != null) {
			if ("/hello".equals(path) && "GET".equals(method)) {
				responseMap.put("statusCode", 200);
				responseMap.put("body", "{\"message\": \"Hello from Lambda\"}");
			} else {
				responseMap.put("statusCode", 400);
				responseMap.put("body", String.format("{\"message\": \"Bad request syntax or unsupported method. Request path: %s. HTTP method: %s\"}", path, method));
			}
		} else {
			responseMap.put("statusCode", 400);
			responseMap.put("body", "{\"message\": \"Bad request syntax. Request path or method is missing.\"}");
		}

		return responseMap;
	}
}