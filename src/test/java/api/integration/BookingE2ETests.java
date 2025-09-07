package api.integration;

import api.setup.BaseTest;
import api.setup.BookingFactory;
import config.Endpoints;
import org.json.JSONObject;
import org.testng.annotations.Test;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;
import pojo.BookingResponse;
import utils.Auth;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static utils.DateUtil.adjustDate;

public class BookingE2ETests extends BaseTest {
    Auth auth;

    @Test
    public void bookingE2eFlow() throws IOException {
        //create a new booking
        BookingRequest bookingRequests =
                BookingFactory.loadBookingRequests("src/test/resources/bookingData_single.json")
                              .get(0);
        CreateBookingResponse createBookingResponse = bookingFactory.createBooking(bookingRequests);

        auth = new Auth(client);
        String token = auth.login(username, password);

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
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .withJsonBody(patchBookingRequestBody)
                .patch(Endpoints.BOOKING_BY_ID, 200)
                .as(BookingResponse.class);

        assertThat(patchedResponse.getBookingdates().getCheckin().toString(), is(bookingDates.get("checkin")));
        assertThat(patchedResponse.getBookingdates().getCheckout().toString(), is(bookingDates.get("checkout")));
        assertThat(patchedResponse.getFirstname(), is(patchBookingRequestBody.get("firstname")));

        // delete the booking
        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .delete(Endpoints.BOOKING_BY_ID, 201);

        //verifying the booking actually get deleted
        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .get(Endpoints.BOOKING_BY_ID, 404);

    }
}
