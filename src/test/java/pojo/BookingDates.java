package pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class BookingDates {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate checkin;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate checkout;

    public LocalDate getCheckin() {
        return checkin;
    }

    public LocalDate getCheckout() {
        return checkout;
    }

    public void setCheckin(LocalDate checkin) {
        this.checkin = checkin;
    }

    public void setCheckout(LocalDate checkout) {
        this.checkout = checkout;
    }
}
