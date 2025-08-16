package api.booking;

import api.setup.BaseTest;
import api.setup.BookingFactory;
import config.Constants;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;
import pojo.LoginRequest;
import pojo.BookingResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static utils.Auth.login;
import static utils.DateUtil.adjustDate;

public class PatchBookingTests extends BaseTest {
    private String token = "";

    @BeforeClass
    public void setupAuth() {
        LoginRequest loginRequest = new LoginRequest(Constants.USERNAME, Constants.PASSWORD);
        token = login(loginRequest, requestSpec, responseSpec);
    }

    @DataProvider(name = "bookingData")
    public Object[][] setupBookings() throws IOException {
        BookingRequest bookingRequests =
                BookingFactory.loadBookingRequests("src/test/resources/bookingData.json")
                              .get(0);
        CreateBookingResponse response = BookingFactory.createBooking(bookingRequests, requestSpec, responseSpec);
        return new Object[][]{
                {response}
        };
    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_firstname_noChangeInOtherFields(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("firstname", "Maestro");

        BookingResponse patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);

        // Assert firstname is updated and all other fields remain unchanged
        Map<String, Object> expectedUpdates = Map.of(
                "firstname", patchBookingRequestBody.get("firstname")
        );
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);
    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_lastname_noChangeInOtherFields(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("lastname", "Bond");

        BookingResponse patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);

        // Assert lastname is updated and all other fields remain unchanged
        Map<String, Object> expectedUpdates = Map.of(
                "lastname", patchBookingRequestBody.get("lastname")
        );
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);
    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_depositPaid_noChangeInOtherFields(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("depositpaid", false);

        BookingResponse patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);

        // Assert depositpaid is updated and all other fields remain unchanged
        Map<String, Object> expectedUpdates = Map.of(
                "depositpaid", patchBookingRequestBody.get("depositpaid")
        );
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);


    }


    @Test(dataProvider = "bookingData")
    public void patchBooking_checkin_noChangeInOtherFields(CreateBookingResponse createBookingResponse) {
        String checkinDateParam =
                adjustDate(
                        createBookingResponse
                                .getBooking()
                                .getBookingdates()
                                .getCheckin(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        JSONObject bookingDates = new JSONObject();
        bookingDates.put("checkin", checkinDateParam);
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("bookingdates", bookingDates);

        BookingResponse patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);

        // Assert checkin is updated and all other fields remain unchanged
        Map<String, Object> expectedUpdates = Map.of(
                "checkin", bookingDates.get("checkin")
        );
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);


    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_checkout_noChangeInOtherFields(CreateBookingResponse createBookingResponse) {
        String checkoutDateParam =
                adjustDate(
                        createBookingResponse
                                .getBooking()
                                .getBookingdates()
                                .getCheckout(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        JSONObject bookingDates = new JSONObject();
        bookingDates.put("checkout", checkoutDateParam);
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("bookingdates", bookingDates);

        BookingResponse patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);

        // Assert checkout is updated and all other fields remain unchanged
        Map<String, Object> expectedUpdates = Map.of(
                "checkout", bookingDates.get("checkout")
        );
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);

    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_firstName_checkin_checkout_noChangeInOtherFields(CreateBookingResponse createBookingResponse) {
        String checkinDateParam =
                adjustDate(
                        createBookingResponse
                                .getBooking()
                                .getBookingdates()
                                .getCheckin(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam =
                adjustDate(
                        createBookingResponse
                                .getBooking()
                                .getBookingdates()
                                .getCheckout(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        JSONObject bookingDates = new JSONObject();
        bookingDates.put("checkout", checkoutDateParam);
        bookingDates.put("checkin", checkinDateParam);
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("bookingdates", bookingDates);
        patchBookingRequestBody.put("firstname", "bond");

        BookingResponse patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);

        // Assert checkout is updated and all other fields remain unchanged
        Map<String, Object> expectedUpdates = Map.of(
                "checkout", bookingDates.get("checkout"),
                "checkin", bookingDates.get("checkin"),
                "firstname", patchBookingRequestBody.get("firstname")
        );
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);

    }

    @Test
    public void patchBooking_non_existent_bookingId() {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("firstname", "Maestro");

        Response response = client
                .withToken(token)
                .withPathParam("id", 2147483647)
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", 404);
    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_incorrect_data_type_firstname_totalprice(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("totalprice", "test");
        patchBookingRequestBody.put("firstname", 123123);

        Response response = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", 400);
    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_missing_auth_token_403(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("firstname", "Bond");

        Response response = client
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", 403);
    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_wrong_auth_token_403(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("firstname", "Bond");

        Response response = client
                .withToken("testtoken")
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", 403);
    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_idemptoencyTest(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("firstname", "Maestro");

        BookingResponse patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);

        // Assert firstname is updated and all other fields remain unchanged
        Map<String, Object> expectedUpdates = Map.of(
                "firstname", patchBookingRequestBody.get("firstname")
        );
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);

        // performing patch again to check for idempotency
        patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);

    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_specialCharacters_encoding(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("firstname", "O'Connor<testing>");
        patchBookingRequestBody.put("lastname", "测试");

        BookingResponse patchedResponse = client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .withBody(patchBookingRequestBody.toString())
                .patch("/booking/{id}", BookingResponse.class);

        // Assert firstname and lastname is updated and all other fields remain unchanged
        Map<String, Object> expectedUpdates = Map.of(
                "firstname", patchBookingRequestBody.get("firstname"),
                "lastname", patchBookingRequestBody.get("lastname")
        );
        assertBookingFields(patchedResponse, createBookingResponse, expectedUpdates);
    }

    @Test(dataProvider = "bookingData")
    public void patchBooking_passing_non_existent_field(CreateBookingResponse createBookingResponse) {
        JSONObject patchBookingRequestBody = new JSONObject();
        patchBookingRequestBody.put("address", "Rotterdam");

        RequestSpecification request = given()
                .spec(requestSpec)
                .cookie("token", token)
                .pathParams("id", createBookingResponse.getBookingid())
                .body(patchBookingRequestBody.toString());

        request
                .when()
                .patch("/booking/{id}")
                .then()
                .spec(responseSpec)
                .statusCode(400);
    }

    public void assertBookingFields(BookingResponse actual,
                                    CreateBookingResponse original,
                                    Map<String, Object> expectedUpdates) {
        // Check firstname
        if (expectedUpdates.containsKey("firstname")) {
            assertThat("firstname should be updated correctly",
                    actual.getFirstname(),
                    is(expectedUpdates.get("firstname")));
        } else {
            assertThat("firstname should remain unchanged",
                    actual.getFirstname(),
                    is(original.getBooking().getFirstname()));
        }

        // Check lastname
        if (expectedUpdates.containsKey("lastname")) {
            assertThat("lastname should be updated correctly",
                    actual.getLastname(),
                    is(expectedUpdates.get("lastname")));
        } else {
            assertThat("lastname should remain unchanged",
                    actual.getLastname(),
                    is(original.getBooking().getLastname()));
        }

        // Check totalprice
        if (expectedUpdates.containsKey("totalprice")) {
            assertThat("totalprice should be updated correctly",
                    actual.getTotalprice(),
                    is(expectedUpdates.get("totalprice")));
        } else {
            assertThat("totalprice should remain unchanged",
                    actual.getTotalprice(),
                    is(original.getBooking().getTotalprice()));
        }

        // Check depositpaid
        if (expectedUpdates.containsKey("depositpaid")) {
            assertThat("depositpaid should be updated correctly",
                    actual.isDepositpaid(),
                    is(expectedUpdates.get("depositpaid")));
        } else {
            assertThat("depositpaid should remain unchanged",
                    actual.isDepositpaid(),
                    is(original.getBooking().isDepositpaid()));
        }

        // Check additionalneeds
        if (expectedUpdates.containsKey("additionalneeds")) {
            assertThat("additionalneeds should be updated correctly",
                    actual.getAdditionalneeds(),
                    is(expectedUpdates.get("additionalneeds")));
        } else {
            assertThat("additionalneeds should remain unchanged",
                    actual.getAdditionalneeds(),
                    is(original.getBooking().getAdditionalneeds()));
        }

        // Check bookingdates.checkin
        if (expectedUpdates.containsKey("checkin")) {
            assertThat("bookingdates.checkin should be updated correctly",
                    actual.getBookingdates().getCheckin().toString(),
                    is(expectedUpdates.get("checkin")));
        } else {
            assertThat("bookingdates.checkin should remain unchanged",
                    actual.getBookingdates().getCheckin(),
                    is(original.getBooking().getBookingdates().getCheckin()));
        }

        // Check bookingdates.checkout
        if (expectedUpdates.containsKey("checkout")) {
            assertThat("bookingdates.checkout should be updated correctly",
                    actual.getBookingdates().getCheckout().toString(),
                    is(expectedUpdates.get("checkout")));
        } else {
            assertThat("bookingdates.checkout should remain unchanged",
                    actual.getBookingdates().getCheckout(),
                    is(original.getBooking().getBookingdates().getCheckout()));
        }
    }


}
