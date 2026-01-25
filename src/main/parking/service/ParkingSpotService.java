package parking.service;

import parking.repository.IParkingSpotRepository;
import parking.model.ParkingSpot;
import parking.exception.NoFreeSpots;
import java.util.List;

public class ParkingSpotService {
    private final IParkingSpotRepository repository;

    public ParkingSpotService(IParkingSpotRepository repository) {
        this.repository = repository;
    }

    public ParkingSpot addParkingSpot(String spotNumber, String type, String zone) {
        ParkingSpot spot = new ParkingSpot(spotNumber, type, zone);
        return repository.create(spot);
    }

    public List<ParkingSpot> getAllSpots() {
        return repository.findAll();
    }

    public List<ParkingSpot> getAvailableSpots() throws NoFreeSpots {
        List<ParkingSpot> availableSpots = repository.findByStatus("AVAILABLE");

        if (availableSpots.isEmpty()) {
            throw new NoFreeSpots("No available parking spots");
        }

        return availableSpots;
    }

    public ParkingSpot getSpotById(int id) {
        return repository.findById(id);
    }

}