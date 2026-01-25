package parking.repository;

import parking.model.ParkingSpot;
import java.util.List;

public interface IParkingSpotRepository {
    ParkingSpot create(ParkingSpot spot);
    ParkingSpot findById(int id);
    List<ParkingSpot> findAll();
    List<ParkingSpot> findByStatus(String status);
    ParkingSpot update(ParkingSpot spot);
    boolean delete(int id);
}