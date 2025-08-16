package api.setup;

import config.Constants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;
import utils.RestClient;

import static org.hamcrest.Matchers.lessThan;

public abstract class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;
    protected RestClient client;

    @BeforeClass
    public void setupBaseConfiguration() {
        // Global REST-assured configuration
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Build request specification
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(Constants.BASE_URL)
                .setContentType(ContentType.JSON)
                .build();

        // Build response specification
        responseSpec = new ResponseSpecBuilder()
                .expectResponseTime(lessThan(5000L))
                .build();

        // Set global specifications
        RestAssured.requestSpecification = requestSpec;
        RestAssured.responseSpecification = responseSpec;
        client = new RestClient(requestSpec, responseSpec);
    }
}
