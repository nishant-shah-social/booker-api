package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import io.restassured.http.Method;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class RestClient {
    private static final Logger LOGGER = LogManager.getLogger(RestClient.class);

    private final RequestSpecification requestSpec;
    private final ResponseSpecification responseSpec;

    // Mutable request state
    private RequestSpecification currentRequest;

    public RestClient(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        this.requestSpec = requestSpec;
        this.responseSpec = responseSpec;
        reset();
    }

    private void reset() {
        currentRequest = given().spec(requestSpec);
        LOGGER.debug("Request specification reset to base configuration");
    }

    public RestClient withToken(String token) {
        currentRequest.cookie("token", token);
        LOGGER.debug("Auth token cookie set (value not logged for security)");
        return this;
    }

    public RestClient withPathParam(String key, Object value) {
        currentRequest.pathParam(key, value);
        LOGGER.debug("Path param set: {}={}", key, value);
        return this;
    }

    public RestClient withPathParams(Map<String, ?> params) {
        currentRequest.pathParams(params);
        LOGGER.debug("Path params set: {}", params);
        return this;
    }

    public RestClient withQueryParam(String key, Object value) {
        currentRequest.queryParam(key, value);
        LOGGER.debug("Query param set: {}={}", key, value);
        return this;
    }

    public RestClient withJsonBody(JSONObject jsonBody) {
        if (jsonBody == null) {
            throw new IllegalArgumentException("JSON body must not be null");
        }
        currentRequest.body(jsonBody.toString());
        LOGGER.debug("JSON body set: {}", jsonBody);
        return this;
    }

    private Response execute(Method method, String endpoint, int expectedStatusCode) {
        long start = System.currentTimeMillis();
        LOGGER.info("{} {}", method, endpoint);
        try {
            Response response = currentRequest
                    .when()
                    .request(method, endpoint)
                    .then()
                    .statusCode(expectedStatusCode)
                    .spec(responseSpec)
                    .extract()
                    .response();

            long duration = System.currentTimeMillis() - start;
            LOGGER.info("{} {} -> {} ({} ms)", method, endpoint, response.getStatusCode(), duration);

            return response;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            LOGGER.error("{} {} failed ({} ms)", method, endpoint, duration, e);
            throw e;
        } finally {
            reset();
        }
    }

    // Convenience wrappers
    public Response get(String endpoint, int expectedStatusCode) {
        return execute(Method.GET, endpoint, expectedStatusCode);
    }

    public Response post(String endpoint, int expectedStatusCode) {
        return execute(Method.POST, endpoint, expectedStatusCode);
    }

    public Response put(String endpoint, int expectedStatusCode) {
        return execute(Method.PUT, endpoint, expectedStatusCode);
    }

    public Response patch(String endpoint, int expectedStatusCode) {
        return execute(Method.PATCH, endpoint, expectedStatusCode);
    }

    public Response delete(String endpoint, int expectedStatusCode) {
        return execute(Method.DELETE, endpoint, expectedStatusCode);
    }

}

