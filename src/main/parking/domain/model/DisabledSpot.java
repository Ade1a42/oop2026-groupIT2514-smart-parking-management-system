package parking.domain.model;

public class DisabledSpot extends ParkingSpot {
    private boolean hasWheelchairAccess = true;

    public DisabledSpot(String spotNumber, String zone) {
        super(spotNumber, "DISABLED", zone);
    }


    public boolean checkPermit(String permitNumber) {
        return permitNumber != null && permitNumber.startsWith("DIS");
    }

    @Override
    public String toString() {
        return super.toString() + " [Wheelchair Accessible]";
    }
}