package api.integration;

import api.setup.BaseTest;
import api.setup.BookingFactory;
import config.Constants;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.annotations.Test;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;
import pojo.LoginRequest;
import pojo.BookingResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static utils.Auth.login;
import static utils.DateUtil.adjustDate;

public class Bookinge2eTests extends BaseTest {

    @Test
    public void bookinge2eflow() throws IOException {
        //create a new booking
        BookingRequest bookingRequests =
                BookingFactory.loadBookingRequests("src/test/resources/bookingData_single.json")
                              .get(0);
        CreateBookingResponse createBookingResponse = BookingFactory.createBooking(bookingRequests, requestSpec, responseSpec);

        //login and generate token for patch and delete
        LoginRequest loginRequest = new LoginRequest(Constants.USERNAME, Constants.PASSWORD);
        String token = login(loginRequest, requestSpec, responseSpec);

        // patch the above created booking with checkin, checkout and firstname
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

        assertThat(patchedResponse.getBookingdates().getCheckin().toString(), is(bookingDates.get("checkin")));
        assertThat(patchedResponse.getBookingdates().getCheckout().toString(), is(bookingDates.get("checkout")));
        assertThat(patchedResponse.getFirstname(), is(patchBookingRequestBody.get("firstname")));

        // delete the booking
        client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .delete("/booking/{id}");

        //verifying the booking actually get deleted
        client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .get("/booking/{id}", 404);

    }
}
