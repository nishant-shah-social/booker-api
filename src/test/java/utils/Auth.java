package utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import pojo.LoginRequest;

public class Auth {
    public static String login(LoginRequest loginRequest,
                               RequestSpecification requestSpec,
                               ResponseSpecification responseSpec) {
        return RestAssured.given()
                          .spec(requestSpec)
                          .body(loginRequest)
                          .post("/auth")
                          .then()
                          .spec(responseSpec)
                          .statusCode(200)
                          .extract()
                          .path("token");
    }
}
