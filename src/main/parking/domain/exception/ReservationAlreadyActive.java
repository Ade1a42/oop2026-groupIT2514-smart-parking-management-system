package parking.domain.exception;

public class ReservationAlreadyActive extends Exception {
    public ReservationAlreadyActive(String message) {
        super(message);
    }
}
