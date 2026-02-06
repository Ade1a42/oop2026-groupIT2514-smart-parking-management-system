package parking.factory;

import parking.model.*;

public class ParkingSpotFactory {
    public static ParkingSpot createSpot(String spotType, String spotNumber, String zone) {
        switch(spotType.toUpperCase()) {
            case "ELECTRIC":
                return new ElectricSpot(spotNumber, zone);
            case "DISABLED":
                return new DisabledSpot(spotNumber, zone);
            case "STANDARD":
            default:
                return new StandardSpot(spotNumber, zone);
        }
    }


    public static ParkingSpot createSpot(int id, String spotNumber, String type, String status, String zone) {
        ParkingSpot spot = createSpot(type, spotNumber, zone);
        spot.setId(id);
        spot.setStatus(status);
        return spot;
    }
}
