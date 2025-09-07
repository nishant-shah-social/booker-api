package utils;

import pojo.BookingRequest;
import pojo.BookingResponse;
import pojo.CreateBookingResponse;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class Assertions {
    public static void assertObjects(Object original, Object updated, String... fieldsToSkip) {
        List<String> skipFields = Arrays.asList(fieldsToSkip);

        for (Field field : original.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (!skipFields.contains(field.getName())) {
                try {
                    Object originalValue = field.get(original);
                    Object updatedValue = field.get(updated);

                    assertThat(
                            "Field '" + field.getName() + "' should remain unchanged",
                            updatedValue,
                            is(originalValue)
                    );

                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to access field: " + field.getName(), e);
                }
            }
        }
    }

    public static void assertBookingMatchesResponse(BookingRequest bookingRequest, BookingResponse actualCreateBookingResponse) {
        assertThat("Firstname should match", bookingRequest.getFirstname(), is(actualCreateBookingResponse.getFirstname()));
        assertThat("Lastname should match", bookingRequest.getLastname(), is(actualCreateBookingResponse.getLastname()));
        assertThat("Totalprice should match", bookingRequest.getTotalprice(), is(actualCreateBookingResponse.getTotalprice()));
        assertThat("Depositpaid should match", bookingRequest.isDepositpaid(), is(actualCreateBookingResponse.isDepositpaid()));
        assertThat("Checkin date should match", bookingRequest.getBookingdates().getCheckin(), is(actualCreateBookingResponse.getBookingdates().getCheckin()));
        assertThat("Checkout date should match", bookingRequest.getBookingdates().getCheckout(), is(actualCreateBookingResponse.getBookingdates().getCheckout()));
        assertThat("Additionalneeds should match", bookingRequest.getAdditionalneeds(), is(actualCreateBookingResponse.getAdditionalneeds()));
    }
}
