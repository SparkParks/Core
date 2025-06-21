package network.palace.core.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

/**
 * Handler class for integrating with the Rollbar error tracking and monitoring service.
 * The `RollbarHandler` is responsible for sending error, warning, and informational
 * messages to Rollbar, as well as handling uncaught exceptions and reporting them.
 * <p>
 * This class interacts with the Rollbar API and posts log data such as log levels,
 * environment details, stack traces, and other context to the service for monitoring
 * and debugging purposes.
 */
@RequiredArgsConstructor
public class RollbarHandler {

    /**
     * The access token used for authenticating with the logging service.
     * This token is required to interact with external services such as Rollbar
     * for error reporting and log management.
     * <p>
     * It is a final and immutable string, ensuring that the token value
     * remains constant throughout the lifecycle of the application.
     */
    private final String accessToken;

    /**
     * The environment type in which the application is currently operating.
     * This variable holds a value from the {@code EnvironmentType} enum, which
     * represents predefined constants such as production, staging, or local
     * development environments. It is used to identify and configure behavior
     * specific to the target environment.
     * <p>
     * This field is immutable and final, indicating that the environment cannot
     * be changed once set.
     */
    private final EnvironmentType environment;

    /**
     * The URL endpoint used for sending log or error messages to the Rollbar API.
     * This constant defines the base URL for interacting with Rollbar's error-reporting service.
     * It specifies the API endpoint to which error data is sent.
     */
    private String URL_STRING = "https://api.rollbar.com/api/1/item/";

    /**
     * An instance of {@code ObjectMapper} used for converting
     * Java objects to and from JSON. This utility is provided
     * by the Jackson library and offers comprehensive capabilities
     * for serialization and deserialization of data.
     * <p>
     * The {@code mapper} variable is essential for facilitating
     * JSON processing, making it easier to interact with external
     * services or store structured data in a JSON format.
     */
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Watch for unhandled errors
     */
//    public void watch() {
//        Thread.getAllStackTraces().keySet().forEach(thread -> thread.setUncaughtExceptionHandler((t, e) -> {
//            error(e);
//        }));
//    }
//
//    public void info(String message) {
//        Core.runTaskAsynchronously(() -> post(URL_STRING, build("info", message, null)));
//    }
//
//    public void info(Throwable throwable) {
//        Core.runTaskAsynchronously(() -> post(URL_STRING, build("info", null, throwable)));
//    }
//
//    public void warning(String message) {
//        Core.runTaskAsynchronously(() -> post(URL_STRING, build("warning", message, null)));
//    }
//
//    public void warning(Throwable throwable) {
//        Core.runTaskAsynchronously(() -> post(URL_STRING, build("warning", null, throwable)));
//    }
//
//    public void error(String message) {
//        Core.runTaskAsynchronously(() -> post(URL_STRING, build("error", message, null)));
//    }
//
//    public void error(Throwable throwable) {
//        Core.runTaskAsynchronously(() -> post(URL_STRING, build("error", null, throwable)));
//    }
//
//    private ObjectNode build(String level, String message, Throwable throwable) {
//        ObjectNode payload = JsonNodeFactory.instance.objectNode();
//        payload.put("access_token", this.accessToken);
//        ObjectNode data = JsonNodeFactory.instance.objectNode();
//        data.put("environment", environment.getType());
//        data.put("level", level);
//        data.put("language", "Java");
//        data.put("framework", "Java");
//        data.put("timestamp", System.currentTimeMillis() / 1000);
//        data.set("body", getBody(message, throwable));
//        data.set("request", getRequest());
//        payload.set("data", data);
//        return payload;
//    }
//
//    private ObjectNode getRequest() {
//        ObjectNode request = JsonNodeFactory.instance.objectNode();
//        request.put("user_ip", getIP());
//        return request;
//    }
//
//    private ObjectNode getBody(String message, Throwable original) {
//        // Body node
//        ObjectNode body = JsonNodeFactory.instance.objectNode();
//        // Copy throwable
//        Throwable throwable = original;
//        // Loop through throwable and add it to trace chain
//        if (throwable != null) {
//            List<ObjectNode> traces = new ArrayList<>();
//            do {
//                traces.add(0, createTrace(throwable));
//                throwable = throwable.getCause();
//            } while (throwable != null);
//            ArrayNode tracesArray = JsonNodeFactory.instance.arrayNode();
//            traces.forEach(tracesArray::add);
//            body.set("trace_chain", tracesArray);
//        }
//        // If throwable is null and message exist then add message to body
//        if (original == null && message != null) {
//            ObjectNode messageBody = JsonNodeFactory.instance.objectNode();
//            messageBody.put("body", message);
//            body.set("message", messageBody);
//        }
//        return body;
//    }
//
//    private ObjectNode createTrace(Throwable throwable) {
//        ObjectNode trace = JsonNodeFactory.instance.objectNode();
//        ArrayNode frames = JsonNodeFactory.instance.arrayNode();
//        StackTraceElement[] elements = throwable.getStackTrace();
//        for (int i = elements.length - 1; i >= 0; --i) {
//            StackTraceElement element = elements[i];
//            ObjectNode frame = JsonNodeFactory.instance.objectNode();
//            frame.put("class_name", element.getClassName());
//            frame.put("filename", element.getFileName());
//            frame.put("method", element.getMethodName());
//            if (element.getLineNumber() > 0) {
//                frame.put("lineno", element.getLineNumber());
//            }
//            frames.add(frame);
//        }
//        try {
//            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
//            PrintStream printStream = new PrintStream(byteArray);
//            throwable.printStackTrace(printStream);
//            printStream.close();
//            byteArray.close();
//            trace.put("raw", byteArray.toString("UTF-8"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        ObjectNode exceptionData = JsonNodeFactory.instance.objectNode();
//        exceptionData.put("class", throwable.getClass().getName());
//        exceptionData.put("message", throwable.getMessage());
//        trace.set("frames", frames);
//        trace.set("exception", exceptionData);
//        return trace;
//    }
//
//    private void post(String url, Object data) {
//        try {
//            URL urlObject = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
//            // Set
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11" );
//            connection.setRequestMethod("POST");
//            connection.setDoOutput(true);
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("Accept", "application/json");
//            // Write and send
//            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
//            dataOutputStream.write(mapper.writeValueAsBytes(data));
//            dataOutputStream.flush();
//            dataOutputStream.close();
//            // Done
//            connection.getInputStream();
//        } catch (IOException ignored) {
//        }
//    }
//
//    /**
//     * Get the ip of the server
//     *
//     * @return the ip
//     */
//    private String getIP() {
//        try {
//            return InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            return "Unknown IP";
//        }
//    }
}
