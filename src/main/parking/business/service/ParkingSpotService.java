package parking.business.service;

import parking.domain.repository.IParkingSpotRepository;
import parking.domain.model.ParkingSpot;
import parking.domain.exception.NoFreeSpots;
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

    public ParkingSpot updateSpot(ParkingSpot spot) {
        return repository.update(spot);
    }

    public boolean deleteSpot(int id) {
        return repository.delete(id);
    }
}