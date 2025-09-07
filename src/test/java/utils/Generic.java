package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Generic {

    private static final Logger LOGGER = LogManager.getLogger(Generic.class);

    /**
     * Converts any Java object to a JSONObject
     *
     * @param <T> The type of the object to convert
     * @param object The Java object to be converted
     * @return JSONObject representation of the object
     * @throws RuntimeException If the conversion fails
     */
    public static <T> JSONObject convertToJson(T object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Ensure proper serialization of dates
            String jsonString = mapper.writeValueAsString(object);
            return new JSONObject(jsonString);
        } catch (Exception e) {
            LOGGER.error("Failed to convert object to JSONObject", e);
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }
}
