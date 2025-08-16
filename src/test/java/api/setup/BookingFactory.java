package api.setup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BookingFactory {
    public static List<BookingRequest> loadBookingRequests(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // check-in/check-out fields are of type date
        return mapper.readValue(new File(filePath), new TypeReference<>() {
        });
    }

    public static CreateBookingResponse createBooking(BookingRequest bookingRequest,
                                                      RequestSpecification requestSpec,
                                                      ResponseSpecification responseSpec) {
        return RestAssured.given()
                          .spec(requestSpec)
                          .body(bookingRequest)
                          .post("/booking")
                          .then()
                          .spec(responseSpec)
                          .statusCode(200)
                          .extract()
                          .as(CreateBookingResponse.class);
    }
}
