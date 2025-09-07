package api.booking;

import api.setup.BaseTest;
import api.setup.BookingFactory;
import config.Endpoints;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;
import pojo.BookingResponse;
import utils.Assertions;
import utils.Auth;

import java.io.IOException;

import static utils.Generic.convertToJson;

public class PutBookingTests extends BaseTest {
    Auth auth;
    BookingRequest bookingRequest;
    private String token = "";

    @BeforeClass
    public void setupAuth() {
        auth = new Auth(client);
        token = auth.login(username, password);
    }

    @DataProvider(name = "bookingData")
    public Object[][] setupBookings() throws IOException {
        BookingRequest bookingRequests =
                BookingFactory.loadBookingRequests("src/test/resources/bookingData.json")
                              .get(0);
        CreateBookingResponse response = bookingFactory.createBooking(bookingRequests);
        return new Object[][]{
                {response}
        };
    }

    @Test(dataProvider = "bookingData")
    public void putBookingValidParamsUpdateIsSuccessful(CreateBookingResponse createBookingResponse) {
        bookingRequest = bookingFactory.createBookingRequest(
                "Christiano",
                "Ronaldo",
                300,
                false,
                "Double Bed",
                -2,
                2);

        BookingResponse putResponse = client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .withJsonBody(convertToJson(bookingRequest))
                .put(Endpoints.BOOKING_BY_ID, 200)
                .as(BookingResponse.class);

        // Assert that bookingRequest matches actualCreateBookingResponse
        Assertions.assertBookingMatchesResponse(bookingRequest, putResponse);
    }

    @Test(dataProvider = "bookingData")
    public void putBookingWithCheckinDateGreaterThanCheckoutDateReturnsBadParam(CreateBookingResponse createBookingResponse) {
        bookingRequest = bookingFactory.createBookingRequest(
                "Christiano",
                "Ronaldo",
                300,
                false,
                "Double Bed",
                2,
                -2);

        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .withJsonBody(convertToJson(bookingRequest))
                .put(Endpoints.BOOKING_BY_ID, 400);

    }

    @Test(dataProvider = "bookingData")
    public void putBookingMissingFirstNameReturnsBadParam(CreateBookingResponse createBookingResponse) {
        bookingRequest = bookingFactory.createBookingRequest(
                null,
                "Ronaldo",
                300,
                false,
                "Double Bed",
                2,
                -2);

        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .withJsonBody(convertToJson(bookingRequest))
                .put(Endpoints.BOOKING_BY_ID, 400);

    }

    @Test(dataProvider = "bookingData")
    public void putBookingMissingLastNameReturnsBadParam(CreateBookingResponse createBookingResponse) {
        bookingRequest = bookingFactory.createBookingRequest(
                "Christiano",
                null,
                300,
                false,
                "Double Bed",
                2,
                -2);

        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .withJsonBody(convertToJson(bookingRequest))
                .put(Endpoints.BOOKING_BY_ID, 400);

    }

    @Test(dataProvider = "bookingData")
    public void putBookingMissingTotalPriceReturnsBadParam(CreateBookingResponse createBookingResponse) {
        bookingRequest = bookingFactory.createBookingRequest(
                "Christiano",
                "Ronaldo",
                null,
                false,
                "Double Bed",
                2,
                -2);

        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .withJsonBody(convertToJson(bookingRequest))
                .put(Endpoints.BOOKING_BY_ID, 400);

    }

    @Test(dataProvider = "bookingData")
    public void putBookingInvalidBookingDatesReturnsBadParam(CreateBookingResponse createBookingResponse) {
        String checkinDateParam = "CheckinDate";
        String checkoutDateParam = "CheckoutDate";

        JSONObject bookingRequest = bookingFactory.createBookingRequestJson(
                "Christiano",
                "Ronaldo",
                1000,
                true,
                "Breakfast",
                checkinDateParam,
                checkoutDateParam
        );

        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .withJsonBody(convertToJson(bookingRequest))
                .put(Endpoints.BOOKING_BY_ID, 400);

    }

    @Test(dataProvider = "bookingData")
    public void putBookingNonExistingBookingReturns404NotFound(CreateBookingResponse createBookingResponse) {
        bookingRequest = bookingFactory.createBookingRequest(
                "Christiano",
                "Ronaldo",
                300,
                false,
                "Double Bed",
                2,
                -2);

        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, 2147483647)
                .withJsonBody(convertToJson(bookingRequest))
                .put(Endpoints.BOOKING_BY_ID, 404);

    }

    @Test(dataProvider = "bookingData")
    public void putBookingWithoutAuthReturns403(CreateBookingResponse createBookingResponse) {
        bookingRequest = bookingFactory.createBookingRequest(
                "Christiano",
                "Ronaldo",
                300,
                false,
                "Double Bed",
                2,
                -2);

        client
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .withJsonBody(convertToJson(bookingRequest))
                .put(Endpoints.BOOKING_BY_ID, 403);

    }


}
