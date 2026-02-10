package parking.domain.model;

public class ElectricSpot extends ParkingSpot {
    private double chargingRatePerHour;

    public ElectricSpot(String spotNumber, String zone) {
        super(spotNumber, "ELECTRIC", zone);
        this.chargingRatePerHour = 2.0; // $2/hour for charging
    }


    public double calculateChargingCost(int hours) {
        return hours * chargingRatePerHour;
    }

    @Override
    public String toString() {
        return super.toString() + " [EV Charger Available]";
    }
}