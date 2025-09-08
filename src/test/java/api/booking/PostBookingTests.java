package api.booking;

import api.setup.BaseTest;
import config.Endpoints;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.Test;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;
import utils.Assertions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static utils.DateUtil.adjustDate;
import static utils.Generic.convertToJson;

public class PostBookingTests extends BaseTest {

    BookingRequest bookingRequest;

    @Test
    public void createBookingWithValidParamsIsSuccessful() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                100,
                true,
                "Breakfast",
                -5,
                5);

        CreateBookingResponse actualCreateBookingResponse = client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 200)
                .as(CreateBookingResponse.class);

        // Assert that bookingRequest matches actualCreateBookingResponse
        Assertions.assertBookingMatchesResponse(
                bookingRequest,
                bookingFactory.convertCreateBookingResponseToBookingResponse(actualCreateBookingResponse));
    }

    @Test
    public void createBookingWithoutFirstNameReturnsBadParams() {
        bookingRequest = bookingFactory.createBookingRequest(
                null,
                "Doe",
                100,
                true,
                "Breakfast",
                -5,
                5);

        client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 400);
    }

    @Test
    public void createBookingWithoutLastNameReturnsBadParams() {
        bookingRequest = bookingFactory.createBookingRequest("John", null, 100, true, "Breakfast", -5, 5);

        client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 400);
    }

    @Test
    public void createBookingWithoutTotalPriceReturnBadParams() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                null,
                true,
                "Breakfast",
                -5,
                5);

        client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 400);
    }

    @Test
    public void createBookingWithoutcheckoutDateReturnBadParams() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                100,
                true,
                "Breakfast",
                -5,
                null);

        client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 400);
    }

    @Test
    public void createBookingWithoutcheckinDateReturnBadParams() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                100,
                true,
                "Breakfast",
                null,
                5);

        client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 400);
    }

    @Test
    public void createBookingWithoutAdditionalNeedsIsSuccessful() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                100,
                true,
                null,
                -5,
                5);

        CreateBookingResponse actualCreateBookingResponse = client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 200)
                .as(CreateBookingResponse.class);

        // Assert that bookingRequest matches actualCreateBookingResponse
        Assertions.assertBookingMatchesResponse(
                bookingRequest,
                bookingFactory.convertCreateBookingResponseToBookingResponse(actualCreateBookingResponse));
    }

    @Test
    public void createBookingWithCheckinDateGreaterThanCheckoutDateReturnsBadParams() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                100,
                true,
                "Breakfast",
                5,
                -5);

        Response resp = client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingWithCheckinDateSameAsCheckoutDateReturnsBadParams() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                100,
                true,
                "Breakfast",
                5,
                5);

        Response resp = client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingWithInvalidTypeInFirstNameReturnsBadParams() {
        String checkinDateParam =
                adjustDate(
                        LocalDate.now(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam =
                adjustDate(
                        LocalDate.now(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);

        JSONObject createBookingRequest = bookingFactory.createBookingRequestJson(
                1234,
                "Doe",
                100,
                true,
                "Breakfast",
                checkinDateParam,
                checkoutDateParam
        );

        Response resp = client
                .withJsonBody(createBookingRequest)
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingWithInvalidTypeInLastNameReturnsBadParams() {
        String checkinDateParam =
                adjustDate(
                        LocalDate.now(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam =
                adjustDate(
                        LocalDate.now(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);

        JSONObject createBookingRequest = bookingFactory.createBookingRequestJson(
                "John",
                1234,
                100,
                true,
                "Breakfast",
                checkinDateParam,
                checkoutDateParam
        );

        Response resp = client
                .withJsonBody(createBookingRequest)
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingWithInvalidTypeInTotalPriceReturnsBadParams() {
        String checkinDateParam =
                adjustDate(
                        LocalDate.now(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam =
                adjustDate(
                        LocalDate.now(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);

        JSONObject createBookingRequest = bookingFactory.createBookingRequestJson(
                "John",
                "Doe",
                "555",
                true,
                "Breakfast",
                checkinDateParam,
                checkoutDateParam
        );

        Response resp = client
                .withJsonBody(createBookingRequest)
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingWithInvalidTypeInCheckinCheckoutDatesReturnsBadParams() {
        JSONObject createBookingRequest = bookingFactory.createBookingRequestJson(
                "John",
                "Doe",
                555,
                true,
                "Breakfast",
                "checkinDate",
                "checkoutDate"
        );

        Response resp = client
                .withJsonBody(createBookingRequest)
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingWithInvalidTypeInDepositPaidReturnsBadParams() {
        String checkinDateParam =
                adjustDate(
                        LocalDate.now(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam =
                adjustDate(
                        LocalDate.now(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);

        JSONObject createBookingRequest = bookingFactory.createBookingRequestJson(
                "John",
                "Doe",
                555,
                "I dont know",
                "Breakfast",
                checkinDateParam,
                checkoutDateParam
        );

        Response resp = client
                .withJsonBody(createBookingRequest)
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingWithIncorrectFormatOfCheckinCheckoutDatesReturnsBadParams() {
        String checkinDateParam =
                adjustDate(
                        LocalDate.now(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam =
                adjustDate(
                        LocalDate.now(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);

        JSONObject createBookingRequest = bookingFactory.createBookingRequestJson(
                "John",
                "Doe",
                555,
                true,
                "Breakfast",
                checkinDateParam,
                checkoutDateParam
        );

        Response resp = client
                .withJsonBody(createBookingRequest)
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingTotalPriceBoundaryJustBelowIntegerMaxValueIsSuccess() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                Integer.MAX_VALUE - 1,
                true,
                "Breakfast",
                5,
                5);


        CreateBookingResponse actualCreateBookingResponse = client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 200)
                .as(CreateBookingResponse.class);

        // Assert that bookingRequest matches actualCreateBookingResponse
        Assertions.assertBookingMatchesResponse(bookingRequest,
                bookingFactory.convertCreateBookingResponseToBookingResponse(actualCreateBookingResponse));

    }

    @Test
    public void createBookingTotalPriceBoundaryJustAboveIntegerMaxValueReturnsBadParams() {
        String checkinDateParam =
                adjustDate(
                        LocalDate.now(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam =
                adjustDate(
                        LocalDate.now(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        JSONObject createBookingRequest = bookingFactory.createBookingRequestJson(
                "John",
                "Doe",
                2147483648L,
                true,
                "Breakfast",
                checkinDateParam,
                checkoutDateParam
        );

        Response resp = client
                .withJsonBody(createBookingRequest)
                .post(Endpoints.BOOKING_BASE, 400);

    }

    @Test
    public void createBookingTotalPriceBoundarySameAsIntegerMaxValueIsSuccess() {
        bookingRequest = bookingFactory.createBookingRequest(
                "John",
                "Doe",
                Integer.MAX_VALUE,
                true,
                "Breakfast",
                -5,
                5);

        CreateBookingResponse actualCreateBookingResponse = client
                .withJsonBody(convertToJson(bookingRequest))
                .post(Endpoints.BOOKING_BASE, 200)
                .as(CreateBookingResponse.class);

        // Assert that bookingRequest matches actualCreateBookingResponse
        Assertions.assertBookingMatchesResponse(bookingRequest,
                bookingFactory.convertCreateBookingResponseToBookingResponse(actualCreateBookingResponse));
    }

    @Test
    public void createBookingNonExistingFieldReturnsBadParams() {
        String checkinDateParam =
                adjustDate(
                        LocalDate.now(),
                        -5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam =
                adjustDate(
                        LocalDate.now(),
                        5,
                        DateTimeFormatter.ISO_LOCAL_DATE);
        JSONObject createBookingRequest = bookingFactory.createBookingRequestJson(
                "John",
                "Doe",
                2147483648L,
                true,
                "Breakfast",
                checkinDateParam,
                checkoutDateParam
        );
        createBookingRequest.put("address", "Rotterdam"); //non-existing field

        Response resp = client
                .withJsonBody(createBookingRequest)
                .post(Endpoints.BOOKING_BASE, 400);

    }


}
