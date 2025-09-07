package api.setup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import pojo.BookingDates;
import pojo.BookingRequest;
import pojo.BookingResponse;
import pojo.CreateBookingResponse;
import utils.RestClient;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static utils.DateUtil.adjustDateToLocalDate;
import static utils.Generic.convertToJson;

public class BookingFactory {
    private static final Logger LOGGER = LogManager.getLogger(BookingFactory.class);
    private final RestClient restClient;

    // Constructor to inject RestClient dependency
    public BookingFactory(RestClient restClient) {
        this.restClient = restClient;
    }

    public static List<BookingRequest> loadBookingRequests(String filePath) throws IOException {
        LOGGER.info("Loading booking requests from file: {}", filePath);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // check-in/check-out fields are of type date
        List<BookingRequest> requests = mapper.readValue(new File(filePath), new TypeReference<>() {
        });
        LOGGER.info("Loaded {} booking request(s) from file", requests.size());
        return requests;
    }

    public CreateBookingResponse createBooking(BookingRequest bookingRequest) {
        long start = System.currentTimeMillis();
        LOGGER.info("POST /booking (create booking)");
        try {
            JSONObject bookingRequestJson = convertToJson(bookingRequest);
            CreateBookingResponse response = restClient
                    .withJsonBody(bookingRequestJson)
                    .post("/booking", 200)
                    .as(CreateBookingResponse.class);
            long duration = System.currentTimeMillis() - start;
            LOGGER.info("POST /booking -> {} ({} ms)", response, duration);
            return response;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - start;
            LOGGER.error("POST /booking failed ({} ms)", duration, e);
            throw e;
        }
    }

    public BookingRequest createBookingRequest(String firstname, String lastname, Integer totalprice, Boolean depositpaid,
                                               String additionalneeds, Integer checkinOffset, Integer checkoutOffset) {
        BookingRequest bookingRequest = new BookingRequest();

        if (firstname != null) {
            bookingRequest.setFirstname(firstname);
        }
        if (lastname != null) {
            bookingRequest.setLastname(lastname);
        }
        if (totalprice != null) {
            bookingRequest.setTotalprice(totalprice);
        }
        if (depositpaid != null) {
            bookingRequest.setDepositpaid(depositpaid);
        }
        if (additionalneeds != null) {
            bookingRequest.setAdditionalneeds(additionalneeds);
        }

        BookingDates bookingDates = new BookingDates();
        if (checkinOffset != null) {
            bookingDates.setCheckin(adjustDateToLocalDate(LocalDate.now(), checkinOffset));
        }
        if (checkoutOffset != null) {
            bookingDates.setCheckout(adjustDateToLocalDate(LocalDate.now(), checkoutOffset));
        }

        if (checkinOffset != null || checkoutOffset != null) {
            bookingRequest.setBookingdates(bookingDates);
        }

        return bookingRequest;
    }

    // initializes the JSON object for BookingRequest. Needed for cases when POJO is not required
    public JSONObject createBookingRequestJson(Object firstname, Object lastname, Object totalprice, Object depositpaid,
                                               Object additionalneeds, Object checkinDate, Object checkoutDate) {
        JSONObject bookingRequest = new JSONObject();
        JSONObject bookingDates = new JSONObject();
        if (checkinDate != null) {
            bookingDates.put("checkin", checkinDate);
        }
        if (checkoutDate != null) {
            bookingDates.put("checkout", checkoutDate);
        }
        bookingRequest.put("bookingdates", bookingDates);
        if (firstname != null) {
            bookingRequest.put("firstname", firstname);
        }
        if (lastname != null) {
            bookingRequest.put("lastname", lastname);
        }
        if (totalprice != null) {
            bookingRequest.put("totalprice", totalprice);
        }
        if (depositpaid != null) {
            bookingRequest.put("depositpaid", depositpaid);
        }
        if (additionalneeds != null) {
            bookingRequest.put("additionalneeds", additionalneeds);
        }
        return bookingRequest;
    }


    public BookingResponse convertCreateBookingResponseToBookingResponse(CreateBookingResponse createBookingResponse) {
        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setFirstname(createBookingResponse.getBooking().getFirstname());
        bookingResponse.setLastname(createBookingResponse.getBooking().getLastname());
        bookingResponse.setTotalprice(createBookingResponse.getBooking().getTotalprice());
        bookingResponse.setDepositpaid(createBookingResponse.getBooking().isDepositpaid());
        bookingResponse.setBookingdates(createBookingResponse.getBooking().getBookingdates());
        bookingResponse.setAdditionalneeds(createBookingResponse.getBooking().getAdditionalneeds());
        bookingResponse.setBookingid(createBookingResponse.getBookingid());
        return bookingResponse;
    }

}
