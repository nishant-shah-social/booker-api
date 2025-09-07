package api.setup;

import config.Constants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import pojo.BookingDates;
import pojo.BookingRequest;
import pojo.BookingResponse;
import pojo.CreateBookingResponse;
import utils.RestClient;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static utils.DateUtil.adjustDateToLocalDate;

public abstract class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;
    protected RestClient client;
    protected String baseUrl = Constants.getBaseUrl();
    protected String username = Constants.getUsername();
    protected String password = Constants.getPassword();
    protected BookingFactory bookingFactory;

    @BeforeClass
    public void setupBaseConfiguration() {
        // Global REST-assured configuration
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Build request specification
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUrl)
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
        bookingFactory = new BookingFactory(client);
    }

}
