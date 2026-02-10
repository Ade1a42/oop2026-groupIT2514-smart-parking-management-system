package parking.domain.exception;

public class NoFreeSpots extends Exception {
    public NoFreeSpots(String message) {
        super(message);
    }
}
