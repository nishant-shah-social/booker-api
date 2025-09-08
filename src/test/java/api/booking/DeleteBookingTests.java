package api.booking;

import api.setup.BaseTest;
import api.setup.BookingFactory;
import config.Endpoints;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;
import utils.Auth;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static java.util.Collections.synchronizedList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteBookingTests extends BaseTest {
    Auth auth;
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
    public void deleteBookingExistingBookingIsSuccessful(CreateBookingResponse createBookingResponse) {
        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .delete(Endpoints.BOOKING_BY_ID, 200);

        // Verify that the booking is actually deleted (GET should return 404)
        client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .get("/booking/{id}", 404);
    }

    @Test
    public void deleteBookingNonExistentBookingReturns404() {
        client
                .withToken(token)
                .withPathParam(Endpoints.PARAM_ID, 2147483647)
                .delete(Endpoints.BOOKING_BY_ID, 404);
    }

    @Test(dataProvider = "bookingData")
    public void deleteBookingWithoutAuthenticationReturns403(CreateBookingResponse createBookingResponse) {
        client
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .delete(Endpoints.BOOKING_BY_ID, 403);
    }

    @Test(dataProvider = "bookingData")
    public void deleteBookingWithWrongAuthTokenReturns403(CreateBookingResponse createBookingResponse) {
        client
                .withToken("test1234")
                .withPathParam(Endpoints.PARAM_ID, createBookingResponse.getBookingid())
                .delete(Endpoints.BOOKING_BY_ID, 403);
    }

    @Test(dataProvider = "bookingData")
    public void deleteBookingExistingBookingConcurrentlySucceeds(CreateBookingResponse createBookingResponse) throws InterruptedException {
        final int bookingId = createBookingResponse.getBookingid();
        final int threads = 2;

        // setting up a race condition like situation with three concurrent threads

        CountDownLatch startGate = new CountDownLatch(1); //keeps all threads waiting until we signal them to start
        CountDownLatch doneGate = new CountDownLatch(threads);//waits until all threads are done
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Integer> statuses = synchronizedList(new ArrayList<>()); //makes it threadsafe

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    startGate.await(); //waits until we release the startgate
                    int status = given()
                            .spec(requestSpec)
                            .cookie("token", token)
                            .pathParam(Endpoints.PARAM_ID, bookingId)
                            .when()
                            .delete(Endpoints.BOOKING_BY_ID)
                            .then()
                            .extract()
                            .statusCode();
                    statuses.add(status);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneGate.countDown();
                }
            });
        }

        // Start all threads at once
        startGate.countDown();
        doneGate.await();
        pool.shutdownNow();

        long successCount = statuses.stream().filter(s -> s == 201).count(); //assuming 201 is correct respons code for this test case to run successfully.
        long notFoundCount = statuses.stream().filter(s -> s == 404 || s == 405).count();
        long otherCount = statuses.size() - successCount - notFoundCount;

        assertThat("Exactly one DELETE should succeed", successCount, is(1L));
        assertThat("All remaining deletes should report not found", notFoundCount, is((long) (threads - 1)));
        assertThat("No unexpected status codes", otherCount, is(0L));

        // Final verification that the booking is gone
        client
                .withToken(token)
                .withPathParam("id", bookingId)
                .get("/booking/{id}", 404);
    }

    @Test(dataProvider = "bookingData")
    public void deleteBookingWithoutBookingIdReturns404(CreateBookingResponse createBookingResponse) {
        client
                .delete("/booking", 404);
    }


}
