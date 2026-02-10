package parking.domain.exception;

public class ReservationExpired extends Exception {
    public ReservationExpired(String message) {
        super(message);
    }
}
