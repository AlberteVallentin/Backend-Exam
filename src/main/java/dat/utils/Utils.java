package dat.utils;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dat.exceptions.ApiException;
import io.javalin.http.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {

    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    public static String getPropertyValue(String propName, String resourceName) throws ApiException {
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new ApiException(500, String.format("Resource %s not found", resourceName));
            }

            Properties prop = new Properties();
            prop.load(is);

            String value = prop.getProperty(propName);
            if (value != null) {
                return value.trim();
            }
            throw new ApiException(500, String.format("Property %s not found in %s", propName, resourceName));

        } catch (IOException ex) {
            throw new ApiException(500, String.format("Could not read property %s: %s", propName, ex.getMessage()));
        }
    }

    public static String convertToJsonMessage(Context ctx, String property, String message) {
        Map<String, String> msgMap = new HashMap<>();
        msgMap.put(property, message);
        msgMap.put("status", String.valueOf(ctx.status()));
        msgMap.put("timestamp", getCurrentTimestamp());

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(msgMap);
        } catch (Exception e) {
            return String.format("{\"error\": \"Could not convert message to JSON\", \"message\": \"%s\"}", message);
        }
    }

    public static String getCurrentTimestamp() {
        return java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }
}
