package api.booking;

import api.setup.BaseTest;
import api.setup.BookingFactory;
import config.Endpoints;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static utils.DateUtil.adjustDate;

public class GetBookingTests extends BaseTest {
    private final List<CreateBookingResponse> createdBookings = new ArrayList<>();

    @BeforeClass
    public void setupBookings() throws IOException {
        List<BookingRequest> bookingRequests =
                BookingFactory.loadBookingRequests("src/test/resources/bookingData.json");

        for (BookingRequest request : bookingRequests) {
            CreateBookingResponse response = bookingFactory.createBooking(request);
            createdBookings.add(response);
        }
    }

    @Test
    public void getBookingsNoFiltersReturnsArrayWithIds() {
        List<Integer> expectedBookingIds = createdBookings.stream()
                                                          .map(CreateBookingResponse::getBookingid)
                                                          .toList();

        List<Integer> bookingIds = client
                .get(Endpoints.BOOKING_BASE, 200)
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat(
                "Expected booking id list to contain " + expectedBookingIds,
                bookingIds,
                hasItems(expectedBookingIds.toArray(new Integer[0]))
        );
    }

    @Test
    public void getBookingsWithFiltersFirstNameReturnsCorrectBookingIds() {
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam("firstname", createdBookings.get(0).getBooking().getFirstname())
                .get(Endpoints.BOOKING_BASE, 200)
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookingsWithFiltersLastNameReturnsCorrectBookingIds() {
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam(Endpoints.PARAM_LASTNAME, createdBookings.get(0).getBooking().getLastname())
                .get(Endpoints.BOOKING_BASE, 200)
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookingsWithFiltersCheckinDateGreaterThanProvidedDateReturnsCorrectBookingIds() {
        String checkinDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckin(),
                -5,
                DateTimeFormatter.ISO_LOCAL_DATE);
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam(Endpoints.PARAM_CHECKIN, checkinDateParam)
                .get(Endpoints.BOOKING_BASE, 200)
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookingsWithFiltersCheckoutDateLessThanProvidedDateReturnsCorrectBookingIds() {
        String checkoutDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckout(),
                5,
                DateTimeFormatter.ISO_LOCAL_DATE);
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam(Endpoints.PARAM_CHECKOUT, checkoutDateParam)
                .get(Endpoints.BOOKING_BASE, 200)
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookingsFiltersWithProvidedDateLessThanCheckinAndGreaterThanCheckoutReturnsCorrectBookingIds() {
        String checkinDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckin(),
                -5,
                DateTimeFormatter.ISO_LOCAL_DATE);
        String checkoutDateParam = adjustDate(
                createdBookings.get(1).getBooking().getBookingdates().getCheckout(),
                0,
                DateTimeFormatter.ISO_LOCAL_DATE);
        List<Integer> expectedBookingIds = createdBookings.stream()
                                                          .map(CreateBookingResponse::getBookingid)
                                                          .limit(2)
                                                          .toList();

        List<Integer> bookingIds = client
                .withQueryParam(Endpoints.PARAM_CHECKIN, checkinDateParam)
                .withQueryParam(Endpoints.PARAM_CHECKOUT, checkoutDateParam)
                .get(Endpoints.BOOKING_BASE, 200)
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat(
                "Expected booking id list to contain " + expectedBookingIds,
                bookingIds,
                hasItems(expectedBookingIds.toArray(new Integer[0]))
        );
    }

    @Test
    public void getBookingsFiltersWithCheckoutDateEqualToProvidedDateReturnsCorrectBookingIds() {
        String checkoutDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckout(),
                0,
                DateTimeFormatter.ISO_LOCAL_DATE);
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam(Endpoints.PARAM_CHECKOUT, checkoutDateParam)
                .get(Endpoints.BOOKING_BASE, 200)
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookingsFiltersWithCheckinDateEqualToProvidedDateReturnsCorrectBookingIds() {
        String checkinDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckin(),
                0,
                DateTimeFormatter.ISO_LOCAL_DATE);
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam(Endpoints.PARAM_CHECKIN, checkinDateParam)
                .get(Endpoints.BOOKING_BASE, 200)
                .jsonPath()
                .getList("bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookingsFiltersWithCheckinDateIncorrectFormatReturnBadParams() {
        String checkinDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckin(),
                -1,
                DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.US)
        );

        client.withQueryParam(Endpoints.PARAM_CHECKIN, checkinDateParam)
              .get(Endpoints.BOOKING_BASE, 400);
    }

    @Test
    public void getBookingsFiltersCheckinDateIncorrectTypeReturnBadParams() {
        client.withQueryParam(Endpoints.PARAM_CHECKIN, 222)
              .get(Endpoints.BOOKING_BASE, 400);
    }

    @Test
    public void getBookingsFiltersNotMatchingReturnsEmptyList() {
        client.withQueryParam(Endpoints.PARAM_FIRSTNAME, UUID.randomUUID().toString())
              .get(Endpoints.BOOKING_BASE, 200)
              .then()
              .body("", empty());
    }

    @Test
    public void getBookingsFiltersSqlInjectionTruthyReturnsEmptyList() {
        client.withQueryParam(Endpoints.PARAM_FIRSTNAME, "nishant' OR '1'='1")
              .get(Endpoints.BOOKING_BASE, 200)
              .then()
              .body("", empty());
    }

    @Test
    public void getBookingsFiltersSqlInjectionUnionReturnsEmptyList() {
        client.withQueryParam(Endpoints.PARAM_FIRSTNAME, "nishant' UNION Select username, password from users--")
              .get(Endpoints.BOOKING_BASE, 200)
              .then()
              .body("", empty());
    }
}
