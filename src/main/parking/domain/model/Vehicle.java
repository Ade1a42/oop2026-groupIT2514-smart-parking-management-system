package parking.domain.model;

public class Vehicle {
    private int id;
    private String plateNumber;
    private String ownerName;

    public Vehicle() {}
    public Vehicle(String plateNumber, String ownerName) {
        this.plateNumber = plateNumber;
        this.ownerName = ownerName;
    }
    public Vehicle(int id, String plateNumber, String ownerName) {
        this.id = id;
        this.plateNumber = plateNumber;
        this.ownerName = ownerName;
    }




    // setter and getters
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
