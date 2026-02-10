package parking.domain.model;

public class StandardSpot extends ParkingSpot {
    public StandardSpot(String spotNumber, String zone) {
        super(spotNumber, "STANDARD", zone);
    }
}
