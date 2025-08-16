package api.booking;

import api.setup.BaseTest;
import api.setup.BookingFactory;
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
            CreateBookingResponse response = BookingFactory.createBooking(request, requestSpec, responseSpec);
            createdBookings.add(response);
        }
    }

    @Test
    public void getBookings_noFilters_returnsArrayWithValidIds() {
        List<Integer> expectedBookingIds = createdBookings.stream()
                                                          .map(CreateBookingResponse::getBookingid)
                                                          .toList();

        List<Integer> bookingIds = client
                .getList("/booking", "bookingid", Integer.class);

        assertThat(
                "Expected booking id list to contain " + expectedBookingIds,
                bookingIds,
                hasItems(expectedBookingIds.toArray(new Integer[0]))
        );
    }

    @Test
    public void getBookings_filters_firstName() {
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam("firstname", createdBookings.get(0).getBooking().getFirstname())
                .getList("/booking", "bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookings_filters_lastName() {
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam("lastname", createdBookings.get(0).getBooking().getLastname())
                .getList("/booking", "bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookings_filters_checkinDate_greater_than_providedDate() {
        String checkinDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckin(),
                -5,
                DateTimeFormatter.ISO_LOCAL_DATE);
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam("checkin", checkinDateParam)
                .getList("/booking", "bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookings_filters_checkoutDate_less_than_provided_date() {
        String checkoutDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckout(),
                5,
                DateTimeFormatter.ISO_LOCAL_DATE);
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam("checkout", checkoutDateParam)
                .getList("/booking", "bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookings_filters_checkin_less_checkout_greater() {
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
                .withQueryParam("checkin", checkinDateParam)
                .withQueryParam("checkout", checkoutDateParam)
                .getList("/booking", "bookingid", Integer.class);

        assertThat(
                "Expected booking id list to contain " + expectedBookingIds,
                bookingIds,
                hasItems(expectedBookingIds.toArray(new Integer[0]))
        );
    }

    @Test
    public void getBookings_filters_checkoutDate_equal_to_provided_date() {
        String checkoutDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckout(),
                0,
                DateTimeFormatter.ISO_LOCAL_DATE);
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam("checkout", checkoutDateParam)
                .getList("/booking", "bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookings_filters_checkinDate_equal_to_providedDate() {
        String checkinDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckin(),
                0,
                DateTimeFormatter.ISO_LOCAL_DATE);
        int expectedBookingId = createdBookings.get(0).getBookingid();

        List<Integer> bookingIds = client
                .withQueryParam("checkin", checkinDateParam)
                .getList("/booking", "bookingid", Integer.class);

        assertThat("Expected bookingId list to contain " + expectedBookingId,
                bookingIds, hasItem(expectedBookingId));
    }

    @Test
    public void getBookings_filters_checkinDate_incorrect_format_return_400_status() {
        String checkinDateParam = adjustDate(
                createdBookings.get(0).getBooking().getBookingdates().getCheckin(),
                -1,
                DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.US)
        );

        client.withQueryParam("checkin", checkinDateParam)
              .get("/booking", 400);
    }

    @Test
    public void getBookings_filters_checkinDate_incorrectType_return_400_status() {
        client.withQueryParam("checkin", 222)
              .get("/booking", 400);
    }

    @Test
    public void getBookings_filters_return_empty_result() {
        client.withQueryParam("firstname", UUID.randomUUID().toString())
              .get("/booking")
              .then()
              .body("", empty());
    }

    @Test
    public void getBookings_filters_SQLInjection_truthy() {
        client.withQueryParam("firstname", "nishant' OR '1'='1")
              .get("/booking")
              .then()
              .body("", empty());
    }

    @Test
    public void getBookings_filters_SQLInjection_union() {
        client.withQueryParam("firstname", "nishant' UNION Select username, password from users--")
              .get("/booking")
              .then()
              .body("", empty());
    }
}
