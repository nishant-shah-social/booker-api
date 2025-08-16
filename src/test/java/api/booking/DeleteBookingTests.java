package api.booking;

import api.setup.BaseTest;
import api.setup.BookingFactory;
import config.Constants;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pojo.BookingRequest;
import pojo.CreateBookingResponse;
import pojo.LoginRequest;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static java.util.Collections.synchronizedList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static utils.Auth.login;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DeleteBookingTests extends BaseTest {
    private String token = "";

    @BeforeClass
    public void setupAuth() {
        LoginRequest loginRequest = new LoginRequest(Constants.USERNAME, Constants.PASSWORD);
        token = login(loginRequest, requestSpec, responseSpec);
    }

    @DataProvider(name = "bookingData")
    public Object[][] setupBookings() throws IOException {
        BookingRequest bookingRequests =
                BookingFactory.loadBookingRequests("src/test/resources/bookingData.json")
                              .get(0);
        CreateBookingResponse response = BookingFactory.createBooking(bookingRequests, requestSpec, responseSpec);
        return new Object[][]{
                {response}
        };
    }

    @Test(dataProvider = "bookingData")
    public void deleteExistingBooking(CreateBookingResponse createBookingResponse) {
        client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .delete("/booking/{id}");

        // Verify that the booking is actually deleted (GET should return 404)
        client
                .withToken(token)
                .withPathParam("id", createBookingResponse.getBookingid())
                .get("/booking/{id}", 404);
    }

    @Test
    public void deleteNonExistentBooking() {
        client
                .withToken(token)
                .withPathParam("id", 2147483647)
                .delete("/booking/{id}", 404);
    }

    @Test(dataProvider = "bookingData")
    public void deleteWithoutAuthentication_returns403(CreateBookingResponse createBookingResponse) {
        client
                .withPathParam("id", createBookingResponse.getBookingid())
                .delete("/booking/{id}", 403);
    }

    @Test(dataProvider = "bookingData")
    public void deleteWithWrongAuthToken_returns403(CreateBookingResponse createBookingResponse) {
        client
                .withToken("test1234")
                .withPathParam("id", createBookingResponse.getBookingid())
                .delete("/booking/{id}", 403);
    }

    @Test(dataProvider = "bookingData")
    public void deleteExistingBooking_concurrent(CreateBookingResponse createBookingResponse) throws InterruptedException {
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
                            .pathParam("id", bookingId)
                            .when()
                            .delete("/booking/{id}")
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

        long successCount = statuses.stream().filter(s -> s == 201).count();
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
    public void deleteWithoutBookingId(CreateBookingResponse createBookingResponse) {
        client
                .delete("/booking", 404);
    }


}
