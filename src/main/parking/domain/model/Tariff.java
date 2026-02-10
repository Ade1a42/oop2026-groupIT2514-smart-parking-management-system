package parking.domain.model;

public class Tariff {
    private int id;
    private String spotType;
    private double hourlyRate;

    public Tariff() {}
    public Tariff(String spotType, double hourlyRate) {
        this.spotType = spotType;
        this.hourlyRate = hourlyRate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getSpotType() { return spotType; }
    public void setSpotType(String spotType) { this.spotType = spotType; }
    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }
}