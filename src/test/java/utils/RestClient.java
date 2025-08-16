package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class RestClient {

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
    }

    public RestClient withToken(String token) {
        currentRequest.cookie("token", token);
        return this;
    }

    public RestClient withPathParam(String key, Object value) {
        currentRequest.pathParam(key, value);
        return this;
    }

    public RestClient withPathParams(Map<String, ?> params) {
        currentRequest.pathParams(params);
        return this;
    }

    public RestClient withQueryParam(String key, Object value) {
        currentRequest.queryParam(key, value);
        return this;
    }

    public RestClient withBody(Object body) {
        currentRequest.body(body);
        return this;
    }

    public Response get(String endpoint) {
        return get(endpoint, 200);
    }

    public <T> T get(String endpoint, Class<T> responseClass) {
        return get(endpoint, responseClass, 200);
    }

    public Response get(String endpoint, int expectedStatusCode) {
        try {
            Response response = currentRequest
                    .when()
                    .get(endpoint)
                    .then()
                    .statusCode(expectedStatusCode)
                    .spec(responseSpec)
                    .extract()
                    .response();
            return response;
        } finally {
            reset();
        }
    }

    public Response post(String endpoint) {
        try {
            Response response = currentRequest
                    .when()
                    .post(endpoint)
                    .then()
                    .spec(responseSpec)
                    .extract()
                    .response();
            return response;
        } finally {
            reset();
        }
    }

    public Response patch(String endpoint) {
        return patch(endpoint, 200);
    }

    public <T> T patch(String endpoint, Class<T> responseClass) {
        return patch(endpoint, responseClass, 200);
    }

    public Response patch(String endpoint, int expectedStatus) {
        try {
            Response response = currentRequest
                    .when()
                    .patch(endpoint)
                    .then()
                    .statusCode(expectedStatus)
                    .spec(responseSpec)
                    .extract()
                    .response();
            return response;
        } finally {
            reset();
        }
    }

    public Response delete(String endpoint) {
        return delete(endpoint, 201);
    }

    public Response delete(String endpoint, int expectedStatusCode) {
        try {
            Response response = currentRequest
                    .when()
                    .delete(endpoint)
                    .then()
                    .statusCode(expectedStatusCode)
                    .spec(responseSpec)
                    .extract()
                    .response();
            return response;
        } finally {
            reset();
        }
    }

    // ==== Typed responses ====
    public <T> T get(String endpoint, Class<T> responseClass, int expectedStatusCode) {
        try {
            T response = currentRequest
                    .when()
                    .get(endpoint)
                    .then()
                    .statusCode(expectedStatusCode)
                    .spec(responseSpec)
                    .extract()
                    .as(responseClass);
            return response;
        } finally {
            reset();
        }
    }

    public <T> T post(String endpoint, Class<T> responseClass) {
        try {
            T response = currentRequest
                    .when()
                    .post(endpoint)
                    .then()
                    .spec(responseSpec)
                    .extract()
                    .as(responseClass);
            reset();
            return response;
        } finally {
            reset();
        }
    }

    public <T> T patch(String endpoint, Class<T> responseClass, int expectedStatusCode) {
        try {
            T response = currentRequest
                    .when()
                    .patch(endpoint)
                    .then()
                    .statusCode(expectedStatusCode)
                    .spec(responseSpec)
                    .extract()
                    .as(responseClass);
            return response;
        } finally {
            reset();
        }
    }

    public <T> List<T> getList(String endpoint, String jsonPath, Class<T> type) {
        try {
            List<T> result = currentRequest
                    .when()
                    .get(endpoint)
                    .then()
                    .spec(responseSpec)
                    .extract()
                    .jsonPath()
                    .getList(jsonPath, type);
            return result;
        } finally {
            reset();
        }
    }
}

