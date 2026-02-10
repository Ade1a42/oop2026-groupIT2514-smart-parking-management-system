package parking.domain.model;

public class ParkingSpot {
    private int id;
    private String spotNumber;
    private String type;   // standart, electric, disabled
    private String status;  // available, reserved, occupied
    private String zone;   // zone_A, zone_B

    // constructors ( for java and database )
    public ParkingSpot() {}
    public ParkingSpot(String spotNumber, String type, String zone) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.status = "AVAILABLE";
        this.zone = zone;
    }
    public ParkingSpot(int id, String spotNumber, String type, String status, String zone) {
        this.id = id;
        this.spotNumber = spotNumber;
        this.type = type;
        this.status = status;
        this.zone = zone;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSpotNumber(String spotNumber){
        this.spotNumber = spotNumber;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getSpotNumber(){
        return spotNumber;
    }

    public String getType(){
        return type;
    }

    public String getStatus(){
        return status;
    }

    public String getZone(){
        return zone;
    }

    @Override
    public String toString() {
        return "ParkingSpot [id=" + id + ", number=" + spotNumber +
                ", type=" + type + ", status=" + status + ", zone=" + zone + "]";
    }
}
