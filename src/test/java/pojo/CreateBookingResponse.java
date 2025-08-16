package pojo;

public class CreateBookingResponse {
    private BookingRequest booking;
    private int bookingid;

    public BookingRequest getBooking() {
        return booking;
    }

    public void setBooking(BookingRequest booking) {
        this.booking = booking;
    }

    public int getBookingid() {
        return bookingid;
    }

    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }
}
